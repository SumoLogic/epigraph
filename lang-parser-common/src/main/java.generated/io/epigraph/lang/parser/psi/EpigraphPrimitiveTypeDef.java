// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.lang.parser.psi.stubs.EpigraphPrimitiveTypeDefStub;

public interface EpigraphPrimitiveTypeDef extends EpigraphTypeDef, StubBasedPsiElement<EpigraphPrimitiveTypeDefStub> {

  @Nullable
  EpigraphExtendsDecl getExtendsDecl();

  @Nullable
  EpigraphMetaDecl getMetaDecl();

  @Nullable
  EpigraphPrimitiveTypeBody getPrimitiveTypeBody();

  @Nullable
  EpigraphQid getQid();

  @Nullable
  EpigraphSupplementsDecl getSupplementsDecl();

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
  PsiElement getStringT();

  @NotNull
  PrimitiveTypeKind getPrimitiveTypeKind();

}
