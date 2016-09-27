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

public class IdlOpInputComaMapModelProjectionImpl extends ASTWrapperPsiElement implements IdlOpInputComaMapModelProjection {

  public IdlOpInputComaMapModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpInputComaMapModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public IdlOpInputComaKeyProjection getOpInputComaKeyProjection() {
    return findNotNullChildByClass(IdlOpInputComaKeyProjection.class);
  }

  @Override
  @NotNull
  public IdlOpInputComaVarProjection getOpInputComaVarProjection() {
    return findNotNullChildByClass(IdlOpInputComaVarProjection.class);
  }

  @Override
  @NotNull
  public PsiElement getParenLeft() {
    return findNotNullChildByType(I_PAREN_LEFT);
  }

  @Override
  @NotNull
  public PsiElement getParenRight() {
    return findNotNullChildByType(I_PAREN_RIGHT);
  }

}
