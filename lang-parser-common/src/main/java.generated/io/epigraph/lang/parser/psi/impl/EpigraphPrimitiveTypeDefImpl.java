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
import io.epigraph.lang.parser.psi.stubs.EpigraphPrimitiveTypeDefStub;
import com.intellij.psi.stubs.IStubElementType;

public class EpigraphPrimitiveTypeDefImpl extends EpigraphPrimitiveTypeDefImplBase implements EpigraphPrimitiveTypeDef {

  public EpigraphPrimitiveTypeDefImpl(ASTNode node) {
    super(node);
  }

  public EpigraphPrimitiveTypeDefImpl(EpigraphPrimitiveTypeDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitPrimitiveTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EpigraphExtendsDecl getExtendsDecl() {
    return PsiTreeUtil.getChildOfType(this, EpigraphExtendsDecl.class);
  }

  @Override
  @Nullable
  public EpigraphMetaDecl getMetaDecl() {
    return PsiTreeUtil.getChildOfType(this, EpigraphMetaDecl.class);
  }

  @Override
  @Nullable
  public EpigraphPrimitiveTypeBody getPrimitiveTypeBody() {
    return PsiTreeUtil.getChildOfType(this, EpigraphPrimitiveTypeBody.class);
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

  @Override
  @Nullable
  public PsiElement getBooleanT() {
    return findChildByType(E_BOOLEAN_T);
  }

  @Override
  @Nullable
  public PsiElement getDoubleT() {
    return findChildByType(E_DOUBLE_T);
  }

  @Override
  @Nullable
  public PsiElement getIntegerT() {
    return findChildByType(E_INTEGER_T);
  }

  @Override
  @Nullable
  public PsiElement getLongT() {
    return findChildByType(E_LONG_T);
  }

  @Override
  @Nullable
  public PsiElement getStringT() {
    return findChildByType(E_STRING_T);
  }

  @NotNull
  public PrimitiveTypeKind getPrimitiveTypeKind() {
    return EpigraphPsiImplUtil.getPrimitiveTypeKind(this);
  }

}
