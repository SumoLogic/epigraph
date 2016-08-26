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

public class EpigraphAnonMapImpl extends EpigraphTypeRefImpl implements EpigraphAnonMap {

  public EpigraphAnonMapImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitAnonMap(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EpigraphTypeRef getTypeRef() {
    return PsiTreeUtil.getChildOfType(this, EpigraphTypeRef.class);
  }

  @Override
  @Nullable
  public EpigraphValueTypeRef getValueTypeRef() {
    return PsiTreeUtil.getChildOfType(this, EpigraphValueTypeRef.class);
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
  @Nullable
  public PsiElement getComma() {
    return findChildByType(E_COMMA);
  }

  @Override
  @NotNull
  public PsiElement getMap() {
    return notNullChild(findChildByType(E_MAP));
  }

}
