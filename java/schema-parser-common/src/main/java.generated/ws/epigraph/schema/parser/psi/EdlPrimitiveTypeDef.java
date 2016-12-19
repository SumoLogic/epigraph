// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.schema.parser.psi.stubs.EdlPrimitiveTypeDefStub;

public interface EdlPrimitiveTypeDef extends EdlTypeDef, StubBasedPsiElement<EdlPrimitiveTypeDefStub> {

  @Nullable
  EdlExtendsDecl getExtendsDecl();

  @Nullable
  EdlMetaDecl getMetaDecl();

  @Nullable
  EdlPrimitiveTypeBody getPrimitiveTypeBody();

  @Nullable
  EdlQid getQid();

  @Nullable
  EdlSupplementsDecl getSupplementsDecl();

  @Nullable
  PsiElement getAbstract();

  @Nullable
  PsiElement getBooleanT();

  @Nullable
  PsiElement getDoubleT();

  @Nullable
  PsiElement getIntegerT();

  @Nullable
  PsiElement getLongT();

  @Nullable
  PsiElement getStringT();

  @NotNull
  PrimitiveTypeKind getPrimitiveTypeKind();

}
