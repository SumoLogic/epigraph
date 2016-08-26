// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.lang.parser.psi.stubs.EpigraphListTypeDefStub;

public interface EpigraphListTypeDef extends EpigraphTypeDef, StubBasedPsiElement<EpigraphListTypeDefStub> {

  @NotNull
  SchemaAnonList getAnonList();

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaListTypeBody getListTypeBody();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaQid getQid();

  @Nullable
  SchemaSupplementsDecl getSupplementsDecl();

  @Nullable
  PsiElement getAbstract();

}
