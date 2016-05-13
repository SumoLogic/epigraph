// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaRecordTypeDef extends SchemaTypeDef {

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaRecordSupplementsDecl getRecordSupplementsDecl();

  @Nullable
  SchemaRecordTypeBody getRecordTypeBody();

  @Nullable
  PsiElement getAbstract();

  @Nullable
  PsiElement getPolymorphic();

  @NotNull
  PsiElement getRecord();

  @Nullable
  PsiElement getId();

}
