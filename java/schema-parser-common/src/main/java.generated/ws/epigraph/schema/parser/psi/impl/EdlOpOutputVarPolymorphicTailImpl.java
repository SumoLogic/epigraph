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

public class EdlOpOutputVarPolymorphicTailImpl extends ASTWrapperPsiElement implements EdlOpOutputVarPolymorphicTail {

  public EdlOpOutputVarPolymorphicTailImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpOutputVarPolymorphicTail(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpOutputVarMultiTail getOpOutputVarMultiTail() {
    return PsiTreeUtil.getChildOfType(this, EdlOpOutputVarMultiTail.class);
  }

  @Override
  @Nullable
  public EdlOpOutputVarSingleTail getOpOutputVarSingleTail() {
    return PsiTreeUtil.getChildOfType(this, EdlOpOutputVarSingleTail.class);
  }

}
