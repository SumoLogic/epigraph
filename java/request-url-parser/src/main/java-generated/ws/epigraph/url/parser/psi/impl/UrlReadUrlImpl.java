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

public class UrlReadUrlImpl extends ASTWrapperPsiElement implements UrlReadUrl {

  public UrlReadUrlImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitReadUrl(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public UrlQid getQid() {
    return findNotNullChildByClass(UrlQid.class);
  }

  @Override
  @NotNull
  public UrlReqOutputTrunkFieldProjection getReqOutputTrunkFieldProjection() {
    return findNotNullChildByClass(UrlReqOutputTrunkFieldProjection.class);
  }

  @Override
  @NotNull
  public List<UrlRequestParam> getRequestParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, UrlRequestParam.class);
  }

  @Override
  @NotNull
  public PsiElement getSlash() {
    return findNotNullChildByType(U_SLASH);
  }

}
