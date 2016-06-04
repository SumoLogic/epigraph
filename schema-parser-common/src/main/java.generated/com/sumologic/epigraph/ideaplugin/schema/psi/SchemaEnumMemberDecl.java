// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public interface SchemaEnumMemberDecl extends CustomParamsHolder, PsiNamedElement {

  @NotNull
  List<SchemaCustomParam> getCustomParamList();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getId();

  @Nullable
  String getName();

  PsiElement setName(String name);

  @NotNull
  PsiElement getNameIdentifier();

}
