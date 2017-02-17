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

public class UrlReqDeleteModelProjectionImpl extends ASTWrapperPsiElement implements UrlReqDeleteModelProjection {

  public UrlReqDeleteModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitReqDeleteModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public UrlReqDeleteListModelProjection getReqDeleteListModelProjection() {
    return findChildByClass(UrlReqDeleteListModelProjection.class);
  }

  @Override
  @Nullable
  public UrlReqDeleteMapModelProjection getReqDeleteMapModelProjection() {
    return findChildByClass(UrlReqDeleteMapModelProjection.class);
  }

  @Override
  @Nullable
  public UrlReqDeleteModelPolymorphicTail getReqDeleteModelPolymorphicTail() {
    return findChildByClass(UrlReqDeleteModelPolymorphicTail.class);
  }

  @Override
  @Nullable
  public UrlReqDeleteRecordModelProjection getReqDeleteRecordModelProjection() {
    return findChildByClass(UrlReqDeleteRecordModelProjection.class);
  }

}
