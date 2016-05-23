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

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeInheritorsSearcher implements QueryExecutor<SchemaTypeDef, SearchParameters> {
  @Override
  public boolean execute(@NotNull SearchParameters queryParameters, @NotNull Processor<SchemaTypeDef> consumer) {
    // TODO use stub indices
    // TODO take supplements into account

    final SchemaTypeDef target = queryParameters.schemaTypeDef;
    final Project project = PsiUtilCore.getProjectInReadAction(target);


    List<SchemaTypeDef> typeDefs = ApplicationManager.getApplication().runReadAction(
        (Computable<List<SchemaTypeDef>>) () -> SchemaIndexUtil.findTypeDefs(project, null, null)
    );

    for (SchemaTypeDef candidate : typeDefs) {
      ProgressManager.checkCanceled();

      final SchemaTypeDef[] child = {null};
      ApplicationManager.getApplication().runReadAction(() -> {
        List<SchemaTypeDef> candidateParents = candidate.parents();
        for (SchemaTypeDef candidateParent : candidateParents) {
          if (target.equals(candidateParent)) {
            child[0] = candidate;
            break;
          }
        }
      });

      if (child[0] != null) consumer.process(child[0]);

//      List<SchemaTypeDef> inheritors = ApplicationManager.getApplication().runReadAction(
//          (Computable<List<SchemaTypeDef>>) () -> {
//            List<SchemaTypeDef> parents = candidate.parents();
//            if (parents.isEmpty()) return null;
//            return parents.stream().filter(target::equals).map(p -> candidate).collect(Collectors.toList());
//          }
//      );
//
//      if (inheritors != null)
//        for (SchemaTypeDef inheritor : inheritors)
//          if (!consumer.process(inheritor)) return false;
    }

    return false;
  }
}
