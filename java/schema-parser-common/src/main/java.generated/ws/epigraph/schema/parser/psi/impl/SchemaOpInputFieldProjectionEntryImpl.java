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

public class SchemaOpInputFieldProjectionEntryImpl extends ASTWrapperPsiElement implements SchemaOpInputFieldProjectionEntry {

  public SchemaOpInputFieldProjectionEntryImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpInputFieldProjectionEntry(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SchemaOpInputFieldProjection getOpInputFieldProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpInputFieldProjection.class));
  }

  @Override
  @NotNull
  public SchemaQid getQid() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaQid.class));
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(S_PLUS);
  }

}
