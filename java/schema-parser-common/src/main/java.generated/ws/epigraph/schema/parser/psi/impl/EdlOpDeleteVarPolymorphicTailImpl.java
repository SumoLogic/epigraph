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

public class EdlOpDeleteVarPolymorphicTailImpl extends ASTWrapperPsiElement implements EdlOpDeleteVarPolymorphicTail {

  public EdlOpDeleteVarPolymorphicTailImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpDeleteVarPolymorphicTail(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpDeleteVarMultiTail getOpDeleteVarMultiTail() {
    return PsiTreeUtil.getChildOfType(this, EdlOpDeleteVarMultiTail.class);
  }

  @Override
  @Nullable
  public EdlOpDeleteVarSingleTail getOpDeleteVarSingleTail() {
    return PsiTreeUtil.getChildOfType(this, EdlOpDeleteVarSingleTail.class);
  }

}