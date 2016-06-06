// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.sumologic.epigraph.schema.parser.psi.util.SchemaPsiTreeUtil;
import static com.sumologic.epigraph.schema.parser.lexer.SchemaElementTypes.*;
import com.sumologic.epigraph.schema.parser.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class SchemaListTypeDefImpl extends SchemaListTypeDefImplBase implements SchemaListTypeDef {

  public SchemaListTypeDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaListTypeDefImpl(com.sumologic.epigraph.schema.parser.psi.stubs.SchemaListTypeDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitListTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SchemaAnonList getAnonList() {
    return notNullChild(SchemaPsiTreeUtil.getChildOfType(this, SchemaAnonList.class));
  }

  @Override
  @Nullable
  public SchemaExtendsDecl getExtendsDecl() {
    return SchemaPsiTreeUtil.getChildOfType(this, SchemaExtendsDecl.class);
  }

  @Override
  @Nullable
  public SchemaListTypeBody getListTypeBody() {
    return SchemaPsiTreeUtil.getChildOfType(this, SchemaListTypeBody.class);
  }

  @Override
  @Nullable
  public SchemaMetaDecl getMetaDecl() {
    return SchemaPsiTreeUtil.getChildOfType(this, SchemaMetaDecl.class);
  }

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(S_ABSTRACT);
  }

  @Override
  @Nullable
  public PsiElement getPolymorphic() {
    return findChildByType(S_POLYMORPHIC);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(S_ID);
  }

}
