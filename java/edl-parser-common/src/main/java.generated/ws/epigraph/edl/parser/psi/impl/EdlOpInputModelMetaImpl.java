// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.EdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.edl.parser.psi.*;

public class EdlOpInputModelMetaImpl extends ASTWrapperPsiElement implements EdlOpInputModelMeta {

  public EdlOpInputModelMetaImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpInputModelMeta(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EdlOpInputModelProjection getOpInputModelProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlOpInputModelProjection.class));
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return notNullChild(findChildByType(E_COLON));
  }

  @Override
  @NotNull
  public PsiElement getMeta() {
    return notNullChild(findChildByType(E_META));
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(E_PLUS);
  }

}
