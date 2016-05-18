package com.sumologic.epigraph.ideaplugin.schema.brains.search;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import com.sumologic.epigraph.ideaplugin.schema.brains.search.SchemaDirectTypeInheritorsSearch.SearchParameters;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeInheritorsSearcher implements QueryExecutor<SchemaTypeDef, SearchParameters> {
  @Override
  public boolean execute(@NotNull SearchParameters queryParameters, @NotNull Processor<SchemaTypeDef> consumer) {
    // TODO use stub indices
    // TODO take supplements into account

    final SchemaTypeDef parent = queryParameters.schemaTypeDef;
    final Project project = PsiUtilCore.getProjectInReadAction(parent);


    List<SchemaTypeDef> typeDefs = ApplicationManager.getApplication().runReadAction(
        (Computable<List<SchemaTypeDef>>) () -> SchemaIndexUtil.findTypeDefs(project, null, null)
    );

    for (SchemaTypeDef typeDef : typeDefs) {
      ProgressManager.checkCanceled();

      List<SchemaTypeDef> inheritors = ApplicationManager.getApplication().runReadAction(
          (Computable<List<SchemaTypeDef>>) () -> {
            List<SchemaTypeDef> parents = typeDef.parents();
            if (parents.isEmpty()) return null;
            return parents.stream().filter(parent::equals).map(p -> typeDef).collect(Collectors.toList());
          }
      );

      if (inheritors != null)
        for (SchemaTypeDef inheritor : inheritors)
          if (!consumer.process(inheritor)) return false;
    }

    return false;
  }
}
