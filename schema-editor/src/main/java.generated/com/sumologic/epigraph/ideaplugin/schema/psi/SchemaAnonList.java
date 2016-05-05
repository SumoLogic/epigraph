// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaAnonList extends PsiElement {

  @Nullable
  SchemaDefaultOverride getDefaultOverride();

  @Nullable
  SchemaFqnTypeRef getFqnTypeRef();

  @Nullable
  PsiElement getBracketLeft();

  @Nullable
  PsiElement getBracketRight();

  @NotNull
  PsiElement getList();

}
