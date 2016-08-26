// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.lang.parser.psi.stubs.EpigraphSupplementDefStub;
import com.intellij.navigation.ItemPresentation;

public interface EpigraphSupplementDef extends PsiElement, StubBasedPsiElement<EpigraphSupplementDefStub> {

  @NotNull
  List<EpigraphFqnTypeRef> getFqnTypeRefList();

  @NotNull
  PsiElement getSupplement();

  @Nullable
  PsiElement getWith();

  @Nullable
  EpigraphFqnTypeRef sourceRef();

  @NotNull
  List<EpigraphFqnTypeRef> supplementedRefs();

  @Nullable
  EpigraphTypeDef source();

  @NotNull
  List<EpigraphTypeDef> supplemented();

  @NotNull
  ItemPresentation getPresentation();

  @NotNull
  String toString();

}
