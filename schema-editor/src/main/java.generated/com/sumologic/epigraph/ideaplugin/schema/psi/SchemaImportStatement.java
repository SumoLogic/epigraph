// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaImportStatement extends PsiElement {

  @Nullable
  SchemaFqn getFqn();

  @Nullable
  SchemaStarImportSuffix getStarImportSuffix();

  @NotNull
  PsiElement getImport();

}
