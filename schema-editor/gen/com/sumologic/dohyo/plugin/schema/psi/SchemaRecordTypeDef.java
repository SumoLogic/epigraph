// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaRecordTypeDef extends PsiElement {

  @NotNull
  List<SchemaCustomParam> getCustomParamList();

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @NotNull
  List<SchemaFieldDecl> getFieldDeclList();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getRecord();

  @NotNull
  PsiElement getId();

}
