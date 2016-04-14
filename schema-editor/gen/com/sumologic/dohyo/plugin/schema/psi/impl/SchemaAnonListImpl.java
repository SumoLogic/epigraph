// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.sumologic.dohyo.plugin.schema.psi.*;

public class SchemaAnonListImpl extends ASTWrapperPsiElement implements SchemaAnonList {

  public SchemaAnonListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitAnonList(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaDefaultOverride getDefaultOverride() {
    return findChildByClass(SchemaDefaultOverride.class);
  }

  @Override
  @NotNull
  public SchemaTypeRef getTypeRef() {
    return findNotNullChildByClass(SchemaTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getBracketLeft() {
    return findNotNullChildByType(S_BRACKET_LEFT);
  }

  @Override
  @NotNull
  public PsiElement getBracketRight() {
    return findNotNullChildByType(S_BRACKET_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getList() {
    return findNotNullChildByType(S_LIST);
  }

}
