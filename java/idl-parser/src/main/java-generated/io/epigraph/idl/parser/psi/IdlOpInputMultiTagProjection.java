// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputMultiTagProjection extends PsiElement {

  @NotNull
  List<IdlOpInputMultiTagProjectionItem> getOpInputMultiTagProjectionItemList();

  @NotNull
  PsiElement getColon();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
