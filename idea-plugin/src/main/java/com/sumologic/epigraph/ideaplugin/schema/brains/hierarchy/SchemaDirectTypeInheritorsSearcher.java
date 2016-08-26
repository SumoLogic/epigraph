package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.SchemaDirectTypeInheritorsSearch.SearchParameters;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import io.epigraph.lang.parser.psi.*;
import io.epigraph.lang.parser.psi.EpigraphRecordTypeDef;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeInheritorsSearcher implements QueryExecutor<EpigraphTypeDef, SearchParameters> {
  @Override
  public boolean execute(@NotNull SearchParameters queryParameters, @NotNull Processor<EpigraphTypeDef> consumer) {

    final EpigraphTypeDef target = queryParameters.epigraphTypeDef;
    final Project project = PsiUtilCore.getProjectInReadAction(target);
    Application application = ApplicationManager.getApplication();

    List<EpigraphTypeDef> candidates = application.runReadAction(
        (Computable<List<EpigraphTypeDef>>) () -> SchemaIndexUtil.findTypeDefs(project, null, null, SchemaSearchScopeUtil.getSearchScope(target))
    );

    final List<EpigraphTypeDef> children = new ArrayList<>();

    for (EpigraphTypeDef candidate : candidates) {
      ProgressManager.checkCanceled();

      application.runReadAction(() -> {
        List<EpigraphTypeDef> candidateParents = candidate.extendsParents();
        children.addAll(
            candidateParents.stream()
                .filter(target::equals)
                .map(candidateParent -> candidate)
                .collect(Collectors.toList()));
      });

    }

    // -- process 'supplements'

    if (target instanceof EpigraphRecordTypeDef) {
      EpigraphRecordTypeDef recordTypeDef = (EpigraphRecordTypeDef) target;
      children.addAll(application.runReadAction((Computable<List<EpigraphTypeDef>>) recordTypeDef::supplemented));
    } else if (target instanceof EpigraphVarTypeDef) {
      EpigraphVarTypeDef varTypeDef = (EpigraphVarTypeDef) target;
      children.addAll(application.runReadAction((Computable<List<EpigraphTypeDef>>) varTypeDef::supplemented));
    }

    // -- process 'supplement x with y'

    application.runReadAction(() -> {
      List<SchemaSupplementDef> supplements = SchemaIndexUtil.findSupplementsBySource(project, target);
      for (SchemaSupplementDef supplement : supplements) {
        children.addAll(supplement.supplemented());
      }
    });

    children.forEach(consumer::process);

    return true;
  }
}
