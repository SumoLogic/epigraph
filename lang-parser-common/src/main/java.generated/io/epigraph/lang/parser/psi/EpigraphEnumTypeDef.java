// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.lang.parser.psi.stubs.SchemaEnumTypeDefStub;

public interface EpigraphEnumTypeDef extends EpigraphTypeDef, StubBasedPsiElement<SchemaEnumTypeDefStub> {

  @Nullable
  SchemaEnumTypeBody getEnumTypeBody();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaQid getQid();

  @NotNull
  PsiElement getEnum();

}
