package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

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
import com.sumologic.epigraph.ideaplugin.schema.brains.VirtualFileUtil;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.SchemaDirectTypeParentsSearch.SearchParameters;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import io.epigraph.lang.parser.psi.*;
import io.epigraph.lang.parser.psi.EpigraphRecordTypeDef;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeParentsSearcher implements QueryExecutor<EpigraphTypeDef, SearchParameters> {
  @Override
  public boolean execute(@NotNull SearchParameters queryParameters, @NotNull Processor<EpigraphTypeDef> consumer) {

    final EpigraphTypeDef target = queryParameters.epigraphTypeDef;
    final Project project = PsiUtilCore.getProjectInReadAction(target);
    final VirtualFile targetVirtualFile = VirtualFileUtil.getOriginalVirtualFile(target.getContainingFile());
    if (targetVirtualFile == null) return true;

    final Application application = ApplicationManager.getApplication();

    final List<EpigraphTypeDef> parents = new ArrayList<>();

    if (queryParameters.includeExtends) {
      application.runReadAction(() -> {
        parents.addAll(target.extendsParents());
      });
    }

    // -- process 'supplements'

    if (queryParameters.includeSupplements) {

      List<EpigraphTypeDef> candidates = application.runReadAction(
          (Computable<List<EpigraphTypeDef>>) () -> SchemaIndexUtil.findTypeDefs(project, null, null, GlobalSearchScope.allScope(project))
      );

      for (EpigraphTypeDef candidate : candidates) {
        ProgressManager.checkCanceled();

        application.runReadAction(() -> {

          // supplemented type must be in the candidate's search scope
          final GlobalSearchScope candidateScope = SchemaSearchScopeUtil.getSearchScope(candidate);
          if (candidateScope.contains(targetVirtualFile)) {

            List<EpigraphTypeDef> supplementedList = null;
            if (candidate instanceof EpigraphRecordTypeDef) {
              EpigraphRecordTypeDef recordTypeDef = (EpigraphRecordTypeDef) candidate;
              supplementedList = recordTypeDef.supplemented();
            } else if (candidate instanceof EpigraphVarTypeDef) {
              EpigraphVarTypeDef varTypeDef = (EpigraphVarTypeDef) candidate;
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
        List<EpigraphSupplementDef> supplements = SchemaIndexUtil.findSupplementsBySupplemented(project, target);
        parents.addAll(supplements.stream()
            .map(EpigraphSupplementDef::source)
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
      });
    }

    parents.forEach(consumer::process);

    return true;
  }
}
