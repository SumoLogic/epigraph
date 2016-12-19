// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface EdlOpOutputSingleTagProjection extends PsiElement {

  @Nullable
  EdlOpOutputModelProjection getOpOutputModelProjection();

  @NotNull
  List<EdlOpOutputModelProperty> getOpOutputModelPropertyList();

  @Nullable
  EdlTagName getTagName();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
