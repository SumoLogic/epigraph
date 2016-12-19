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
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.EdlDirectTypeInheritorsSearch.SearchParameters;
import ws.epigraph.ideaplugin.schema.index.EdlIndexUtil;
import ws.epigraph.ideaplugin.schema.index.EdlSearchScopeUtil;
import ws.epigraph.schema.parser.psi.EdlRecordTypeDef;
import ws.epigraph.schema.parser.psi.EdlSupplementDef;
import ws.epigraph.schema.parser.psi.EdlTypeDef;
import ws.epigraph.schema.parser.psi.EdlVarTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlDirectTypeInheritorsSearcher implements QueryExecutor<EdlTypeDef, SearchParameters> {
  @Override
  public boolean execute(@NotNull SearchParameters queryParameters, @NotNull Processor<EdlTypeDef> consumer) {

    final EdlTypeDef target = queryParameters.edlTypeDef;
    final Project project = PsiUtilCore.getProjectInReadAction(target);
    Application application = ApplicationManager.getApplication();

    List<EdlTypeDef> candidates = application.runReadAction(
        (Computable<List<EdlTypeDef>>) () -> EdlIndexUtil.findTypeDefs(project, null, null, EdlSearchScopeUtil.getSearchScope(target))
    );

    final List<EdlTypeDef> children = new ArrayList<>();

    for (EdlTypeDef candidate : candidates) {
      ProgressManager.checkCanceled();

      application.runReadAction(() -> {
        List<EdlTypeDef> candidateParents = candidate.extendsParents();
        children.addAll(
            candidateParents.stream()
                .filter(target::equals)
                .map(candidateParent -> candidate)
                .collect(Collectors.toList()));
      });

    }

    // -- process 'supplements'

    if (target instanceof EdlRecordTypeDef) {
      EdlRecordTypeDef recordTypeDef = (EdlRecordTypeDef) target;
      children.addAll(application.runReadAction((Computable<List<EdlTypeDef>>) recordTypeDef::supplemented));
    } else if (target instanceof EdlVarTypeDef) {
      EdlVarTypeDef varTypeDef = (EdlVarTypeDef) target;
      children.addAll(application.runReadAction((Computable<List<EdlTypeDef>>) varTypeDef::supplemented));
    }

    // -- process 'supplement x with y'

    application.runReadAction(() -> {
      List<EdlSupplementDef> supplements = EdlIndexUtil.findSupplementsBySource(project, target);
      for (EdlSupplementDef supplement : supplements) {
        children.addAll(supplement.supplemented());
      }
    });

    children.forEach(consumer::process);

    return true;
  }
}
