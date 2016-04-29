// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaPlugin.schema.psi;

import java.util.List;
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
