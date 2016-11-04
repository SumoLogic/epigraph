// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.idl.lexer.IdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.idl.parser.psi.*;

public class IdlOpDeleteMultiTagProjectionImpl extends ASTWrapperPsiElement implements IdlOpDeleteMultiTagProjection {

  public IdlOpDeleteMultiTagProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpDeleteMultiTagProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlOpDeleteMultiTagProjectionItem> getOpDeleteMultiTagProjectionItemList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlOpDeleteMultiTagProjectionItem.class);
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return findNotNullChildByType(I_COLON);
  }

  @Override
  @NotNull
  public PsiElement getParenLeft() {
    return findNotNullChildByType(I_PAREN_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getParenRight() {
    return findChildByType(I_PAREN_RIGHT);
  }

}
