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

public class EdlOpOutputModelProjectionImpl extends ASTWrapperPsiElement implements EdlOpOutputModelProjection {

  public EdlOpOutputModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpOutputModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpOutputListModelProjection getOpOutputListModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpOutputListModelProjection.class);
  }

  @Override
  @Nullable
  public EdlOpOutputMapModelProjection getOpOutputMapModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpOutputMapModelProjection.class);
  }

  @Override
  @Nullable
  public EdlOpOutputRecordModelProjection getOpOutputRecordModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpOutputRecordModelProjection.class);
  }

}
