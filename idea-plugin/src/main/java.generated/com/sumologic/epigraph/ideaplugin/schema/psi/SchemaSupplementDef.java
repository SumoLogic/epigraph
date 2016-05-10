// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaSupplementDef extends PsiElement {

  @NotNull
  List<SchemaCombinedFqns> getCombinedFqnsList();

  @Nullable
  SchemaFqnTypeRef getFqnTypeRef();

  @NotNull
  PsiElement getSupplement();

  @Nullable
  PsiElement getWith();

}
