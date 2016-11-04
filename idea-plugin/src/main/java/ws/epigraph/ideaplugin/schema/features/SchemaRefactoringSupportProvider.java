package ws.epigraph.ideaplugin.schema.features;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import io.epigraph.schema.parser.psi.SchemaQnSegment;
import io.epigraph.schema.parser.psi.SchemaTypeDef;
import io.epigraph.schema.parser.psi.SchemaVarTagDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class SchemaRefactoringSupportProvider extends RefactoringSupportProvider {
  @Override
  public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context) {
    return element instanceof SchemaTypeDef || element instanceof SchemaQnSegment || element instanceof SchemaVarTagDecl;
  }

  @Override
  public boolean isSafeDeleteAvailable(@NotNull PsiElement element) {
    return element instanceof SchemaTypeDef;
  }
}
