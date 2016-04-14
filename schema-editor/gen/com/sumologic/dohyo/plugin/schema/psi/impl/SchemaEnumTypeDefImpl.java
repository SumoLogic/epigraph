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

public class SchemaEnumTypeDefImpl extends ASTWrapperPsiElement implements SchemaEnumTypeDef {

  public SchemaEnumTypeDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitEnumTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaCustomParam> getCustomParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaCustomParam.class);
  }

  @Override
  @NotNull
  public List<SchemaEnumMember> getEnumMemberList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaEnumMember.class);
  }

  @Override
  @NotNull
  public PsiElement getCurlyLeft() {
    return findNotNullChildByType(S_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(S_CURLY_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getEnum() {
    return findNotNullChildByType(S_ENUM);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(S_ID);
  }

}
