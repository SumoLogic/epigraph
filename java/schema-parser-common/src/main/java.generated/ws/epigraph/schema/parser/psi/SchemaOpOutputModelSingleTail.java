// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpOutputModelSingleTail extends PsiElement {

  @Nullable
  SchemaOpOutputModelProjection getOpOutputModelProjection();

  @NotNull
  List<SchemaOpOutputModelProperty> getOpOutputModelPropertyList();

  @NotNull
  SchemaTypeRef getTypeRef();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
