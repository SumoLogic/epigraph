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

public class EdlOpInputMapModelProjectionImpl extends ASTWrapperPsiElement implements EdlOpInputMapModelProjection {

  public EdlOpInputMapModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpInputMapModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EdlOpInputKeyProjection getOpInputKeyProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlOpInputKeyProjection.class));
  }

  @Override
  @Nullable
  public EdlOpInputVarProjection getOpInputVarProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpInputVarProjection.class);
  }

  @Override
  @Nullable
  public PsiElement getParenLeft() {
    return findChildByType(E_PAREN_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getParenRight() {
    return findChildByType(E_PAREN_RIGHT);
  }

}
