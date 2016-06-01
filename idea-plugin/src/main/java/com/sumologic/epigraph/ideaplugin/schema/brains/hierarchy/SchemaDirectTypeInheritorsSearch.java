package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.util.Query;
import com.intellij.util.QueryFactory;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeInheritorsSearch extends QueryFactory<SchemaTypeDef, SchemaDirectTypeInheritorsSearch.SearchParameters> {
  public static final SchemaDirectTypeInheritorsSearch INSTANCE = new SchemaDirectTypeInheritorsSearch();

  public static class SearchParameters {
    public final SchemaTypeDef schemaTypeDef;

    public SearchParameters(SchemaTypeDef schemaTypeDef) {
      this.schemaTypeDef = schemaTypeDef;
    }
  }

  private SchemaDirectTypeInheritorsSearch() {
    registerExecutor(new SchemaDirectTypeInheritorsSearcher());
  }

  public static Query<SchemaTypeDef> search(@NotNull final SchemaTypeDef schemaTypeDef) {
    return search(new SearchParameters(schemaTypeDef));
  }

  public static Query<SchemaTypeDef> search(@NotNull final SearchParameters parameters) {
    return INSTANCE.createUniqueResultsQuery(parameters);
  }
}
