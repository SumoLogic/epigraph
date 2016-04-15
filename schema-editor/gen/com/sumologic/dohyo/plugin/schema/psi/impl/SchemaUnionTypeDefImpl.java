// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.*;
import com.sumologic.dohyo.plugin.schema.psi.*;

public class SchemaUnionTypeDefImpl extends SchemaTypeDefImpl implements SchemaUnionTypeDef {

  public SchemaUnionTypeDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitUnionTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaExtendsDecl getExtendsDecl() {
    return findChildByClass(SchemaExtendsDecl.class);
  }

  @Override
  @Nullable
  public SchemaUnionTypeBody getUnionTypeBody() {
    return findChildByClass(SchemaUnionTypeBody.class);
  }

  @Override
  @NotNull
  public PsiElement getUnion() {
    return findNotNullChildByType(S_UNION);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(S_ID);
  }

}
