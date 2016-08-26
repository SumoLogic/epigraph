// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.lang.parser.psi.stubs.EpigraphListTypeDefStub;

public interface EpigraphListTypeDef extends EpigraphTypeDef, StubBasedPsiElement<EpigraphListTypeDefStub> {

  @NotNull
  EpigraphAnonList getAnonList();

  @Nullable
  EpigraphExtendsDecl getExtendsDecl();

  @Nullable
  EpigraphListTypeBody getListTypeBody();

  @Nullable
  EpigraphMetaDecl getMetaDecl();

  @Nullable
  EpigraphQid getQid();

  @Nullable
  EpigraphSupplementsDecl getSupplementsDecl();

  @Nullable
  PsiElement getAbstract();

}
