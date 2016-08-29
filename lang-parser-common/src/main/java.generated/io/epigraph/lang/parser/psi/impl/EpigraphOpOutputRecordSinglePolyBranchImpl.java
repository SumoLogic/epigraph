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

public class EpigraphOpOutputRecordSinglePolyBranchImpl extends ASTWrapperPsiElement implements EpigraphOpOutputRecordSinglePolyBranch {

  public EpigraphOpOutputRecordSinglePolyBranchImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitOpOutputRecordSinglePolyBranch(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EpigraphFqnTypeRef getFqnTypeRef() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EpigraphFqnTypeRef.class));
  }

  @Override
  @NotNull
  public EpigraphOpOutputRecordModelProjection getOpOutputRecordModelProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EpigraphOpOutputRecordModelProjection.class));
  }

}
