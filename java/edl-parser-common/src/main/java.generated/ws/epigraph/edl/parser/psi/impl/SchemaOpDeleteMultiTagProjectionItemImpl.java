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

public class SchemaOpDeleteMultiTagProjectionItemImpl extends ASTWrapperPsiElement implements SchemaOpDeleteMultiTagProjectionItem {

  public SchemaOpDeleteMultiTagProjectionItemImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpDeleteMultiTagProjectionItem(this);
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
  @NotNull
  public SchemaTagName getTagName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaTagName.class));
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
