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

public class EdlOpOutputVarProjectionImpl extends ASTWrapperPsiElement implements EdlOpOutputVarProjection {

  public EdlOpOutputVarProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpOutputVarProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpOutputMultiTagProjection getOpOutputMultiTagProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpOutputMultiTagProjection.class);
  }

  @Override
  @Nullable
  public EdlOpOutputSingleTagProjection getOpOutputSingleTagProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpOutputSingleTagProjection.class);
  }

  @Override
  @Nullable
  public EdlOpOutputVarPolymorphicTail getOpOutputVarPolymorphicTail() {
    return PsiTreeUtil.getChildOfType(this, EdlOpOutputVarPolymorphicTail.class);
  }

}
