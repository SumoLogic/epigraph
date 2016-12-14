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

public class SchemaOpInputKeyProjectionImpl extends ASTWrapperPsiElement implements SchemaOpInputKeyProjection {

  public SchemaOpInputKeyProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpInputKeyProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaOpInputKeyProjectionPart> getOpInputKeyProjectionPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpInputKeyProjectionPart.class);
  }

  @Override
  @NotNull
  public PsiElement getBracketLeft() {
    return notNullChild(findChildByType(S_BRACKET_LEFT));
  }

  @Override
  @Nullable
  public PsiElement getBracketRight() {
    return findChildByType(S_BRACKET_RIGHT);
  }

  @Override
  @Nullable
  public PsiElement getForbidden() {
    return findChildByType(S_FORBIDDEN);
  }

  @Override
  @Nullable
  public PsiElement getRequired() {
    return findChildByType(S_REQUIRED);
  }

}
