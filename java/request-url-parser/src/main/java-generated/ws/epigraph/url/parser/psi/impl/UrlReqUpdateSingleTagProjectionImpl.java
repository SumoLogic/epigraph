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

public class UrlReqUpdateSingleTagProjectionImpl extends ASTWrapperPsiElement implements UrlReqUpdateSingleTagProjection {

  public UrlReqUpdateSingleTagProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitReqUpdateSingleTagProjection(this);
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
  public List<UrlReqParam> getReqParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, UrlReqParam.class);
  }

  @Override
  @Nullable
  public UrlReqUpdateModelMeta getReqUpdateModelMeta() {
    return findChildByClass(UrlReqUpdateModelMeta.class);
  }

  @Override
  @NotNull
  public UrlReqUpdateModelProjection getReqUpdateModelProjection() {
    return findNotNullChildByClass(UrlReqUpdateModelProjection.class);
  }

  @Override
  @Nullable
  public UrlTagName getTagName() {
    return findChildByClass(UrlTagName.class);
  }

  @Override
  @Nullable
  public PsiElement getColon() {
    return findChildByType(U_COLON);
  }

}
