// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpOutputKeyProjection extends PsiElement {

  @NotNull
  List<IdlOpOutputKeyProjectionPart> getOpOutputKeyProjectionPartList();

  @NotNull
  PsiElement getBracketLeft();

  @Nullable
  PsiElement getBracketRight();

  @Nullable
  PsiElement getForbidden();

  @Nullable
  PsiElement getRequired();

}