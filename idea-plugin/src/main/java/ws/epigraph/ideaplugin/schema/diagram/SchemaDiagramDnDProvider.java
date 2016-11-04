package ws.epigraph.ideaplugin.schema.diagram;

import com.intellij.diagram.extras.providers.DiagramDnDProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import io.epigraph.schema.parser.psi.SchemaDefs;
import io.epigraph.schema.parser.psi.SchemaFile;
import io.epigraph.schema.parser.psi.SchemaTypeDef;
import io.epigraph.schema.parser.psi.SchemaTypeDefWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramDnDProvider implements DiagramDnDProvider<PsiNamedElement> {
  @Override
  public boolean isAcceptedForDnD(Object o, Project project) {
    return o instanceof SchemaFile || o instanceof SchemaTypeDef;
  }

  @Nullable
  @Override
  public PsiNamedElement[] wrapToModelObject(Object o, Project project) {
    if (o instanceof SchemaTypeDef) return new PsiNamedElement[]{(PsiNamedElement) o};
    if (o instanceof SchemaFile) {
      SchemaFile schemaFile = (SchemaFile) o;
      SchemaDefs defs = schemaFile.getDefs();
      if (defs != null)
        return defs.getTypeDefWrapperList()
                   .stream()
                   .map(SchemaTypeDefWrapper::getElement)
                   .toArray(PsiNamedElement[]::new);
    }

    return null;
  }
}
