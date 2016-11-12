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

public class UrlReqInputVarProjectionImpl extends ASTWrapperPsiElement implements UrlReqInputVarProjection {

  public UrlReqInputVarProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitReqInputVarProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public UrlReqInputMultiTagProjection getReqInputMultiTagProjection() {
    return findChildByClass(UrlReqInputMultiTagProjection.class);
  }

  @Override
  @Nullable
  public UrlReqInputSingleTagProjection getReqInputSingleTagProjection() {
    return findChildByClass(UrlReqInputSingleTagProjection.class);
  }

  @Override
  @Nullable
  public UrlReqInputVarPolymorphicTail getReqInputVarPolymorphicTail() {
    return findChildByClass(UrlReqInputVarPolymorphicTail.class);
  }

}
