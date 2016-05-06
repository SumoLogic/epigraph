// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;

public interface SchemaFqnSegment extends PsiNameIdentifierOwner {

  @NotNull
  PsiElement getId();

  @Nullable
  String getName();

  PsiElement setName(String name);

  @NotNull
  PsiElement getNameIdentifier();

  @Nullable
  SchemaFqnTypeRef getFqnTypeRef();

  boolean isLast();

  @Nullable
  PsiReference getReference();

}
