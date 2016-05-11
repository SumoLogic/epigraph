// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public interface SchemaCustomParam extends PsiNamedElement {

  @NotNull
  PsiElement getEq();

  @Nullable
  PsiElement getSemiColon();

  @Nullable
  PsiElement getDataValue();

  @NotNull
  PsiElement getId();

  @Nullable
  String getName();

  PsiElement setName(String name);

  @NotNull
  PsiElement getNameIdentifier();

}
