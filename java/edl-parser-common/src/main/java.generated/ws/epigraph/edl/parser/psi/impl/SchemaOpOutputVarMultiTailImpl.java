// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.edl.parser.psi.*;

public class SchemaOpOutputVarMultiTailImpl extends ASTWrapperPsiElement implements SchemaOpOutputVarMultiTail {

  public SchemaOpOutputVarMultiTailImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpOutputVarMultiTail(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaOpOutputVarMultiTailItem> getOpOutputVarMultiTailItemList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpOutputVarMultiTailItem.class);
  }

  @Override
  @NotNull
  public PsiElement getParenLeft() {
    return notNullChild(findChildByType(S_PAREN_LEFT));
  }

  @Override
  @Nullable
  public PsiElement getParenRight() {
    return findChildByType(S_PAREN_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getTilda() {
    return notNullChild(findChildByType(S_TILDA));
  }

}
