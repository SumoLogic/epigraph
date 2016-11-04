package ws.epigraph.ideaplugin.schema.features.structureview;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.psi.PsiFile;
import ws.epigraph.schema.parser.psi.SchemaFile;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaStructureViewFactory implements PsiStructureViewFactory {
  @Nullable
  @Override
  public StructureViewBuilder getStructureViewBuilder(PsiFile psiFile) {
    return new SchemaStructureViewBuilder((SchemaFile) psiFile);
  }
}
