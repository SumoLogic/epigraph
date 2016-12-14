// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.SchemaElementTypes.*;
import ws.epigraph.edl.parser.psi.*;
import ws.epigraph.edl.parser.psi.stubs.SchemaEnumTypeDefStub;
import com.intellij.psi.stubs.IStubElementType;

public class SchemaEnumTypeDefImpl extends SchemaEnumTypeDefImplBase implements SchemaEnumTypeDef {

  public SchemaEnumTypeDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaEnumTypeDefImpl(SchemaEnumTypeDefStub stub, IStubElementType type) {
    super(stub, type);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitEnumTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaEnumTypeBody getEnumTypeBody() {
    return PsiTreeUtil.getChildOfType(this, SchemaEnumTypeBody.class);
  }

  @Override
  @Nullable
  public SchemaMetaDecl getMetaDecl() {
    return PsiTreeUtil.getChildOfType(this, SchemaMetaDecl.class);
  }

  @Override
  @Nullable
  public SchemaQid getQid() {
    return PsiTreeUtil.getChildOfType(this, SchemaQid.class);
  }

  @Override
  @NotNull
  public PsiElement getEnum() {
    return notNullChild(findChildByType(S_ENUM));
  }

}
