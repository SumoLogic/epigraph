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

public class EdlOpInputFieldProjectionEntryImpl extends ASTWrapperPsiElement implements EdlOpInputFieldProjectionEntry {

  public EdlOpInputFieldProjectionEntryImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpInputFieldProjectionEntry(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EdlOpInputFieldProjection getOpInputFieldProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlOpInputFieldProjection.class));
  }

  @Override
  @NotNull
  public EdlQid getQid() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlQid.class));
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(E_PLUS);
  }

}
