// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlDataPrimitive extends IdlDataValue {

  @Nullable
  IdlFqnTypeRef getFqnTypeRef();

  @Nullable
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

  @Nullable
  PsiElement getBoolean();

  @Nullable
  PsiElement getNumber();

  @Nullable
  PsiElement getString();

}
