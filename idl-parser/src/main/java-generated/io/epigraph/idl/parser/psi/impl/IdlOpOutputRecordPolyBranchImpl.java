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

public class IdlOpOutputRecordPolyBranchImpl extends ASTWrapperPsiElement implements IdlOpOutputRecordPolyBranch {

  public IdlOpOutputRecordPolyBranchImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpOutputRecordPolyBranch(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlFqnTypeRef getFqnTypeRef() {
    return findChildByClass(IdlFqnTypeRef.class);
  }

  @Override
  @Nullable
  public IdlOpOutputModelProjectionBody getOpOutputModelProjectionBody() {
    return findChildByClass(IdlOpOutputModelProjectionBody.class);
  }

  @Override
  @Nullable
  public IdlOpOutputRecordModelProjection getOpOutputRecordModelProjection() {
    return findChildByClass(IdlOpOutputRecordModelProjection.class);
  }

  @Override
  @Nullable
  public PsiElement getAngleLeft() {
    return findChildByType(I_ANGLE_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getAngleRight() {
    return findChildByType(I_ANGLE_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getTilda() {
    return findNotNullChildByType(I_TILDA);
  }

}
