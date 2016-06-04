// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaRecordTypeDefStub;

public interface SchemaRecordTypeDef extends SchemaTypeDef, StubBasedPsiElement<SchemaRecordTypeDefStub> {

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaRecordTypeBody getRecordTypeBody();

  @Nullable
  SchemaSupplementsDecl getSupplementsDecl();

  @Nullable
  PsiElement getAbstract();

  @Nullable
  PsiElement getPolymorphic();

  @NotNull
  PsiElement getRecord();

  @Nullable
  PsiElement getId();

  @NotNull
  List<SchemaTypeDef> supplemented();

}
