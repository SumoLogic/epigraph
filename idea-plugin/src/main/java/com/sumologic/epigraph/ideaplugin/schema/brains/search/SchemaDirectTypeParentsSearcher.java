package com.sumologic.epigraph.ideaplugin.schema.brains.search;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import com.sumologic.epigraph.ideaplugin.schema.brains.search.SchemaDirectTypeParentsSearch.SearchParameters;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaRecordTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaSupplementDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaVarTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeParentsSearcher implements QueryExecutor<SchemaTypeDef, SearchParameters> {
  @Override
  public boolean execute(@NotNull SearchParameters queryParameters, @NotNull Processor<SchemaTypeDef> consumer) {

    final SchemaTypeDef target = queryParameters.schemaTypeDef;
    final Project project = PsiUtilCore.getProjectInReadAction(target);
    Application application = ApplicationManager.getApplication();

    final List<SchemaTypeDef> parents = new ArrayList<>();
    application.runReadAction(() -> {
      parents.addAll(target.extendsParents());
    });

    // -- process 'supplements'

    List<SchemaTypeDef> candidates = application.runReadAction(
        (Computable<List<SchemaTypeDef>>) () -> SchemaIndexUtil.findTypeDefs(project, null, null)
    );

    for (SchemaTypeDef candidate : candidates) {
      ProgressManager.checkCanceled();

      application.runReadAction(() -> {

        List<SchemaTypeDef> supplementedList = null;
        if (candidate instanceof SchemaRecordTypeDef) {
          SchemaRecordTypeDef recordTypeDef = (SchemaRecordTypeDef) candidate;
          supplementedList = recordTypeDef.supplemented();
        } else if (candidate instanceof SchemaVarTypeDef) {
          SchemaVarTypeDef varTypeDef = (SchemaVarTypeDef) candidate;
          supplementedList = varTypeDef.supplemented();
        }

        if (supplementedList != null) {
          parents.addAll(
              supplementedList.stream()
                  .filter(target::equals)
                  .map(candidateChild -> candidate)
                  .collect(Collectors.toList()));
        }
      });
    }


    // -- process 'supplement x with y'

    application.runReadAction(() -> {
      List<SchemaSupplementDef> supplements = SchemaIndexUtil.findSupplementsBySupplemented(project, target);
      for (SchemaSupplementDef supplement : supplements) {
        parents.addAll(supplement.supplemented());
      }
    });

    parents.forEach(consumer::process);

    return true;
  }
}
