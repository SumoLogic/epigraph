// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpOutputMultiTagProjectionItem extends PsiElement {

  @Nullable
  EdlOpOutputModelProjection getOpOutputModelProjection();

  @NotNull
  List<EdlOpOutputModelProperty> getOpOutputModelPropertyList();

  @NotNull
  EdlTagName getTagName();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
