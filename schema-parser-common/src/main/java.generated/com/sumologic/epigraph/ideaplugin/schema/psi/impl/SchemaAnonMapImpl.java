// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;

public class SchemaAnonMapImpl extends ASTWrapperPsiElement implements SchemaAnonMap {

  public SchemaAnonMapImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitAnonMap(this);
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
  public List<SchemaTypeRef> getTypeRefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaTypeRef.class);
  }

  @Override
  @Nullable
  public PsiElement getBracketLeft() {
    return findChildByType(S_BRACKET_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getBracketRight() {
    return findChildByType(S_BRACKET_RIGHT);
  }

  @Override
  @Nullable
  public PsiElement getComma() {
    return findChildByType(S_COMMA);
  }

  @Override
  @NotNull
  public PsiElement getMap() {
    return findNotNullChildByType(S_MAP);
  }

}
