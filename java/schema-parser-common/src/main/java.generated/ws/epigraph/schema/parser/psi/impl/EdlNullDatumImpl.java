// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.EdlElementTypes.*;
import ws.epigraph.schema.parser.psi.*;

public class EdlNullDatumImpl extends EdlDatumImpl implements EdlNullDatum {

  public EdlNullDatumImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitNullDatum(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlTypeRef getTypeRef() {
    return PsiTreeUtil.getChildOfType(this, EdlTypeRef.class);
  }

  @Override
  @Nullable
  public PsiElement getAt() {
    return findChildByType(S_AT);
  }

  @Override
  @NotNull
  public PsiElement getNull() {
    return notNullChild(findChildByType(S_NULL));
  }

}
