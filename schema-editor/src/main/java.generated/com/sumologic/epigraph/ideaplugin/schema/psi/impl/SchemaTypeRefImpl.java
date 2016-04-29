// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;

public class SchemaTypeRefImpl extends ASTWrapperPsiElement implements SchemaTypeRef {

  public SchemaTypeRefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitTypeRef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaAnonList getAnonList() {
    return findChildByClass(SchemaAnonList.class);
  }

  @Override
  @Nullable
  public SchemaAnonMap getAnonMap() {
    return findChildByClass(SchemaAnonMap.class);
  }

  @Override
  @Nullable
  public SchemaFqnTypeRef getFqnTypeRef() {
    return findChildByClass(SchemaFqnTypeRef.class);
  }

}
