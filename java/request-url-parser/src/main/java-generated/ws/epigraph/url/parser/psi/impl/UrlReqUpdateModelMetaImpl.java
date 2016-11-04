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

public class UrlReqUpdateModelMetaImpl extends ASTWrapperPsiElement implements UrlReqUpdateModelMeta {

  public UrlReqUpdateModelMetaImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitReqUpdateModelMeta(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public UrlReqUpdateModelProjection getReqUpdateModelProjection() {
    return findNotNullChildByClass(UrlReqUpdateModelProjection.class);
  }

  @Override
  @NotNull
  public PsiElement getAt() {
    return findNotNullChildByType(U_AT);
  }

}
