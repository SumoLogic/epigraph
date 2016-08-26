package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.util.Query;
import com.intellij.util.QueryFactory;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeParentsSearch extends QueryFactory<EpigraphTypeDef, SchemaDirectTypeParentsSearch.SearchParameters> {
  public static final SchemaDirectTypeParentsSearch INSTANCE = new SchemaDirectTypeParentsSearch();

  public static class SearchParameters {
    public final EpigraphTypeDef epigraphTypeDef;
    public final boolean includeExtends;
    public final boolean includeSupplements;
    public final boolean includeStandaloneSupplements;

    public SearchParameters(EpigraphTypeDef epigraphTypeDef,
                            boolean includeExtends,
                            boolean includeSupplements,
                            boolean includeStandaloneSupplements) {
      this.epigraphTypeDef = epigraphTypeDef;
      this.includeExtends = includeExtends;
      this.includeSupplements = includeSupplements;
      this.includeStandaloneSupplements = includeStandaloneSupplements;
    }

    public SearchParameters(EpigraphTypeDef epigraphTypeDef) {
      this(epigraphTypeDef, true, true, true);
    }
  }

  private SchemaDirectTypeParentsSearch() {
    registerExecutor(new SchemaDirectTypeParentsSearcher());
  }

  public static Query<EpigraphTypeDef> search(@NotNull final EpigraphTypeDef epigraphTypeDef) {
    return search(new SearchParameters(epigraphTypeDef));
  }

  public static Query<EpigraphTypeDef> search(@NotNull final SearchParameters parameters) {
    return INSTANCE.createUniqueResultsQuery(parameters);
  }
}
