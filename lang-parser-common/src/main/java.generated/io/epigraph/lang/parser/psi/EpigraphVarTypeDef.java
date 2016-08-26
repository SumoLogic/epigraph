// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.lang.parser.psi.stubs.EpigraphVarTypeDefStub;

public interface EpigraphVarTypeDef extends EpigraphTypeDef, StubBasedPsiElement<EpigraphVarTypeDefStub> {

  @Nullable
  EpigraphDefaultOverride getDefaultOverride();

  @Nullable
  EpigraphExtendsDecl getExtendsDecl();

  @Nullable
  EpigraphQid getQid();

  @Nullable
  EpigraphSupplementsDecl getSupplementsDecl();

  @Nullable
  EpigraphVarTypeBody getVarTypeBody();

  @Nullable
  PsiElement getAbstract();

  @NotNull
  PsiElement getVartype();

  @NotNull
  List<EpigraphTypeDef> supplemented();

}
