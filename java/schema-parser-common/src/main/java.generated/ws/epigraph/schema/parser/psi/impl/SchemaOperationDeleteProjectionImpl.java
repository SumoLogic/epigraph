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

public class SchemaOperationDeleteProjectionImpl extends ASTWrapperPsiElement implements SchemaOperationDeleteProjection {

  public SchemaOperationDeleteProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOperationDeleteProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpDeleteFieldProjection getOpDeleteFieldProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpDeleteFieldProjection.class);
  }

  @Override
  @NotNull
  public PsiElement getDeleteProjection() {
    return notNullChild(findChildByType(S_DELETE_PROJECTION));
  }

}
