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

public class EdlOpOutputMapModelProjectionImpl extends ASTWrapperPsiElement implements EdlOpOutputMapModelProjection {

  public EdlOpOutputMapModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpOutputMapModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EdlOpOutputKeyProjection getOpOutputKeyProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlOpOutputKeyProjection.class));
  }

  @Override
  @Nullable
  public EdlOpOutputVarProjection getOpOutputVarProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpOutputVarProjection.class);
  }

  @Override
  @Nullable
  public PsiElement getParenLeft() {
    return findChildByType(S_PAREN_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getParenRight() {
    return findChildByType(S_PAREN_RIGHT);
  }

}
