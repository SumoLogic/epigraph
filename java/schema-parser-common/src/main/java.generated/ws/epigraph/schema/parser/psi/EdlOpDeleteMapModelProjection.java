// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpDeleteMapModelProjection extends PsiElement {

  @NotNull
  EdlOpDeleteKeyProjection getOpDeleteKeyProjection();

  @Nullable
  EdlOpDeleteVarProjection getOpDeleteVarProjection();

  @Nullable
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}