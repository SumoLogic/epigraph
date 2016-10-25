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

public class IdlOpInputMapModelProjectionImpl extends ASTWrapperPsiElement implements IdlOpInputMapModelProjection {

  public IdlOpInputMapModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpInputMapModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public IdlOpInputKeyProjection getOpInputKeyProjection() {
    return findNotNullChildByClass(IdlOpInputKeyProjection.class);
  }

  @Override
  @NotNull
  public IdlOpInputVarProjection getOpInputVarProjection() {
    return findNotNullChildByClass(IdlOpInputVarProjection.class);
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
