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

public class SchemaCustomParamImpl extends ASTWrapperPsiElement implements SchemaCustomParam {

  public SchemaCustomParamImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitCustomParam(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getEq() {
    return findNotNullChildByType(S_EQ);
  }

  @Override
  @Nullable
  public PsiElement getSemiColon() {
    return findChildByType(S_SEMI_COLON);
  }

  @Override
  @Nullable
  public PsiElement getDataValue() {
    return findChildByType(S_DATA_VALUE);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(S_ID);
  }

  @Nullable
  public String getName() {
    return SchemaPsiImplUtil.getName(this);
  }

  public PsiElement setName(String name) {
    return SchemaPsiImplUtil.setName(this, name);
  }

  @NotNull
  public PsiElement getNameIdentifier() {
    return SchemaPsiImplUtil.getNameIdentifier(this);
  }

}
