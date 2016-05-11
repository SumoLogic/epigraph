// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaTagCommonType extends PsiElement {

  @Nullable
  SchemaDefaultOverride getDefaultOverride();

  @NotNull
  SchemaFqnTypeRef getFqnTypeRef();

  @NotNull
  PsiElement getBracketLeft();

  @NotNull
  PsiElement getBracketRight();

}
