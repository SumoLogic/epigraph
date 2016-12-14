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

public class SchemaOpInputFieldProjectionImpl extends ASTWrapperPsiElement implements SchemaOpInputFieldProjection {

  public SchemaOpInputFieldProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpInputFieldProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaOpInputFieldProjectionBodyPart> getOpInputFieldProjectionBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpInputFieldProjectionBodyPart.class);
  }

  @Override
  @NotNull
  public SchemaOpInputVarProjection getOpInputVarProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpInputVarProjection.class));
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
