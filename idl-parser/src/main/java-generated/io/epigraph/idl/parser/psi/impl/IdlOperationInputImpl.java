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

public class IdlOperationInputImpl extends ASTWrapperPsiElement implements IdlOperationInput {

  public IdlOperationInputImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOperationInput(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlOpInputTrunkVarProjection getOpInputTrunkVarProjection() {
    return findChildByClass(IdlOpInputTrunkVarProjection.class);
  }

  @Override
  @NotNull
  public PsiElement getInput() {
    return findNotNullChildByType(I_INPUT);
  }

}
