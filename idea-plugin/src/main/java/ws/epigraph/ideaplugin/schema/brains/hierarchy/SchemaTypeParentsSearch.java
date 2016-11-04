package ws.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.Query;
import com.intellij.util.QueryFactory;
import com.intellij.util.containers.ContainerUtil;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaTypeParentsSearch extends QueryFactory<SchemaTypeDef, SchemaTypeParentsSearch.SearchParameters> {
  public static final SchemaTypeParentsSearch INSTANCE = new SchemaTypeParentsSearch();

  public static class SearchParameters {
    @NotNull
    public final SchemaTypeDef schemaTypeDef;

    public SearchParameters(@NotNull SchemaTypeDef schemaTypeDef) {
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
    final Project project = parameters.schemaTypeDef.getProject();
    return INSTANCE.createUniqueResultsQuery(parameters, ContainerUtil.canonicalStrategy(),
        schemaTypeDef ->
            schemaTypeDef == null ? null :
            ApplicationManager.getApplication().runReadAction(
            (Computable<SmartPsiElementPointer<SchemaTypeDef>>) () ->
                SmartPointerManager.getInstance(project).createSmartPsiElementPointer(schemaTypeDef)
        )
    );
  }
}
