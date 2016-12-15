// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.edl.parser.psi.stubs.EdlEnumTypeDefStub;

public interface EdlEnumTypeDef extends EdlTypeDef, StubBasedPsiElement<EdlEnumTypeDefStub> {

  @Nullable
  EdlEnumTypeBody getEnumTypeBody();

  @Nullable
  EdlMetaDecl getMetaDecl();

  @Nullable
  EdlQid getQid();

  @NotNull
  PsiElement getEnum();

}
