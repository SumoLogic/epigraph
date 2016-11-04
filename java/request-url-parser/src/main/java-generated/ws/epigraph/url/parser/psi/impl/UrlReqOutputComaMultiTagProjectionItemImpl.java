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

public class UrlReqOutputComaMultiTagProjectionItemImpl extends ASTWrapperPsiElement implements UrlReqOutputComaMultiTagProjectionItem {

  public UrlReqOutputComaMultiTagProjectionItemImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitReqOutputComaMultiTagProjectionItem(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<UrlReqAnnotation> getReqAnnotationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, UrlReqAnnotation.class);
  }

  @Override
  @NotNull
  public UrlReqOutputComaModelProjection getReqOutputComaModelProjection() {
    return findNotNullChildByClass(UrlReqOutputComaModelProjection.class);
  }

  @Override
  @Nullable
  public UrlReqOutputModelMeta getReqOutputModelMeta() {
    return findChildByClass(UrlReqOutputModelMeta.class);
  }

  @Override
  @NotNull
  public List<UrlReqParam> getReqParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, UrlReqParam.class);
  }

  @Override
  @NotNull
  public UrlTagName getTagName() {
    return findNotNullChildByClass(UrlTagName.class);
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(U_PLUS);
  }

}
