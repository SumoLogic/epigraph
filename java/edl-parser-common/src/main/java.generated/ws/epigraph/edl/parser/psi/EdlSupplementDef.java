// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.edl.parser.psi.stubs.EdlSupplementDefStub;
import com.intellij.navigation.ItemPresentation;

public interface EdlSupplementDef extends PsiElement, StubBasedPsiElement<EdlSupplementDefStub> {

  @NotNull
  List<EdlQnTypeRef> getQnTypeRefList();

  @NotNull
  PsiElement getSupplement();

  @Nullable
  PsiElement getWith();

  @Nullable
  EdlQnTypeRef sourceRef();

  @NotNull
  List<EdlQnTypeRef> supplementedRefs();

  @Nullable
  EdlTypeDef source();

  @NotNull
  List<EdlTypeDef> supplemented();

  @NotNull
  ItemPresentation getPresentation();

  @NotNull
  String toString();

}
