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

public class EdlOpDeleteMapModelProjectionImpl extends ASTWrapperPsiElement implements EdlOpDeleteMapModelProjection {

  public EdlOpDeleteMapModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpDeleteMapModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EdlOpDeleteKeyProjection getOpDeleteKeyProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlOpDeleteKeyProjection.class));
  }

  @Override
  @Nullable
  public EdlOpDeleteVarProjection getOpDeleteVarProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpDeleteVarProjection.class);
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
