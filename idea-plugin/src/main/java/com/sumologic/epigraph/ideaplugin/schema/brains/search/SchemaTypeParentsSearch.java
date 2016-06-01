package com.sumologic.epigraph.ideaplugin.schema.brains.search;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.Query;
import com.intellij.util.QueryFactory;
import com.intellij.util.containers.ContainerUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaTypeParentsSearch extends QueryFactory<SchemaTypeDef, SchemaTypeParentsSearch.SearchParameters> {
  public static final SchemaTypeParentsSearch INSTANCE = new SchemaTypeParentsSearch();

  public static class SearchParameters {
    public final SchemaTypeDef schemaTypeDef;

    public SearchParameters(SchemaTypeDef schemaTypeDef) {
      this.schemaTypeDef = schemaTypeDef;
    }
  }

  private SchemaTypeParentsSearch() {
    registerExecutor(new SchemaTypeParentsSearcher());
  }

  public static Query<SchemaTypeDef> search(@NotNull final SchemaTypeDef schemaTypeDef) {
    return search(new SearchParameters(schemaTypeDef));
  }

  public static Query<SchemaTypeDef> search(@NotNull final SearchParameters parameters) {
    return INSTANCE.createUniqueResultsQuery(parameters, ContainerUtil.canonicalStrategy(),
        schemaTypeDef -> ApplicationManager.getApplication().runReadAction(
            (Computable<SmartPsiElementPointer<SchemaTypeDef>>) () ->
                SmartPointerManager.getInstance(schemaTypeDef.getProject()).createSmartPsiElementPointer(schemaTypeDef)
        )
    );
  }
}
