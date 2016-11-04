package ws.epigraph.schema.parser.psi;

import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaReferenceFactory {

  @Nullable
  public static PsiReference getQnReference(@NotNull SchemaQnSegment segment) {
    return null;
  }

  @Nullable
  public static PsiReference getVarTagReference(@NotNull SchemaVarTagRef varTagRef) {
    return null;
  }
}
