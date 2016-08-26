package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.util.Query;
import com.intellij.util.QueryFactory;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeInheritorsSearch extends QueryFactory<EpigraphTypeDef, SchemaDirectTypeInheritorsSearch.SearchParameters> {
  public static final SchemaDirectTypeInheritorsSearch INSTANCE = new SchemaDirectTypeInheritorsSearch();

  public static class SearchParameters {
    public final EpigraphTypeDef epigraphTypeDef;

    public SearchParameters(EpigraphTypeDef epigraphTypeDef) {
      this.epigraphTypeDef = epigraphTypeDef;
    }
  }

  private SchemaDirectTypeInheritorsSearch() {
    registerExecutor(new SchemaDirectTypeInheritorsSearcher());
  }

  public static Query<EpigraphTypeDef> search(@NotNull final EpigraphTypeDef epigraphTypeDef) {
    return search(new SearchParameters(epigraphTypeDef));
  }

  public static Query<EpigraphTypeDef> search(@NotNull final SearchParameters parameters) {
    return INSTANCE.createUniqueResultsQuery(parameters);
  }
}
