// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpDeleteSingleTagProjection extends PsiElement {

  @Nullable
  SchemaOpDeleteModelProjection getOpDeleteModelProjection();

  @NotNull
  List<SchemaOpDeleteModelProperty> getOpDeleteModelPropertyList();

  @Nullable
  SchemaTagName getTagName();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
