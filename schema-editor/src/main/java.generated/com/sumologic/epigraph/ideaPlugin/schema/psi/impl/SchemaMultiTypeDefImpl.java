// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaPlugin.schema.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.sumologic.epigraph.ideaPlugin.schema.lexer.SchemaElementTypes.*;
import com.sumologic.epigraph.ideaPlugin.schema.psi.*;

public class SchemaMultiTypeDefImpl extends SchemaTypeDefImpl implements SchemaMultiTypeDef {

  public SchemaMultiTypeDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitMultiTypeDef(this);
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
  public SchemaMultiSupplementsDecl getMultiSupplementsDecl() {
    return findChildByClass(SchemaMultiSupplementsDecl.class);
  }

  @Override
  @Nullable
  public SchemaMultiTypeBody getMultiTypeBody() {
    return findChildByClass(SchemaMultiTypeBody.class);
  }

  @Override
  @NotNull
  public PsiElement getMulti() {
    return findNotNullChildByType(S_MULTI);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(S_ID);
  }

}
