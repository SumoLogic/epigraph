// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.EdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.edl.parser.psi.*;

public class EdlCustomOperationBodyPartImpl extends ASTWrapperPsiElement implements EdlCustomOperationBodyPart {

  public EdlCustomOperationBodyPartImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitCustomOperationBodyPart(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlAnnotation getAnnotation() {
    return PsiTreeUtil.getChildOfType(this, EdlAnnotation.class);
  }

  @Override
  @Nullable
  public EdlOperationInputProjection getOperationInputProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOperationInputProjection.class);
  }

  @Override
  @Nullable
  public EdlOperationInputType getOperationInputType() {
    return PsiTreeUtil.getChildOfType(this, EdlOperationInputType.class);
  }

  @Override
  @Nullable
  public EdlOperationMethod getOperationMethod() {
    return PsiTreeUtil.getChildOfType(this, EdlOperationMethod.class);
  }

  @Override
  @Nullable
  public EdlOperationOutputProjection getOperationOutputProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOperationOutputProjection.class);
  }

  @Override
  @Nullable
  public EdlOperationOutputType getOperationOutputType() {
    return PsiTreeUtil.getChildOfType(this, EdlOperationOutputType.class);
  }

  @Override
  @Nullable
  public EdlOperationPath getOperationPath() {
    return PsiTreeUtil.getChildOfType(this, EdlOperationPath.class);
  }

}
