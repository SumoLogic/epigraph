// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class SchemaPrimitiveTypeDefImpl extends SchemaPrimitiveTypeDefImplBase implements SchemaPrimitiveTypeDef {

  public SchemaPrimitiveTypeDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaPrimitiveTypeDefImpl(com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaPrimitiveTypeDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitPrimitiveTypeDef(this);
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
  public SchemaMetaDecl getMetaDecl() {
    return findChildByClass(SchemaMetaDecl.class);
  }

  @Override
  @Nullable
  public SchemaPrimitiveTypeBody getPrimitiveTypeBody() {
    return findChildByClass(SchemaPrimitiveTypeBody.class);
  }

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(S_ABSTRACT);
  }

  @Override
  @Nullable
  public PsiElement getBooleanT() {
    return findChildByType(S_BOOLEAN_T);
  }

  @Override
  @Nullable
  public PsiElement getDoubleT() {
    return findChildByType(S_DOUBLE_T);
  }

  @Override
  @Nullable
  public PsiElement getIntegerT() {
    return findChildByType(S_INTEGER_T);
  }

  @Override
  @Nullable
  public PsiElement getLongT() {
    return findChildByType(S_LONG_T);
  }

  @Override
  @Nullable
  public PsiElement getPolymorphic() {
    return findChildByType(S_POLYMORPHIC);
  }

  @Override
  @Nullable
  public PsiElement getStringT() {
    return findChildByType(S_STRING_T);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(S_ID);
  }

}
