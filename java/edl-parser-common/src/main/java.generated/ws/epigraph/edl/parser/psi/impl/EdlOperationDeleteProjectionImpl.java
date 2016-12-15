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

public class EdlOperationDeleteProjectionImpl extends ASTWrapperPsiElement implements EdlOperationDeleteProjection {

  public EdlOperationDeleteProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOperationDeleteProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpDeleteFieldProjection getOpDeleteFieldProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpDeleteFieldProjection.class);
  }

  @Override
  @NotNull
  public PsiElement getDeleteProjection() {
    return notNullChild(findChildByType(S_DELETE_PROJECTION));
  }

}
