// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.EdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.edl.parser.psi.*;

public class EdlUpdateOperationDefImpl extends ASTWrapperPsiElement implements EdlUpdateOperationDef {

  public EdlUpdateOperationDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitUpdateOperationDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOperationName getOperationName() {
    return PsiTreeUtil.getChildOfType(this, EdlOperationName.class);
  }

  @Override
  @NotNull
  public List<EdlUpdateOperationBodyPart> getUpdateOperationBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlUpdateOperationBodyPart.class);
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(E_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(E_CURLY_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getUpdate() {
    return notNullChild(findChildByType(E_UPDATE));
  }

}
