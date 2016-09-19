// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpParam extends PsiElement {

  @NotNull
  IdlFqnTypeRef getFqnTypeRef();

  @NotNull
  IdlOpInputModelProjection getOpInputModelProjection();

  @NotNull
  IdlQid getQid();

  @Nullable
  IdlVarValue getVarValue();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getEq();

  @Nullable
  PsiElement getPlus();

  @NotNull
  PsiElement getSemicolon();

}
