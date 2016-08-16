// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaPrimitiveTypeDefStub;

public interface SchemaPrimitiveTypeDef extends SchemaTypeDef, StubBasedPsiElement<SchemaPrimitiveTypeDefStub> {

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaPrimitiveTypeBody getPrimitiveTypeBody();

  @Nullable
  SchemaQid getQid();

  @Nullable
  SchemaSupplementsDecl getSupplementsDecl();

  @Nullable
  PsiElement getAbstract();

  @Nullable
  PsiElement getBooleanT();

  @Nullable
  PsiElement getDoubleT();

  @Nullable
  PsiElement getIntegerT();

  @Nullable
  PsiElement getLongT();

  @Nullable
  PsiElement getPolymorphic();

  @Nullable
  PsiElement getStringT();

  @NotNull
  PrimitiveTypeKind getPrimitiveTypeKind();

}
