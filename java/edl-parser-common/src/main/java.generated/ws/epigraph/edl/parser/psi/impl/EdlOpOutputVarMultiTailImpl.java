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

public class EdlOpOutputVarMultiTailImpl extends ASTWrapperPsiElement implements EdlOpOutputVarMultiTail {

  public EdlOpOutputVarMultiTailImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpOutputVarMultiTail(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlOpOutputVarMultiTailItem> getOpOutputVarMultiTailItemList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlOpOutputVarMultiTailItem.class);
  }

  @Override
  @NotNull
  public PsiElement getParenLeft() {
    return notNullChild(findChildByType(E_PAREN_LEFT));
  }

  @Override
  @Nullable
  public PsiElement getParenRight() {
    return findChildByType(E_PAREN_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getTilda() {
    return notNullChild(findChildByType(E_TILDA));
  }

}
