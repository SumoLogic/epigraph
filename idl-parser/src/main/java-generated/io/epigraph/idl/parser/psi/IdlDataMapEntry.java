// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlDataMapEntry extends PsiElement {

  @Nullable
  IdlDataPrimitive getDataPrimitive();

  @NotNull
  List<IdlDataValue> getDataValueList();

  @NotNull
  List<IdlFqnTypeRef> getFqnTypeRefList();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getComma();

  @Nullable
  PsiElement getNull();

}
