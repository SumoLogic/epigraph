package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.Query;
import com.intellij.util.QueryFactory;
import com.intellij.util.containers.ContainerUtil;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaTypeParentsSearch extends QueryFactory<EpigraphTypeDef, SchemaTypeParentsSearch.SearchParameters> {
  public static final SchemaTypeParentsSearch INSTANCE = new SchemaTypeParentsSearch();

  public static class SearchParameters {
    @NotNull
    public final EpigraphTypeDef epigraphTypeDef;

    public SearchParameters(@NotNull EpigraphTypeDef epigraphTypeDef) {
      this.epigraphTypeDef = epigraphTypeDef;
    }
  }

  private SchemaTypeParentsSearch() {
    registerExecutor(new SchemaTypeParentsSearcher());
  }

  public static Query<EpigraphTypeDef> search(@NotNull final EpigraphTypeDef epigraphTypeDef) {
    return search(new SearchParameters(epigraphTypeDef));
  }

  public static Query<EpigraphTypeDef> search(@NotNull final SearchParameters parameters) {
    final Project project = parameters.epigraphTypeDef.getProject();
    return INSTANCE.createUniqueResultsQuery(parameters, ContainerUtil.canonicalStrategy(),
        schemaTypeDef ->
            schemaTypeDef == null ? null :
            ApplicationManager.getApplication().runReadAction(
            (Computable<SmartPsiElementPointer<EpigraphTypeDef>>) () ->
                SmartPointerManager.getInstance(project).createSmartPsiElementPointer(schemaTypeDef)
        )
    );
  }
}
