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

public class EdlOpOutputMultiTagProjectionItemImpl extends ASTWrapperPsiElement implements EdlOpOutputMultiTagProjectionItem {

  public EdlOpOutputMultiTagProjectionItemImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpOutputMultiTagProjectionItem(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlOpOutputModelProjection getOpOutputModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpOutputModelProjection.class);
  }

  @Override
  @NotNull
  public List<EdlOpOutputModelProperty> getOpOutputModelPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlOpOutputModelProperty.class);
  }

  @Override
  @NotNull
  public EdlTagName getTagName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlTagName.class));
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
