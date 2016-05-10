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

public class SchemaPrimitiveKindImpl extends ASTWrapperPsiElement implements SchemaPrimitiveKind {

  public SchemaPrimitiveKindImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitPrimitiveKind(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getBooleanT() {
    return findChildByType(S_BOOLEAN_T);
  }

  @Override
  @Nullable
  public PsiElement getDoubleT() {
    return findChildByType(S_DOUBLE_T);
  }

  @Override
  @Nullable
  public PsiElement getIntegerT() {
    return findChildByType(S_INTEGER_T);
  }

  @Override
  @Nullable
  public PsiElement getLongT() {
    return findChildByType(S_LONG_T);
  }

  @Override
  @Nullable
  public PsiElement getStringT() {
    return findChildByType(S_STRING_T);
  }

}
