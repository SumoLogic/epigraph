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

public class SchemaNamespacedTypedefsImpl extends ASTWrapperPsiElement implements SchemaNamespacedTypedefs {

  public SchemaNamespacedTypedefsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitNamespacedTypedefs(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SchemaNamespaceDecl getNamespaceDecl() {
    return findNotNullChildByClass(SchemaNamespaceDecl.class);
  }

  @Override
  @NotNull
  public List<SchemaTypeDef> getTypeDefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaTypeDef.class);
  }

}
