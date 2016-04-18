// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaMultiTypeDef extends SchemaTypeDef {

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaMultiSupplementsDecl getMultiSupplementsDecl();

  @Nullable
  SchemaMultiTypeBody getMultiTypeBody();

  @NotNull
  PsiElement getMulti();

  @Nullable
  PsiElement getId();

}
