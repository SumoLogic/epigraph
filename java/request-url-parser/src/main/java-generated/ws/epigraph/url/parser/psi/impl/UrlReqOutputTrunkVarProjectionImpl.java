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

public class UrlReqOutputTrunkVarProjectionImpl extends ASTWrapperPsiElement implements UrlReqOutputTrunkVarProjection {

  public UrlReqOutputTrunkVarProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitReqOutputTrunkVarProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public UrlReqOutputComaMultiTagProjection getReqOutputComaMultiTagProjection() {
    return findChildByClass(UrlReqOutputComaMultiTagProjection.class);
  }

  @Override
  @Nullable
  public UrlReqOutputStarTagProjection getReqOutputStarTagProjection() {
    return findChildByClass(UrlReqOutputStarTagProjection.class);
  }

  @Override
  @Nullable
  public UrlReqOutputTrunkSingleTagProjection getReqOutputTrunkSingleTagProjection() {
    return findChildByClass(UrlReqOutputTrunkSingleTagProjection.class);
  }

  @Override
  @Nullable
  public UrlReqOutputVarPolymorphicTail getReqOutputVarPolymorphicTail() {
    return findChildByClass(UrlReqOutputVarPolymorphicTail.class);
  }

}
