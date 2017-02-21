// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpDeleteModelSingleTail extends PsiElement {

  @Nullable
  SchemaOpDeleteModelProjection getOpDeleteModelProjection();

  @NotNull
  List<SchemaOpDeleteModelProperty> getOpDeleteModelPropertyList();

  @NotNull
  SchemaTypeRef getTypeRef();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
