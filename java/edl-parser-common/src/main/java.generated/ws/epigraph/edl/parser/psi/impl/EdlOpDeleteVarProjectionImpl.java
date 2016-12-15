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

public class EdlOpDeleteVarProjectionImpl extends ASTWrapperPsiElement implements EdlOpDeleteVarProjection {

  public EdlOpDeleteVarProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpDeleteVarProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpDeleteMultiTagProjection getOpDeleteMultiTagProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpDeleteMultiTagProjection.class);
  }

  @Override
  @Nullable
  public EdlOpDeleteSingleTagProjection getOpDeleteSingleTagProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpDeleteSingleTagProjection.class);
  }

  @Override
  @Nullable
  public EdlOpDeleteVarPolymorphicTail getOpDeleteVarPolymorphicTail() {
    return PsiTreeUtil.getChildOfType(this, EdlOpDeleteVarPolymorphicTail.class);
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(E_PLUS);
  }

}
