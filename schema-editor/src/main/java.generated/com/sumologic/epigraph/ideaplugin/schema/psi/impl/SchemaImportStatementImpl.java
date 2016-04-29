// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;

public class SchemaImportStatementImpl extends ASTWrapperPsiElement implements SchemaImportStatement {

  public SchemaImportStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitImportStatement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaFqn getFqn() {
    return findChildByClass(SchemaFqn.class);
  }

  @Override
  @Nullable
  public SchemaStarImportSuffix getStarImportSuffix() {
    return findChildByClass(SchemaStarImportSuffix.class);
  }

  @Override
  @NotNull
  public PsiElement getImport() {
    return findNotNullChildByType(S_IMPORT);
  }

}
