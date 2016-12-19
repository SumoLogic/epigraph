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
import ws.epigraph.lang.Qn;

public class EdlQnImpl extends ASTWrapperPsiElement implements EdlQn {

  public EdlQnImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitQn(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlQnSegment> getQnSegmentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlQnSegment.class);
  }

  @NotNull
  public Qn getQn() {
    return EdlPsiImplUtil.getQn(this);
  }

}
