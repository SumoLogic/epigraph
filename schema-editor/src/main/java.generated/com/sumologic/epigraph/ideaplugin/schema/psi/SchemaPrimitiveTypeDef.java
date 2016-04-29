// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaPrimitiveTypeDef extends SchemaTypeDef {

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @NotNull
  SchemaPrimitiveKind getPrimitiveKind();

  @Nullable
  SchemaPrimitiveTypeBody getPrimitiveTypeBody();

  @Nullable
  PsiElement getId();

}
