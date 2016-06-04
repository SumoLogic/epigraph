// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDataMapEntry extends PsiElement {

  @NotNull
  List<SchemaDataValue> getDataValueList();

  @NotNull
  List<SchemaFqnTypeRef> getFqnTypeRefList();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getComma();

  @Nullable
  PsiElement getNull();

}
