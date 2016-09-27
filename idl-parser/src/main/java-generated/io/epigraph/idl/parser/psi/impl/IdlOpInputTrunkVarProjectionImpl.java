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

public class IdlOpInputTrunkVarProjectionImpl extends ASTWrapperPsiElement implements IdlOpInputTrunkVarProjection {

  public IdlOpInputTrunkVarProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpInputTrunkVarProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlOpInputComaMultiTagProjection getOpInputComaMultiTagProjection() {
    return findChildByClass(IdlOpInputComaMultiTagProjection.class);
  }

  @Override
  @Nullable
  public IdlOpInputTrunkSingleTagProjection getOpInputTrunkSingleTagProjection() {
    return findChildByClass(IdlOpInputTrunkSingleTagProjection.class);
  }

  @Override
  @Nullable
  public IdlOpInputVarPolymorphicTail getOpInputVarPolymorphicTail() {
    return findChildByClass(IdlOpInputVarPolymorphicTail.class);
  }

}
