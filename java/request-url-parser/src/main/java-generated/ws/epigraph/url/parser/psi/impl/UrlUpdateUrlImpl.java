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

public class UrlUpdateUrlImpl extends ASTWrapperPsiElement implements UrlUpdateUrl {

  public UrlUpdateUrlImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitUpdateUrl(this);
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
  public UrlReqFieldPath getReqFieldPath() {
    return findNotNullChildByClass(UrlReqFieldPath.class);
  }

  @Override
  @Nullable
  public UrlReqOutputTrunkFieldProjection getReqOutputTrunkFieldProjection() {
    return findChildByClass(UrlReqOutputTrunkFieldProjection.class);
  }

  @Override
  @Nullable
  public UrlReqUpdateFieldProjection getReqUpdateFieldProjection() {
    return findChildByClass(UrlReqUpdateFieldProjection.class);
  }

  @Override
  @NotNull
  public List<UrlRequestParam> getRequestParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, UrlRequestParam.class);
  }

  @Override
  @Nullable
  public PsiElement getAngleLeft() {
    return findChildByType(U_ANGLE_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getAngleRight() {
    return findChildByType(U_ANGLE_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getSlash() {
    return findNotNullChildByType(U_SLASH);
  }

}
