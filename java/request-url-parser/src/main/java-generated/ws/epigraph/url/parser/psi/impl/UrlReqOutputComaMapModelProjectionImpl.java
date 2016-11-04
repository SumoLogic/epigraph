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

public class UrlReqOutputComaMapModelProjectionImpl extends ASTWrapperPsiElement implements UrlReqOutputComaMapModelProjection {

  public UrlReqOutputComaMapModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitReqOutputComaMapModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public UrlReqOutputComaKeysProjection getReqOutputComaKeysProjection() {
    return findNotNullChildByClass(UrlReqOutputComaKeysProjection.class);
  }

  @Override
  @Nullable
  public UrlReqOutputComaVarProjection getReqOutputComaVarProjection() {
    return findChildByClass(UrlReqOutputComaVarProjection.class);
  }

  @Override
  @Nullable
  public PsiElement getParenLeft() {
    return findChildByType(U_PAREN_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getParenRight() {
    return findChildByType(U_PAREN_RIGHT);
  }

}
