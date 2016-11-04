package ws.epigraph.ideaplugin.schema.diagram;

import com.intellij.diagram.DiagramElementsProvider;
import com.intellij.diagram.actions.DiagramAddElementAction;
import com.intellij.diagram.actions.DiagramDefaultAddElementAction;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.diagram.extras.providers.DiagramDnDProvider;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramExtras extends DiagramExtras<PsiNamedElement> {
  private static final DiagramDefaultAddElementAction<PsiNamedElement> ADD_ELEMENT_ACTION =
      new SchemaDiagramAddElementAction();

  @SuppressWarnings("unchecked")
  private static final DiagramElementsProvider<PsiNamedElement>[] ELEMENTS_PROVIDERS = new DiagramElementsProvider[]{
      new SchemaDiagramParentElementsProvider(), new SchemaDiagramImplementationElementsProvider()
  };

  private static final DiagramDnDProvider<PsiNamedElement> DND_PROVIDER = new SchemaDiagramDnDProvider();

  @Nullable
  @Override
  public DiagramAddElementAction getAddElementHandler() {
    return ADD_ELEMENT_ACTION;
  }

  @Nullable
  @Override
  public DiagramDnDProvider<PsiNamedElement> getDnDProvider() {
    return DND_PROVIDER;
  }

  @Override
  public DiagramElementsProvider<PsiNamedElement>[] getElementsProviders() {
    return ELEMENTS_PROVIDERS;
  }
}
