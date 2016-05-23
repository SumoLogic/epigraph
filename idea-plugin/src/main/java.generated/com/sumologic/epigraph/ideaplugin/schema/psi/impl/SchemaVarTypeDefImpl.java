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

public class SchemaVarTypeDefImpl extends SchemaVarTypeDefImplBase implements SchemaVarTypeDef {

  public SchemaVarTypeDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaVarTypeDefImpl(com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaVarTypeDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitVarTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaDefaultOverride getDefaultOverride() {
    return findChildByClass(SchemaDefaultOverride.class);
  }

  @Override
  @Nullable
  public SchemaExtendsDecl getExtendsDecl() {
    return findChildByClass(SchemaExtendsDecl.class);
  }

  @Override
  @Nullable
  public SchemaVarTypeBody getVarTypeBody() {
    return findChildByClass(SchemaVarTypeBody.class);
  }

  @Override
  @Nullable
  public SchemaVarTypeSupplementsDecl getVarTypeSupplementsDecl() {
    return findChildByClass(SchemaVarTypeSupplementsDecl.class);
  }

  @Override
  @NotNull
  public PsiElement getVartype() {
    return findNotNullChildByType(S_VARTYPE);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(S_ID);
  }

}
