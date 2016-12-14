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

public class SchemaResourceDefImpl extends ASTWrapperPsiElement implements SchemaResourceDef {

  public SchemaResourceDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitResourceDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaOperationDef> getOperationDefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOperationDef.class);
  }

  @Override
  @NotNull
  public SchemaResourceName getResourceName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaResourceName.class));
  }

  @Override
  @NotNull
  public SchemaResourceType getResourceType() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaResourceType.class));
  }

  @Override
  @NotNull
  public PsiElement getCurlyLeft() {
    return notNullChild(findChildByType(S_CURLY_LEFT));
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(S_CURLY_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getResource() {
    return notNullChild(findChildByType(S_RESOURCE));
  }

}
