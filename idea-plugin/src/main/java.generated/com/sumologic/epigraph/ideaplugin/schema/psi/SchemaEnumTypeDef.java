// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaEnumTypeDefStub;

public interface SchemaEnumTypeDef extends SchemaTypeDefElement, StubBasedPsiElement<SchemaEnumTypeDefStub> {

  @Nullable
  SchemaEnumTypeBody getEnumTypeBody();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @NotNull
  PsiElement getEnum();

  @Nullable
  PsiElement getId();

}
