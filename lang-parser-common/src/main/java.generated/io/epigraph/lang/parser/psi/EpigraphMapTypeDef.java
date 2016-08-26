// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.lang.parser.psi.stubs.EpigraphMapTypeDefStub;

public interface EpigraphMapTypeDef extends EpigraphTypeDef, StubBasedPsiElement<EpigraphMapTypeDefStub> {

  @NotNull
  EpigraphAnonMap getAnonMap();

  @Nullable
  EpigraphExtendsDecl getExtendsDecl();

  @Nullable
  EpigraphMapTypeBody getMapTypeBody();

  @Nullable
  EpigraphMetaDecl getMetaDecl();

  @Nullable
  EpigraphQid getQid();

  @Nullable
  EpigraphSupplementsDecl getSupplementsDecl();

  @Nullable
  PsiElement getAbstract();

}
