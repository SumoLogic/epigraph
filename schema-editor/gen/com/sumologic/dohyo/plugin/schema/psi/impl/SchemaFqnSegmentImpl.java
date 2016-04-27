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
import com.intellij.psi.PsiReference;

public class SchemaFqnSegmentImpl extends ASTWrapperPsiElement implements SchemaFqnSegment {

  public SchemaFqnSegmentImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitFqnSegment(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(S_ID);
  }

  @Nullable
  public SchemaFqnTypeRef getFqnTypeRef() {
    return SchemaPsiImplUtil.getFqnTypeRef(this);
  }

  public boolean isLast() {
    return SchemaPsiImplUtil.isLast(this);
  }

  @Nullable
  public PsiReference getReference() {
    return SchemaPsiImplUtil.getReference(this);
  }

}
