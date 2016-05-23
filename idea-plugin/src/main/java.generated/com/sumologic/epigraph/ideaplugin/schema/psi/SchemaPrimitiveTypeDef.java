// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaPrimitiveTypeDefStub;

public interface SchemaPrimitiveTypeDef extends SchemaTypeDef, StubBasedPsiElement<SchemaPrimitiveTypeDefStub> {

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @NotNull
  SchemaPrimitiveKind getPrimitiveKind();

  @Nullable
  SchemaPrimitiveTypeBody getPrimitiveTypeBody();

  @Nullable
  PsiElement getAbstract();

  @Nullable
  PsiElement getPolymorphic();

  @Nullable
  PsiElement getId();

}
