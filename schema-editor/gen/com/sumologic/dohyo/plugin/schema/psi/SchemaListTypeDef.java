// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaListTypeDef extends PsiElement {

  @NotNull
  SchemaAnonList getAnonList();

  @NotNull
  List<SchemaCustomParam> getCustomParamList();

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getId();

}
