// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import io.epigraph.lang.parser.psi.*;

public class EpigraphDataVarImpl extends EpigraphDataValueImpl implements EpigraphDataVar {

  public EpigraphDataVarImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitDataVar(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EpigraphDataVarEntry> getDataVarEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EpigraphDataVarEntry.class);
  }

  @Override
  @NotNull
  public PsiElement getAngleLeft() {
    return notNullChild(findChildByType(E_ANGLE_LEFT));
  }

  @Override
  @Nullable
  public PsiElement getAngleRight() {
    return findChildByType(E_ANGLE_RIGHT);
  }

}
