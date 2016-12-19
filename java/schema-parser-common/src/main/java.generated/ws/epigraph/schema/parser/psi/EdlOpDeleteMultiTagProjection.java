// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpDeleteMultiTagProjection extends PsiElement {

  @NotNull
  List<EdlOpDeleteMultiTagProjectionItem> getOpDeleteMultiTagProjectionItemList();

  @NotNull
  PsiElement getColon();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
