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

public class SchemaDeleteOperationDefImpl extends ASTWrapperPsiElement implements SchemaDeleteOperationDef {

  public SchemaDeleteOperationDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitDeleteOperationDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaDeleteOperationBodyPart> getDeleteOperationBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaDeleteOperationBodyPart.class);
  }

  @Override
  @Nullable
  public SchemaOperationName getOperationName() {
    return PsiTreeUtil.getChildOfType(this, SchemaOperationName.class);
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

  @Override
  @NotNull
  public PsiElement getDelete() {
    return notNullChild(findChildByType(S_DELETE));
  }

}
