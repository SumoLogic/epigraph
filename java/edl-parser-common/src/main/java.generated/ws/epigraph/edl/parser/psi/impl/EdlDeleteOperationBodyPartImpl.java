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

public class EdlDeleteOperationBodyPartImpl extends ASTWrapperPsiElement implements EdlDeleteOperationBodyPart {

  public EdlDeleteOperationBodyPartImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitDeleteOperationBodyPart(this);
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
  public EdlOperationDeleteProjection getOperationDeleteProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOperationDeleteProjection.class);
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
