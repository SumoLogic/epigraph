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

public class EdlOpDeleteKeyProjectionImpl extends ASTWrapperPsiElement implements EdlOpDeleteKeyProjection {

  public EdlOpDeleteKeyProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpDeleteKeyProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlOpDeleteKeyProjectionPart> getOpDeleteKeyProjectionPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlOpDeleteKeyProjectionPart.class);
  }

  @Override
  @NotNull
  public PsiElement getBracketLeft() {
    return notNullChild(findChildByType(E_BRACKET_LEFT));
  }

  @Override
  @Nullable
  public PsiElement getBracketRight() {
    return findChildByType(E_BRACKET_RIGHT);
  }

  @Override
  @Nullable
  public PsiElement getForbidden() {
    return findChildByType(E_FORBIDDEN);
  }

  @Override
  @Nullable
  public PsiElement getRequired() {
    return findChildByType(E_REQUIRED);
  }

}
