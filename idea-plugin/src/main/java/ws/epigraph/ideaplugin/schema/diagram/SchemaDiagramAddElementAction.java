package ws.epigraph.ideaplugin.schema.diagram;

import com.intellij.diagram.actions.DiagramDefaultAddElementAction;
import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.ideaplugin.schema.SchemaBundle;
import io.epigraph.schema.parser.SchemaLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramAddElementAction extends DiagramDefaultAddElementAction<PsiNamedElement> {
  @Override
  public String getText() {
    return SchemaBundle.message("diagram.add.type");
  }

  @Override
  protected GotoClassModel2 createModel(@NotNull Project project) {
    GotoClassModel2 model = super.createModel(project);
    model.setFilterItems(Collections.singleton(SchemaLanguage.INSTANCE));
    return model;
  }
}
