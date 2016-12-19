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
import ws.epigraph.ideaplugin.schema.brains.hierarchy.SchemaDirectTypeParentsSearch.SearchParameters;
import ws.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import ws.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import ws.epigraph.schema.parser.psi.SchemaRecordTypeDef;
import ws.epigraph.schema.parser.psi.SchemaSupplementDef;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import ws.epigraph.schema.parser.psi.SchemaVarTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeParentsSearcher implements QueryExecutor<SchemaTypeDef, SearchParameters> {
  @Override
  public boolean execute(@NotNull SearchParameters queryParameters, @NotNull Processor<SchemaTypeDef> consumer) {

    final SchemaTypeDef target = queryParameters.schemaTypeDef;
    final Project project = PsiUtilCore.getProjectInReadAction(target);
    final VirtualFile targetVirtualFile = VirtualFileUtil.getOriginalVirtualFile(target.getContainingFile());
    if (targetVirtualFile == null) return true;

    final Application application = ApplicationManager.getApplication();

    final List<SchemaTypeDef> parents = new ArrayList<>();

    if (queryParameters.includeExtends) {
      application.runReadAction(() -> {
        parents.addAll(target.extendsParents());
      });
    }

    // -- process 'supplements'

    if (queryParameters.includeSupplements) {

      List<SchemaTypeDef> candidates = application.runReadAction(
          (Computable<List<SchemaTypeDef>>) () -> SchemaIndexUtil.findTypeDefs(project, null, null, GlobalSearchScope.allScope(project))
      );

      for (SchemaTypeDef candidate : candidates) {
        ProgressManager.checkCanceled();

        application.runReadAction(() -> {

          // supplemented type must be in the candidate's search scope
          final GlobalSearchScope candidateScope = SchemaSearchScopeUtil.getSearchScope(candidate);
          if (candidateScope.contains(targetVirtualFile)) {

            List<SchemaTypeDef> supplementedList = null;
            if (candidate instanceof SchemaRecordTypeDef) {
              SchemaRecordTypeDef recordTypeDef = (SchemaRecordTypeDef) candidate;
              supplementedList = recordTypeDef.supplemented();
            } else if (candidate instanceof SchemaVarTypeDef) {
              SchemaVarTypeDef varTypeDef = (SchemaVarTypeDef) candidate;
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
        List<SchemaSupplementDef> supplements = SchemaIndexUtil.findSupplementsBySupplemented(project, target);
        parents.addAll(supplements.stream()
            .map(SchemaSupplementDef::source)
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
      });
    }

    parents.forEach(consumer::process);

    return true;
  }
}
