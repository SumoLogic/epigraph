// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.lang.parser.psi.stubs.EpigraphEnumTypeDefStub;

public interface EpigraphEnumTypeDef extends EpigraphTypeDef, StubBasedPsiElement<EpigraphEnumTypeDefStub> {

  @Nullable
  EpigraphEnumTypeBody getEnumTypeBody();

  @Nullable
  EpigraphMetaDecl getMetaDecl();

  @Nullable
  EpigraphQid getQid();

  @NotNull
  PsiElement getEnum();

}
