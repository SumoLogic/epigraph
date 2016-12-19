// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.EdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.schema.parser.psi.*;

public class EdlCreateOperationDefImpl extends ASTWrapperPsiElement implements EdlCreateOperationDef {

  public EdlCreateOperationDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitCreateOperationDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlCreateOperationBodyPart> getCreateOperationBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlCreateOperationBodyPart.class);
  }

  @Override
  @Nullable
  public EdlOperationName getOperationName() {
    return PsiTreeUtil.getChildOfType(this, EdlOperationName.class);
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
  public PsiElement getOpCreate() {
    return notNullChild(findChildByType(S_OP_CREATE));
  }

}