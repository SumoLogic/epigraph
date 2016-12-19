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

public class EdlOpVarPathImpl extends ASTWrapperPsiElement implements EdlOpVarPath {

  public EdlOpVarPathImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpVarPath(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EdlOpModelPath getOpModelPath() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlOpModelPath.class));
  }

  @Override
  @NotNull
  public List<EdlOpModelPathProperty> getOpModelPathPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlOpModelPathProperty.class);
  }

  @Override
  @Nullable
  public EdlTagName getTagName() {
    return PsiTreeUtil.getChildOfType(this, EdlTagName.class);
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
