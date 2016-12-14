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

public class SchemaOpDeleteSingleTagProjectionImpl extends ASTWrapperPsiElement implements SchemaOpDeleteSingleTagProjection {

  public SchemaOpDeleteSingleTagProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpDeleteSingleTagProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpDeleteModelProjection getOpDeleteModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpDeleteModelProjection.class);
  }

  @Override
  @NotNull
  public List<SchemaOpDeleteModelProperty> getOpDeleteModelPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpDeleteModelProperty.class);
  }

  @Override
  @Nullable
  public SchemaTagName getTagName() {
    return PsiTreeUtil.getChildOfType(this, SchemaTagName.class);
  }

  @Override
  @Nullable
  public PsiElement getColon() {
    return findChildByType(S_COLON);
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
