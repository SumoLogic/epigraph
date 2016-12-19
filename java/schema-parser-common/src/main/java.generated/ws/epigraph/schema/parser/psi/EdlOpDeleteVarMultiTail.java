// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpDeleteVarMultiTail extends PsiElement {

  @NotNull
  List<EdlOpDeleteVarMultiTailItem> getOpDeleteVarMultiTailItemList();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

  @NotNull
  PsiElement getTilda();

}
