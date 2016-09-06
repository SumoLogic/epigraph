// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlDataVarEntry extends PsiElement {

  @Nullable
  IdlDataPrimitive getDataPrimitive();

  @Nullable
  IdlDataValue getDataValue();

  @NotNull
  List<IdlFqnTypeRef> getFqnTypeRefList();

  @NotNull
  IdlQid getQid();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getComma();

  @Nullable
  PsiElement getNull();

}
