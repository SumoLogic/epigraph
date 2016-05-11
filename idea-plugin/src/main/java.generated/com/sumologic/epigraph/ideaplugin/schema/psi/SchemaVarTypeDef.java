// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaVarTypeDef extends SchemaTypeDef {

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaVarTypeBody getVarTypeBody();

  @Nullable
  SchemaVarTypeSupplementsDecl getVarTypeSupplementsDecl();

  @NotNull
  PsiElement getVartype();

  @Nullable
  PsiElement getId();

}
