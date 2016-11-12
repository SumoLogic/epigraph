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

public class UrlReqInputModelProjectionImpl extends ASTWrapperPsiElement implements UrlReqInputModelProjection {

  public UrlReqInputModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitReqInputModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public UrlReqInputListModelProjection getReqInputListModelProjection() {
    return findChildByClass(UrlReqInputListModelProjection.class);
  }

  @Override
  @Nullable
  public UrlReqInputMapModelProjection getReqInputMapModelProjection() {
    return findChildByClass(UrlReqInputMapModelProjection.class);
  }

  @Override
  @Nullable
  public UrlReqInputRecordModelProjection getReqInputRecordModelProjection() {
    return findChildByClass(UrlReqInputRecordModelProjection.class);
  }

}
