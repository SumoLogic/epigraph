// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.url.lexer.UrlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.url.parser.psi.*;

public class UrlCustomUrlImpl extends ASTWrapperPsiElement implements UrlCustomUrl {

  public UrlCustomUrlImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitCustomUrl(this);
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
  @NotNull
  public List<UrlRequestParam> getRequestParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, UrlRequestParam.class);
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
