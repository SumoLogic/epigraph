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

public class IdlOpMapModelPathImpl extends ASTWrapperPsiElement implements IdlOpMapModelPath {

  public IdlOpMapModelPathImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpMapModelPath(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlOpPathKeyProjection getOpPathKeyProjection() {
    return findChildByClass(IdlOpPathKeyProjection.class);
  }

  @Override
  @NotNull
  public IdlOpVarPath getOpVarPath() {
    return findNotNullChildByClass(IdlOpVarPath.class);
  }

  @Override
  @NotNull
  public PsiElement getSlash() {
    return findNotNullChildByType(I_SLASH);
  }

  @Override
  @NotNull
  public PsiElement getStar() {
    return findNotNullChildByType(I_STAR);
  }

}
