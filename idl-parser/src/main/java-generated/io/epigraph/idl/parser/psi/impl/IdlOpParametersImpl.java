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

public class IdlOpParametersImpl extends ASTWrapperPsiElement implements IdlOpParameters {

  public IdlOpParametersImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpParameters(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlOpParamProjection> getOpParamProjectionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlOpParamProjection.class);
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return findNotNullChildByType(I_COLON);
  }

  @Override
  @NotNull
  public PsiElement getCurlyLeft() {
    return findNotNullChildByType(I_CURLY_LEFT);
  }

  @Override
  @NotNull
  public PsiElement getCurlyRight() {
    return findNotNullChildByType(I_CURLY_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getParameters() {
    return findNotNullChildByType(I_PARAMETERS);
  }

}
