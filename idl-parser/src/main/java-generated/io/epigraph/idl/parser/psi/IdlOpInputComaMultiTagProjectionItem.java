// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputComaMultiTagProjectionItem extends PsiElement {

  @Nullable
  IdlOpInputComaModelProjection getOpInputComaModelProjection();

  @NotNull
  List<IdlOpInputModelProperty> getOpInputModelPropertyList();

  @NotNull
  IdlTagName getTagName();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getPlus();

}
