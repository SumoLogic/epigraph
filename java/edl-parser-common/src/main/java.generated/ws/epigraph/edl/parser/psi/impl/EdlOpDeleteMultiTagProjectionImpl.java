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

public class EdlOpDeleteMultiTagProjectionImpl extends ASTWrapperPsiElement implements EdlOpDeleteMultiTagProjection {

  public EdlOpDeleteMultiTagProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpDeleteMultiTagProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlOpDeleteMultiTagProjectionItem> getOpDeleteMultiTagProjectionItemList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlOpDeleteMultiTagProjectionItem.class);
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return notNullChild(findChildByType(E_COLON));
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

}
