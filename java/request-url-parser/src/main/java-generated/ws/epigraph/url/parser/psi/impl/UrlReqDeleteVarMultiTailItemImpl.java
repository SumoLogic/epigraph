// This is a generated file. Not intended for manual editing.
package ws.epigraph.url.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.url.lexer.UrlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.url.parser.psi.*;

public class UrlReqDeleteVarMultiTailItemImpl extends ASTWrapperPsiElement implements UrlReqDeleteVarMultiTailItem {

  public UrlReqDeleteVarMultiTailItemImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitReqDeleteVarMultiTailItem(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public UrlReqDeleteVarProjection getReqDeleteVarProjection() {
    return findNotNullChildByClass(UrlReqDeleteVarProjection.class);
  }

  @Override
  @NotNull
  public UrlTypeRef getTypeRef() {
    return findNotNullChildByClass(UrlTypeRef.class);
  }

}
