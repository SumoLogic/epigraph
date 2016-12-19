// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.EdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.schema.parser.psi.*;

public class EdlOpInputModelProjectionImpl extends ASTWrapperPsiElement implements EdlOpInputModelProjection {

  public EdlOpInputModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpInputModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpInputListModelProjection getOpInputListModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpInputListModelProjection.class);
  }

  @Override
  @Nullable
  public EdlOpInputMapModelProjection getOpInputMapModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpInputMapModelProjection.class);
  }

  @Override
  @Nullable
  public EdlOpInputRecordModelProjection getOpInputRecordModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpInputRecordModelProjection.class);
  }

}
