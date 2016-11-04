package ws.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.util.Query;
import com.intellij.util.QueryFactory;
import io.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class SchemaDirectTypeParentsSearch extends QueryFactory<SchemaTypeDef, SchemaDirectTypeParentsSearch.SearchParameters> {
  public static final SchemaDirectTypeParentsSearch INSTANCE = new SchemaDirectTypeParentsSearch();

  public static class SearchParameters {
    public final SchemaTypeDef schemaTypeDef;
    public final boolean includeExtends;
    public final boolean includeSupplements;
    public final boolean includeStandaloneSupplements;

    public SearchParameters(SchemaTypeDef schemaTypeDef,
                            boolean includeExtends,
                            boolean includeSupplements,
                            boolean includeStandaloneSupplements) {
      this.schemaTypeDef = schemaTypeDef;
      this.includeExtends = includeExtends;
      this.includeSupplements = includeSupplements;
      this.includeStandaloneSupplements = includeStandaloneSupplements;
    }

    public SearchParameters(SchemaTypeDef schemaTypeDef) {
      this(schemaTypeDef, true, true, true);
    }
  }

  private SchemaDirectTypeParentsSearch() {
    registerExecutor(new SchemaDirectTypeParentsSearcher());
  }

  public static Query<SchemaTypeDef> search(@NotNull final SchemaTypeDef schemaTypeDef) {
    return search(new SearchParameters(schemaTypeDef));
  }

  public static Query<SchemaTypeDef> search(@NotNull final SearchParameters parameters) {
    return INSTANCE.createUniqueResultsQuery(parameters);
  }
}
