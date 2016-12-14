// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.edl.parser.psi.stubs.SchemaSupplementDefStub;
import com.intellij.navigation.ItemPresentation;

public interface SchemaSupplementDef extends PsiElement, StubBasedPsiElement<SchemaSupplementDefStub> {

  @NotNull
  List<SchemaQnTypeRef> getQnTypeRefList();

  @NotNull
  PsiElement getSupplement();

  @Nullable
  PsiElement getWith();

  @Nullable
  SchemaQnTypeRef sourceRef();

  @NotNull
  List<SchemaQnTypeRef> supplementedRefs();

  @Nullable
  SchemaTypeDef source();

  @NotNull
  List<SchemaTypeDef> supplemented();

  @NotNull
  ItemPresentation getPresentation();

  @NotNull
  String toString();

}
