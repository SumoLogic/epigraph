// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaAnonUnion extends PsiElement {

  @Nullable
  SchemaDefaultOverride getDefaultOverride();

  @NotNull
  SchemaTypeRef getTypeRef();

  @NotNull
  PsiElement getBracketLeft();

  @NotNull
  PsiElement getBracketRight();

  @NotNull
  PsiElement getUnion();

}
