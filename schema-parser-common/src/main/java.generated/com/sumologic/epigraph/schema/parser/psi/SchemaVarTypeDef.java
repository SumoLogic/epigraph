// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaVarTypeDefStub;

public interface SchemaVarTypeDef extends SchemaTypeDef, StubBasedPsiElement<SchemaVarTypeDefStub> {

  @Nullable
  SchemaDefaultOverride getDefaultOverride();

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaSupplementsDecl getSupplementsDecl();

  @Nullable
  SchemaVarTypeBody getVarTypeBody();

  @NotNull
  PsiElement getVartype();

  @Nullable
  PsiElement getId();

  @NotNull
  List<SchemaTypeDef> supplemented();

}
