// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.edl.parser.psi.stubs.SchemaEnumTypeDefStub;

public interface SchemaEnumTypeDef extends SchemaTypeDef, StubBasedPsiElement<SchemaEnumTypeDefStub> {

  @Nullable
  SchemaEnumTypeBody getEnumTypeBody();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaQid getQid();

  @NotNull
  PsiElement getEnum();

}
