// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaUnionTypeDef extends SchemaTypeDef {

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaUnionTypeBody getUnionTypeBody();

  @NotNull
  PsiElement getUnion();

  @Nullable
  PsiElement getId();

}
