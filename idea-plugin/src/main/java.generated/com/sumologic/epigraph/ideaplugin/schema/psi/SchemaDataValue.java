// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDataValue extends PsiElement {

  @Nullable
  SchemaDataValue getDataValue();

  @NotNull
  List<SchemaFqnTypeRef> getFqnTypeRefList();

  @Nullable
  PsiElement getNull();

}
