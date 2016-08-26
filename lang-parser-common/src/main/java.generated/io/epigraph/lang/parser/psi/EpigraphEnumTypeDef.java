// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import io.epigraph.lang.parser.psi.stubs.EpigraphEnumTypeDefStub;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;

public interface EpigraphEnumTypeDef extends EpigraphTypeDef, StubBasedPsiElement<EpigraphEnumTypeDefStub> {

  @Nullable
  SchemaEnumTypeBody getEnumTypeBody();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaQid getQid();

  @NotNull
  PsiElement getEnum();

}
