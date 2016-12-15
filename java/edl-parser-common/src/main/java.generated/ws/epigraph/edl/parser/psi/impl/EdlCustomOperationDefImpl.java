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

public class EdlCustomOperationDefImpl extends ASTWrapperPsiElement implements EdlCustomOperationDef {

  public EdlCustomOperationDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitCustomOperationDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlCustomOperationBodyPart> getCustomOperationBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlCustomOperationBodyPart.class);
  }

  @Override
  @NotNull
  public EdlOperationName getOperationName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlOperationName.class));
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
  public PsiElement getCustom() {
    return notNullChild(findChildByType(S_CUSTOM));
  }

}
