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

public class SchemaUpdateOperationDefImpl extends ASTWrapperPsiElement implements SchemaUpdateOperationDef {

  public SchemaUpdateOperationDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitUpdateOperationDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOperationName getOperationName() {
    return PsiTreeUtil.getChildOfType(this, SchemaOperationName.class);
  }

  @Override
  @NotNull
  public List<SchemaUpdateOperationBodyPart> getUpdateOperationBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaUpdateOperationBodyPart.class);
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
  public PsiElement getOpUpdate() {
    return notNullChild(findChildByType(S_OP_UPDATE));
  }

}
