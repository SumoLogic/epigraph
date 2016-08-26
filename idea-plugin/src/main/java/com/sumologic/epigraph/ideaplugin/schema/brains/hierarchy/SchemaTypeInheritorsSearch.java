package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.application.ApplicationManager;
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
public class SchemaTypeInheritorsSearch extends QueryFactory<EpigraphTypeDef, SchemaTypeInheritorsSearch.SearchParameters> {
  public static final SchemaTypeInheritorsSearch INSTANCE = new SchemaTypeInheritorsSearch();

  public static class SearchParameters {
    public final EpigraphTypeDef epigraphTypeDef;

    public SearchParameters(EpigraphTypeDef epigraphTypeDef) {
      this.epigraphTypeDef = epigraphTypeDef;
    }
  }

  private SchemaTypeInheritorsSearch() {
    registerExecutor(new SchemaTypeInheritorsSearcher());
  }

  public static Query<EpigraphTypeDef> search(@NotNull final EpigraphTypeDef epigraphTypeDef) {
    return search(new SearchParameters(epigraphTypeDef));
  }

  public static Query<EpigraphTypeDef> search(@NotNull final SearchParameters parameters) {
    return INSTANCE.createUniqueResultsQuery(parameters, ContainerUtil.canonicalStrategy(),
        schemaTypeDef -> ApplicationManager.getApplication().runReadAction(
            (Computable<SmartPsiElementPointer<EpigraphTypeDef>>) () ->
                SmartPointerManager.getInstance(schemaTypeDef.getProject()).createSmartPsiElementPointer(schemaTypeDef)
        )
    );
  }
}
