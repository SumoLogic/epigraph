// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.schema.parser.psi.stubs.SchemaRecordTypeDefStub;

public interface SchemaRecordTypeDef extends SchemaTypeDef, StubBasedPsiElement<SchemaRecordTypeDefStub> {

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaQid getQid();

  @Nullable
  SchemaRecordTypeBody getRecordTypeBody();

  @Nullable
  SchemaSupplementsDecl getSupplementsDecl();

  @Nullable
  PsiElement getAbstract();

  @NotNull
  PsiElement getRecord();

  @NotNull
  List<SchemaTypeDef> supplemented();

}
