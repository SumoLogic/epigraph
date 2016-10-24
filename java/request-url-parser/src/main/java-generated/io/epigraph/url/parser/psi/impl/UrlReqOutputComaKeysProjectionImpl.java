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

public class UrlReqOutputComaKeysProjectionImpl extends ASTWrapperPsiElement implements UrlReqOutputComaKeysProjection {

  public UrlReqOutputComaKeysProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitReqOutputComaKeysProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<UrlReqOutputComaKeyProjection> getReqOutputComaKeyProjectionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, UrlReqOutputComaKeyProjection.class);
  }

  @Override
  @NotNull
  public PsiElement getBracketLeft() {
    return findNotNullChildByType(U_BRACKET_LEFT);
  }

  @Override
  @NotNull
  public PsiElement getBracketRight() {
    return findNotNullChildByType(U_BRACKET_RIGHT);
  }

  @Override
  @Nullable
  public PsiElement getStar() {
    return findChildByType(U_STAR);
  }

}
