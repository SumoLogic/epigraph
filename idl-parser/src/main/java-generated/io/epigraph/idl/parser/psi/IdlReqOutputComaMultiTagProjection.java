// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlReqOutputComaMultiTagProjection extends PsiElement {

  @NotNull
  List<IdlReqOutputComaTagProjectionItem> getReqOutputComaTagProjectionItemList();

  @NotNull
  List<IdlTagName> getTagNameList();

  @NotNull
  PsiElement getColon();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
