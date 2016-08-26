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
import io.epigraph.lang.parser.psi.SchemaRecordTypeDef;
import io.epigraph.lang.parser.psi.SchemaSupplementDef;
import io.epigraph.lang.parser.psi.SchemaTypeDef;
import io.epigraph.lang.parser.psi.SchemaVarTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeInheritorsSearcher implements QueryExecutor<SchemaTypeDef, SearchParameters> {
  @Override
  public boolean execute(@NotNull SearchParameters queryParameters, @NotNull Processor<SchemaTypeDef> consumer) {

    final SchemaTypeDef target = queryParameters.schemaTypeDef;
    final Project project = PsiUtilCore.getProjectInReadAction(target);
    Application application = ApplicationManager.getApplication();

    List<SchemaTypeDef> candidates = application.runReadAction(
        (Computable<List<SchemaTypeDef>>) () -> SchemaIndexUtil.findTypeDefs(project, null, null, SchemaSearchScopeUtil.getSearchScope(target))
    );

    final List<SchemaTypeDef> children = new ArrayList<>();

    for (SchemaTypeDef candidate : candidates) {
      ProgressManager.checkCanceled();

      application.runReadAction(() -> {
        List<SchemaTypeDef> candidateParents = candidate.extendsParents();
        children.addAll(
            candidateParents.stream()
                .filter(target::equals)
                .map(candidateParent -> candidate)
                .collect(Collectors.toList()));
      });

    }

    // -- process 'supplements'

    if (target instanceof SchemaRecordTypeDef) {
      SchemaRecordTypeDef recordTypeDef = (SchemaRecordTypeDef) target;
      children.addAll(application.runReadAction((Computable<List<SchemaTypeDef>>) recordTypeDef::supplemented));
    } else if (target instanceof SchemaVarTypeDef) {
      SchemaVarTypeDef varTypeDef = (SchemaVarTypeDef) target;
      children.addAll(application.runReadAction((Computable<List<SchemaTypeDef>>) varTypeDef::supplemented));
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
