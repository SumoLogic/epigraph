// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaListTypeDef extends SchemaTypeDef {

  @NotNull
  SchemaAnonList getAnonList();

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaListTypeBody getListTypeBody();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  PsiElement getId();

}
