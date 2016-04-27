// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface SchemaFqnSegment extends PsiElement {

  @NotNull
  PsiElement getId();

  @Nullable
  SchemaFqnTypeRef getFqnTypeRef();

  boolean isLast();

  @Nullable
  PsiReference getReference();

}
