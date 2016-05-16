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

public class SchemaRecordSupplementsDeclImpl extends ASTWrapperPsiElement implements SchemaRecordSupplementsDecl {

  public SchemaRecordSupplementsDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitRecordSupplementsDecl(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaCombinedFqns> getCombinedFqnsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaCombinedFqns.class);
  }

  @Override
  @NotNull
  public PsiElement getSupplements() {
    return findNotNullChildByType(S_SUPPLEMENTS);
  }

}