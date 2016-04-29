// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaPlugin.schema.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.sumologic.epigraph.ideaPlugin.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.sumologic.epigraph.ideaPlugin.schema.psi.*;

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
  @NotNull
  public PsiElement getImport() {
    return findNotNullChildByType(S_IMPORT);
  }

}
