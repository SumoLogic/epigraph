// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.edl.parser.psi.stubs.EdlRecordTypeDefStub;

public interface EdlRecordTypeDef extends EdlTypeDef, StubBasedPsiElement<EdlRecordTypeDefStub> {

  @Nullable
  EdlExtendsDecl getExtendsDecl();

  @Nullable
  EdlMetaDecl getMetaDecl();

  @Nullable
  EdlQid getQid();

  @Nullable
  EdlRecordTypeBody getRecordTypeBody();

  @Nullable
  EdlSupplementsDecl getSupplementsDecl();

  @Nullable
  PsiElement getAbstract();

  @NotNull
  PsiElement getRecord();

  @NotNull
  List<EdlTypeDef> supplemented();

}
