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

public class EdlOpInputDefaultValueImpl extends ASTWrapperPsiElement implements EdlOpInputDefaultValue {

  public EdlOpInputDefaultValueImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpInputDefaultValue(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlDatum getDatum() {
    return PsiTreeUtil.getChildOfType(this, EdlDatum.class);
  }

  @Override
  @Nullable
  public PsiElement getColon() {
    return findChildByType(E_COLON);
  }

  @Override
  @NotNull
  public PsiElement getDefault() {
    return notNullChild(findChildByType(E_DEFAULT));
  }

}
