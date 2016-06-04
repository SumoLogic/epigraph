// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDataVarEntry extends PsiElement {

  @Nullable
  SchemaDataValue getDataValue();

  @NotNull
  List<SchemaFqnTypeRef> getFqnTypeRefList();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getComma();

  @Nullable
  PsiElement getNull();

  @NotNull
  PsiElement getId();

}
