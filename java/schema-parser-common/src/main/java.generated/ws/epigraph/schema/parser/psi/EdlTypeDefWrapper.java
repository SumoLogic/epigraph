// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.schema.parser.psi.stubs.EdlTypeDefWrapperStub;

public interface EdlTypeDefWrapper extends PsiElement, StubBasedPsiElement<EdlTypeDefWrapperStub> {

  @Nullable
  EdlEnumTypeDef getEnumTypeDef();

  @Nullable
  EdlListTypeDef getListTypeDef();

  @Nullable
  EdlMapTypeDef getMapTypeDef();

  @Nullable
  EdlPrimitiveTypeDef getPrimitiveTypeDef();

  @Nullable
  EdlRecordTypeDef getRecordTypeDef();

  @Nullable
  EdlVarTypeDef getVarTypeDef();

  @NotNull
  EdlTypeDef getElement();

  void delete();

  @NotNull
  String toString();

}
