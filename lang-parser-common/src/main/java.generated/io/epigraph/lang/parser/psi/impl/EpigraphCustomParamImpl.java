// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.lang.parser.psi.*;

public class EpigraphCustomParamImpl extends ASTWrapperPsiElement implements EpigraphCustomParam {

  public EpigraphCustomParamImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitCustomParam(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EpigraphDataValue getDataValue() {
    return PsiTreeUtil.getChildOfType(this, EpigraphDataValue.class);
  }

  @Override
  @NotNull
  public EpigraphQid getQid() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EpigraphQid.class));
  }

  @Override
  @NotNull
  public PsiElement getEq() {
    return notNullChild(findChildByType(E_EQ));
  }

  @Nullable
  public String getName() {
    return EpigraphPsiImplUtil.getName(this);
  }

  public PsiElement setName(String name) {
    return EpigraphPsiImplUtil.setName(this, name);
  }

  @NotNull
  public PsiElement getNameIdentifier() {
    return EpigraphPsiImplUtil.getNameIdentifier(this);
  }

}
