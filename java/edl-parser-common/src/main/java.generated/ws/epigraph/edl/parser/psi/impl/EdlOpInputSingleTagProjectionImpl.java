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

public class EdlOpInputSingleTagProjectionImpl extends ASTWrapperPsiElement implements EdlOpInputSingleTagProjection {

  public EdlOpInputSingleTagProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpInputSingleTagProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpInputModelProjection getOpInputModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpInputModelProjection.class);
  }

  @Override
  @NotNull
  public List<EdlOpInputModelProperty> getOpInputModelPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlOpInputModelProperty.class);
  }

  @Override
  @Nullable
  public EdlTagName getTagName() {
    return PsiTreeUtil.getChildOfType(this, EdlTagName.class);
  }

  @Override
  @Nullable
  public PsiElement getColon() {
    return findChildByType(E_COLON);
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
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(E_PLUS);
  }

}
