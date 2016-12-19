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

public class EdlOpModelPathImpl extends ASTWrapperPsiElement implements EdlOpModelPath {

  public EdlOpModelPathImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpModelPath(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpMapModelPath getOpMapModelPath() {
    return PsiTreeUtil.getChildOfType(this, EdlOpMapModelPath.class);
  }

  @Override
  @Nullable
  public EdlOpRecordModelPath getOpRecordModelPath() {
    return PsiTreeUtil.getChildOfType(this, EdlOpRecordModelPath.class);
  }

}