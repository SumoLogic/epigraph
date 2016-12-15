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

public class EdlOpDeleteModelProjectionImpl extends ASTWrapperPsiElement implements EdlOpDeleteModelProjection {

  public EdlOpDeleteModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpDeleteModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpDeleteListModelProjection getOpDeleteListModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpDeleteListModelProjection.class);
  }

  @Override
  @Nullable
  public EdlOpDeleteMapModelProjection getOpDeleteMapModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpDeleteMapModelProjection.class);
  }

  @Override
  @Nullable
  public EdlOpDeleteRecordModelProjection getOpDeleteRecordModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpDeleteRecordModelProjection.class);
  }

}
