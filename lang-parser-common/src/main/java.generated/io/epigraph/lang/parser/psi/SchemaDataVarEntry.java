// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDataVarEntry extends PsiElement {

  @Nullable
  SchemaDataPrimitive getDataPrimitive();

  @Nullable
  SchemaDataValue getDataValue();

  @NotNull
  List<SchemaFqnTypeRef> getFqnTypeRefList();

  @NotNull
  SchemaQid getQid();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getComma();

  @Nullable
  PsiElement getNull();

}
