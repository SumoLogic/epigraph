// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpDeleteMultiTagProjectionItem extends PsiElement {

  @Nullable
  IdlOpDeleteModelProjection getOpDeleteModelProjection();

  @NotNull
  List<IdlOpDeleteModelProperty> getOpDeleteModelPropertyList();

  @NotNull
  IdlTagName getTagName();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
