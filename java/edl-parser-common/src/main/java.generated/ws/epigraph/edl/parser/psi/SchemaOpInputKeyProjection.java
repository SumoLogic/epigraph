// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpInputKeyProjection extends PsiElement {

  @NotNull
  List<SchemaOpInputKeyProjectionPart> getOpInputKeyProjectionPartList();

  @NotNull
  PsiElement getBracketLeft();

  @Nullable
  PsiElement getBracketRight();

  @Nullable
  PsiElement getForbidden();

  @Nullable
  PsiElement getRequired();

}
