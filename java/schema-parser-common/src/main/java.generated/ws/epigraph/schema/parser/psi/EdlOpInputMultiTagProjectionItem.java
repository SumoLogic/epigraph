// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpInputMultiTagProjectionItem extends PsiElement {

  @Nullable
  EdlOpInputModelProjection getOpInputModelProjection();

  @NotNull
  List<EdlOpInputModelProperty> getOpInputModelPropertyList();

  @NotNull
  EdlTagName getTagName();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getPlus();

}
