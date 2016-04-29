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

public class SchemaSupplementDefImpl extends ASTWrapperPsiElement implements SchemaSupplementDef {

  public SchemaSupplementDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitSupplementDef(this);
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
  @Nullable
  public SchemaFqnTypeRef getFqnTypeRef() {
    return findChildByClass(SchemaFqnTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getSupplement() {
    return findNotNullChildByType(S_SUPPLEMENT);
  }

  @Override
  @Nullable
  public PsiElement getWith() {
    return findChildByType(S_WITH);
  }

}
