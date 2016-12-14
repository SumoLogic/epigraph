// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpInputSingleTagProjection extends PsiElement {

  @Nullable
  SchemaOpInputModelProjection getOpInputModelProjection();

  @NotNull
  List<SchemaOpInputModelProperty> getOpInputModelPropertyList();

  @Nullable
  SchemaTagName getTagName();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getPlus();

}
