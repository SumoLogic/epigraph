// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputMapModelProjection extends PsiElement {

  @NotNull
  IdlOpInputKeyProjection getOpInputKeyProjection();

  @Nullable
  IdlOpInputVarProjection getOpInputVarProjection();

  @Nullable
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}