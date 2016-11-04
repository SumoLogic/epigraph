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

public class UrlDataImpl extends ASTWrapperPsiElement implements UrlData {

  public UrlDataImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitData(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<UrlDataEntry> getDataEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, UrlDataEntry.class);
  }

  @Override
  @Nullable
  public UrlTypeRef getTypeRef() {
    return findChildByClass(UrlTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getAngleLeft() {
    return findNotNullChildByType(U_ANGLE_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getAngleRight() {
    return findChildByType(U_ANGLE_RIGHT);
  }

}
