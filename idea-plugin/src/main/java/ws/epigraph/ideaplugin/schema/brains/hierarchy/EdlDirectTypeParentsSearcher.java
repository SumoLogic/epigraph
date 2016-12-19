/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import ws.epigraph.ideaplugin.schema.brains.VirtualFileUtil;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.EdlDirectTypeParentsSearch.SearchParameters;
import ws.epigraph.ideaplugin.schema.index.EdlIndexUtil;
import ws.epigraph.ideaplugin.schema.index.EdlSearchScopeUtil;
import ws.epigraph.schema.parser.psi.EdlRecordTypeDef;
import ws.epigraph.schema.parser.psi.EdlSupplementDef;
import ws.epigraph.schema.parser.psi.EdlTypeDef;
import ws.epigraph.schema.parser.psi.EdlVarTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlDirectTypeParentsSearcher implements QueryExecutor<EdlTypeDef, SearchParameters> {
  @Override
  public boolean execute(@NotNull SearchParameters queryParameters, @NotNull Processor<EdlTypeDef> consumer) {

    final EdlTypeDef target = queryParameters.edlTypeDef;
    final Project project = PsiUtilCore.getProjectInReadAction(target);
    final VirtualFile targetVirtualFile = VirtualFileUtil.getOriginalVirtualFile(target.getContainingFile());
    if (targetVirtualFile == null) return true;

    final Application application = ApplicationManager.getApplication();

    final List<EdlTypeDef> parents = new ArrayList<>();

    if (queryParameters.includeExtends) {
      application.runReadAction(() -> {
        parents.addAll(target.extendsParents());
      });
    }

    // -- process 'supplements'

    if (queryParameters.includeSupplements) {

      List<EdlTypeDef> candidates = application.runReadAction(
          (Computable<List<EdlTypeDef>>) () -> EdlIndexUtil.findTypeDefs(project, null, null, GlobalSearchScope.allScope(project))
      );

      for (EdlTypeDef candidate : candidates) {
        ProgressManager.checkCanceled();

        application.runReadAction(() -> {

          // supplemented type must be in the candidate's search scope
          final GlobalSearchScope candidateScope = EdlSearchScopeUtil.getSearchScope(candidate);
          if (candidateScope.contains(targetVirtualFile)) {

            List<EdlTypeDef> supplementedList = null;
            if (candidate instanceof EdlRecordTypeDef) {
              EdlRecordTypeDef recordTypeDef = (EdlRecordTypeDef) candidate;
              supplementedList = recordTypeDef.supplemented();
            } else if (candidate instanceof EdlVarTypeDef) {
              EdlVarTypeDef varTypeDef = (EdlVarTypeDef) candidate;
              supplementedList = varTypeDef.supplemented();
            }

            if (supplementedList != null && supplementedList.stream().anyMatch(target::equals))
              parents.add(candidate);

//            parents.addAll(
//                supplementedList.stream()
//                    .filter(target::equals)
//                    .map(candidateChild -> candidate)
//                    .collect(Collectors.toList()));
          }
        });
      }
    }

    // -- process 'supplement x with y'

    if (queryParameters.includeStandaloneSupplements) {
      application.runReadAction(() -> {
        List<EdlSupplementDef> supplements = EdlIndexUtil.findSupplementsBySupplemented(project, target);
        parents.addAll(supplements.stream()
            .map(EdlSupplementDef::source)
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
      });
    }

    parents.forEach(consumer::process);

    return true;
  }
}
