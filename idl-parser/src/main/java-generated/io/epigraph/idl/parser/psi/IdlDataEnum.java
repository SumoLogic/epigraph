// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlDataEnum extends IdlDataValue {

  @Nullable
  IdlFqnTypeRef getFqnTypeRef();

  @NotNull
  IdlQid getQid();

  @Nullable
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}
