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

public class EpigraphOpOutputMapModelProjectionImpl extends ASTWrapperPsiElement implements EpigraphOpOutputMapModelProjection {

  public EpigraphOpOutputMapModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitOpOutputMapModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EpigraphOpOutputKeyProjection getOpOutputKeyProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EpigraphOpOutputKeyProjection.class));
  }

  @Override
  @Nullable
  public EpigraphOpOutputMapPolyBranch getOpOutputMapPolyBranch() {
    return PsiTreeUtil.getChildOfType(this, EpigraphOpOutputMapPolyBranch.class);
  }

  @Override
  @Nullable
  public EpigraphOpOutputVarProjection getOpOutputVarProjection() {
    return PsiTreeUtil.getChildOfType(this, EpigraphOpOutputVarProjection.class);
  }

  @Override
  @Nullable
  public PsiElement getParenLeft() {
    return findChildByType(E_PAREN_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getParenRight() {
    return findChildByType(E_PAREN_RIGHT);
  }

}
