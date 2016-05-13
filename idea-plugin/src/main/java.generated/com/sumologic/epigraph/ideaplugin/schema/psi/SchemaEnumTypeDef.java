// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaEnumTypeDef extends SchemaTypeDef {

  @NotNull
  SchemaEnumTypeBody getEnumTypeBody();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @NotNull
  PsiElement getEnum();

  @NotNull
  PsiElement getId();

}
