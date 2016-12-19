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
import ws.epigraph.edl.parser.psi.stubs.EdlListTypeDefStub;
import com.intellij.psi.stubs.IStubElementType;

public class EdlListTypeDefImpl extends EdlListTypeDefImplBase implements EdlListTypeDef {

  public EdlListTypeDefImpl(ASTNode node) {
    super(node);
  }

  public EdlListTypeDefImpl(EdlListTypeDefStub stub, IStubElementType type) {
    super(stub, type);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitListTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EdlAnonList getAnonList() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlAnonList.class));
  }

  @Override
  @Nullable
  public EdlExtendsDecl getExtendsDecl() {
    return PsiTreeUtil.getChildOfType(this, EdlExtendsDecl.class);
  }

  @Override
  @Nullable
  public EdlListTypeBody getListTypeBody() {
    return PsiTreeUtil.getChildOfType(this, EdlListTypeBody.class);
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
  @Nullable
  public EdlSupplementsDecl getSupplementsDecl() {
    return PsiTreeUtil.getChildOfType(this, EdlSupplementsDecl.class);
  }

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(S_ABSTRACT);
  }

}
