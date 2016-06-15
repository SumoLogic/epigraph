// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.navigation.ItemPresentation;

public interface SchemaFieldDecl extends CustomParamsHolder, PsiNamedElement {

  @NotNull
  List<SchemaCustomParam> getCustomParamList();

  @Nullable
  SchemaDefaultOverride getDefaultOverride();

  @NotNull
  SchemaQid getQid();

  @Nullable
  SchemaTypeRef getTypeRef();

  @Nullable
  PsiElement getAbstract();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getOverride();

  @Nullable
  String getName();

  PsiElement setName(String name);

  @NotNull
  PsiElement getNameIdentifier();

  @NotNull
  ItemPresentation getPresentation();

  @NotNull
  SchemaRecordTypeDef getRecordTypeDef();

}
