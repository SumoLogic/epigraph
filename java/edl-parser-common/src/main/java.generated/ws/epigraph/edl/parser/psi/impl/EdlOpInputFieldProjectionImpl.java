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

public class EdlOpInputFieldProjectionImpl extends ASTWrapperPsiElement implements EdlOpInputFieldProjection {

  public EdlOpInputFieldProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpInputFieldProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlOpInputFieldProjectionBodyPart> getOpInputFieldProjectionBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlOpInputFieldProjectionBodyPart.class);
  }

  @Override
  @NotNull
  public EdlOpInputVarProjection getOpInputVarProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlOpInputVarProjection.class));
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(S_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(S_CURLY_RIGHT);
  }

}
