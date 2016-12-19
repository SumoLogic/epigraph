// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.schema.parser.psi.*;

public class SchemaOpOutputListModelProjectionImpl extends ASTWrapperPsiElement implements SchemaOpOutputListModelProjection {

  public SchemaOpOutputListModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpOutputListModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpOutputVarProjection getOpOutputVarProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpOutputVarProjection.class);
  }

  @Override
  @Nullable
  public PsiElement getParenLeft() {
    return findChildByType(S_PAREN_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getParenRight() {
    return findChildByType(S_PAREN_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getStar() {
    return notNullChild(findChildByType(S_STAR));
  }

}
