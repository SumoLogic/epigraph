package ws.epigraph.ideaplugin.schema.diagram;

import com.intellij.diagram.extras.providers.ImplementationsProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.ideaplugin.schema.SchemaBundle;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import io.epigraph.schema.parser.psi.SchemaTypeDef;

import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramImplementationElementsProvider extends ImplementationsProvider<PsiNamedElement> {
  @Override
  public boolean isEnabledOn(PsiNamedElement element) {
    return element instanceof SchemaTypeDef;
  }

  @Override
  public PsiNamedElement[] getElements(PsiNamedElement psiNamedElement, Project project) {
    SchemaTypeDef typeDef = (SchemaTypeDef) psiNamedElement;
    List<SchemaTypeDef> parents = HierarchyCache.getHierarchyCache(project).getDirectTypeInheritors(typeDef);
    return parents.toArray(new PsiNamedElement[parents.size()]);
  }

  @Override
  public String getHeaderName(PsiNamedElement psiNamedElement, Project project) {
    return SchemaBundle.message("diagram.show.subtypes", psiNamedElement.getName());
  }

  @Override
  public Comparator<? super PsiNamedElement> getComparator() {
    return PSI_COMPARATOR;
  }
}
