// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.EdlElementTypes.*;
import ws.epigraph.edl.parser.psi.*;
import ws.epigraph.edl.parser.psi.stubs.EdlEnumTypeDefStub;
import com.intellij.psi.stubs.IStubElementType;

public class EdlEnumTypeDefImpl extends EdlEnumTypeDefImplBase implements EdlEnumTypeDef {

  public EdlEnumTypeDefImpl(ASTNode node) {
    super(node);
  }

  public EdlEnumTypeDefImpl(EdlEnumTypeDefStub stub, IStubElementType type) {
    super(stub, type);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitEnumTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlEnumTypeBody getEnumTypeBody() {
    return PsiTreeUtil.getChildOfType(this, EdlEnumTypeBody.class);
  }

  @Override
  @Nullable
  public EdlMetaDecl getMetaDecl() {
    return PsiTreeUtil.getChildOfType(this, EdlMetaDecl.class);
  }

  @Override
  @Nullable
  public EdlQid getQid() {
    return PsiTreeUtil.getChildOfType(this, EdlQid.class);
  }

  @Override
  @NotNull
  public PsiElement getEnum() {
    return notNullChild(findChildByType(S_ENUM));
  }

}
