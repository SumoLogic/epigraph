// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpParam extends PsiElement {

  @NotNull
  List<IdlCustomParam> getCustomParamList();

  @Nullable
  IdlFqnTypeRef getFqnTypeRef();

  @Nullable
  IdlOpInputModelProjection getOpInputModelProjection();

  @Nullable
  IdlQid getQid();

  @Nullable
  IdlVarValue getVarValue();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getEq();

  @Nullable
  PsiElement getPlus();

  @NotNull
  PsiElement getSemicolon();

}