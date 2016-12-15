// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.EdlElementTypes.*;
import ws.epigraph.edl.parser.psi.*;

public class EdlAnonListImpl extends EdlTypeRefImpl implements EdlAnonList {

  public EdlAnonListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitAnonList(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlValueTypeRef getValueTypeRef() {
    return PsiTreeUtil.getChildOfType(this, EdlValueTypeRef.class);
  }

  @Override
  @Nullable
  public PsiElement getBracketLeft() {
    return findChildByType(E_BRACKET_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getBracketRight() {
    return findChildByType(E_BRACKET_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getList() {
    return notNullChild(findChildByType(E_LIST));
  }

}
