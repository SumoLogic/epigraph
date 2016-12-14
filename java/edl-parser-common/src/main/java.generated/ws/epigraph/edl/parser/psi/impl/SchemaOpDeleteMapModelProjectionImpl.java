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

public class SchemaOpDeleteMapModelProjectionImpl extends ASTWrapperPsiElement implements SchemaOpDeleteMapModelProjection {

  public SchemaOpDeleteMapModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpDeleteMapModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SchemaOpDeleteKeyProjection getOpDeleteKeyProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpDeleteKeyProjection.class));
  }

  @Override
  @Nullable
  public SchemaOpDeleteVarProjection getOpDeleteVarProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpDeleteVarProjection.class);
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

}
