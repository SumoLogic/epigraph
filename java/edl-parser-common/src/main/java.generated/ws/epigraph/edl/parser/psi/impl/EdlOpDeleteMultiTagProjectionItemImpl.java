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

public class EdlOpDeleteMultiTagProjectionItemImpl extends ASTWrapperPsiElement implements EdlOpDeleteMultiTagProjectionItem {

  public EdlOpDeleteMultiTagProjectionItemImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpDeleteMultiTagProjectionItem(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpDeleteModelProjection getOpDeleteModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpDeleteModelProjection.class);
  }

  @Override
  @NotNull
  public List<EdlOpDeleteModelProperty> getOpDeleteModelPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlOpDeleteModelProperty.class);
  }

  @Override
  @NotNull
  public EdlTagName getTagName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlTagName.class));
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

}
