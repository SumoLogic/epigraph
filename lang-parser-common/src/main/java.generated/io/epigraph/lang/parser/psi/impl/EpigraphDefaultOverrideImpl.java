// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.lang.parser.psi.*;

public class EpigraphDefaultOverrideImpl extends ASTWrapperPsiElement implements EpigraphDefaultOverride {

  public EpigraphDefaultOverrideImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitDefaultOverride(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EpigraphVarTagRef getVarTagRef() {
    return PsiTreeUtil.getChildOfType(this, EpigraphVarTagRef.class);
  }

  @Override
  @Nullable
  public PsiElement getDefault() {
    return findChildByType(E_DEFAULT);
  }

  @Override
  @Nullable
  public PsiElement getNodefault() {
    return findChildByType(E_NODEFAULT);
  }

}
