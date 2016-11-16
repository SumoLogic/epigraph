// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpOutputMultiTagProjectionItem extends PsiElement {

  @Nullable
  IdlOpOutputModelProjection getOpOutputModelProjection();

  @NotNull
  List<IdlOpOutputModelProperty> getOpOutputModelPropertyList();

  @NotNull
  IdlTagName getTagName();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
