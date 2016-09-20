// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.idl.lexer.IdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.idl.parser.psi.*;

public class IdlOpParamImpl extends ASTWrapperPsiElement implements IdlOpParam {

  public IdlOpParamImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpParam(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlCustomParam> getCustomParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlCustomParam.class);
  }

  @Override
  @Nullable
  public IdlDatum getDatum() {
    return findChildByClass(IdlDatum.class);
  }

  @Override
  @Nullable
  public IdlFqnTypeRef getFqnTypeRef() {
    return findChildByClass(IdlFqnTypeRef.class);
  }

  @Override
  @Nullable
  public IdlOpInputModelProjection getOpInputModelProjection() {
    return findChildByClass(IdlOpInputModelProjection.class);
  }

  @Override
  @Nullable
  public IdlQid getQid() {
    return findChildByClass(IdlQid.class);
  }

  @Override
  @Nullable
  public PsiElement getColon() {
    return findChildByType(I_COLON);
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(I_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(I_CURLY_RIGHT);
  }

  @Override
  @Nullable
  public PsiElement getEq() {
    return findChildByType(I_EQ);
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(I_PLUS);
  }

  @Override
  @NotNull
  public PsiElement getSemicolon() {
    return findNotNullChildByType(I_SEMICOLON);
  }

}
