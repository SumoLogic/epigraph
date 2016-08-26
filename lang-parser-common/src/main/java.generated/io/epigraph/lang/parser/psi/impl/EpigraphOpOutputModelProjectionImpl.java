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

public class EpigraphOpOutputModelProjectionImpl extends ASTWrapperPsiElement implements EpigraphOpOutputModelProjection {

  public EpigraphOpOutputModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitOpOutputModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EpigraphOpOutputEnumModelProjection getOpOutputEnumModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EpigraphOpOutputEnumModelProjection.class);
  }

  @Override
  @Nullable
  public EpigraphOpOutputListModelProjection getOpOutputListModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EpigraphOpOutputListModelProjection.class);
  }

  @Override
  @Nullable
  public EpigraphOpOutputMapModelProjection getOpOutputMapModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EpigraphOpOutputMapModelProjection.class);
  }

  @Override
  @NotNull
  public EpigraphOpOutputModelProjectionBody getOpOutputModelProjectionBody() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EpigraphOpOutputModelProjectionBody.class));
  }

  @Override
  @Nullable
  public EpigraphOpOutputPrimitiveModelProjection getOpOutputPrimitiveModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EpigraphOpOutputPrimitiveModelProjection.class);
  }

  @Override
  @Nullable
  public EpigraphOpOutputRecordModelProjection getOpOutputRecordModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EpigraphOpOutputRecordModelProjection.class);
  }

}
