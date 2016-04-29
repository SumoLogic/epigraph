// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaMultiMemberDecl extends CustomParamsHolder {

  @NotNull
  List<SchemaCustomParam> getCustomParamList();

  @Nullable
  SchemaTypeRef getTypeRef();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getDefault();

  @NotNull
  PsiElement getId();

}
