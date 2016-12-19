// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.EdlElementTypes.*;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.schema.parser.psi.stubs.EdlVarTypeDefStub;
import com.intellij.psi.stubs.IStubElementType;

public class EdlVarTypeDefImpl extends EdlVarTypeDefImplBase implements EdlVarTypeDef {

  public EdlVarTypeDefImpl(ASTNode node) {
    super(node);
  }

  public EdlVarTypeDefImpl(EdlVarTypeDefStub stub, IStubElementType type) {
    super(stub, type);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitVarTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlExtendsDecl getExtendsDecl() {
    return PsiTreeUtil.getChildOfType(this, EdlExtendsDecl.class);
  }

  @Override
  @Nullable
  public EdlQid getQid() {
    return PsiTreeUtil.getChildOfType(this, EdlQid.class);
  }

  @Override
  @Nullable
  public EdlSupplementsDecl getSupplementsDecl() {
    return PsiTreeUtil.getChildOfType(this, EdlSupplementsDecl.class);
  }

  @Override
  @Nullable
  public EdlVarTypeBody getVarTypeBody() {
    return PsiTreeUtil.getChildOfType(this, EdlVarTypeBody.class);
  }

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(S_ABSTRACT);
  }

  @Override
  @NotNull
  public PsiElement getVartype() {
    return notNullChild(findChildByType(S_VARTYPE));
  }

  @NotNull
  public List<EdlTypeDef> supplemented() {
    return EdlPsiImplUtil.supplemented(this);
  }

}
