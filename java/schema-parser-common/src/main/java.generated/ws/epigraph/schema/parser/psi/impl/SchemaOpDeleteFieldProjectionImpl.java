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

public class SchemaOpDeleteFieldProjectionImpl extends ASTWrapperPsiElement implements SchemaOpDeleteFieldProjection {

  public SchemaOpDeleteFieldProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpDeleteFieldProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaOpDeleteFieldProjectionBodyPart> getOpDeleteFieldProjectionBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpDeleteFieldProjectionBodyPart.class);
  }

  @Override
  @NotNull
  public SchemaOpDeleteVarProjection getOpDeleteVarProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpDeleteVarProjection.class));
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(S_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(S_CURLY_RIGHT);
  }

}
