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

public class SchemaTypeDefsImpl extends ASTWrapperPsiElement implements SchemaTypeDefs {

  public SchemaTypeDefsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitTypeDefs(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaEnumTypeDef> getEnumTypeDefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaEnumTypeDef.class);
  }

  @Override
  @NotNull
  public List<SchemaListTypeDef> getListTypeDefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaListTypeDef.class);
  }

  @Override
  @NotNull
  public List<SchemaMapTypeDef> getMapTypeDefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaMapTypeDef.class);
  }

  @Override
  @NotNull
  public List<SchemaMultiTypeDef> getMultiTypeDefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaMultiTypeDef.class);
  }

  @Override
  @NotNull
  public List<SchemaPrimitiveTypeDef> getPrimitiveTypeDefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaPrimitiveTypeDef.class);
  }

  @Override
  @NotNull
  public List<SchemaRecordTypeDef> getRecordTypeDefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaRecordTypeDef.class);
  }

  @Override
  @NotNull
  public List<SchemaUnionTypeDef> getUnionTypeDefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaUnionTypeDef.class);
  }

}
