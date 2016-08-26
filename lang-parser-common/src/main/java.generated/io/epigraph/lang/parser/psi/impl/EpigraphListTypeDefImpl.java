// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import io.epigraph.lang.parser.psi.*;
import io.epigraph.lang.parser.psi.stubs.EpigraphListTypeDefStub;
import com.intellij.psi.stubs.IStubElementType;

public class EpigraphListTypeDefImpl extends EpigraphListTypeDefImplBase implements EpigraphListTypeDef {

  public EpigraphListTypeDefImpl(ASTNode node) {
    super(node);
  }

  public EpigraphListTypeDefImpl(EpigraphListTypeDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitListTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EpigraphAnonList getAnonList() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EpigraphAnonList.class));
  }

  @Override
  @Nullable
  public EpigraphExtendsDecl getExtendsDecl() {
    return PsiTreeUtil.getChildOfType(this, EpigraphExtendsDecl.class);
  }

  @Override
  @Nullable
  public EpigraphListTypeBody getListTypeBody() {
    return PsiTreeUtil.getChildOfType(this, EpigraphListTypeBody.class);
  }

  @Override
  @Nullable
  public EpigraphMetaDecl getMetaDecl() {
    return PsiTreeUtil.getChildOfType(this, EpigraphMetaDecl.class);
  }

  @Override
  @Nullable
  public EpigraphQid getQid() {
    return PsiTreeUtil.getChildOfType(this, EpigraphQid.class);
  }

  @Override
  @Nullable
  public EpigraphSupplementsDecl getSupplementsDecl() {
    return PsiTreeUtil.getChildOfType(this, EpigraphSupplementsDecl.class);
  }

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(E_ABSTRACT);
  }

}
