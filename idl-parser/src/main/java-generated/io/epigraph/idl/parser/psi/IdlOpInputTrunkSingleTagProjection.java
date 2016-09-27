// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputTrunkSingleTagProjection extends PsiElement {

  @NotNull
  List<IdlOpInputModelProperty> getOpInputModelPropertyList();

  @Nullable
  IdlOpInputTrunkModelProjection getOpInputTrunkModelProjection();

  @Nullable
  IdlTagName getTagName();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getPlus();

}
