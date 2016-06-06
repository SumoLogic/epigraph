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

public class SchemaEnumTypeDefImpl extends SchemaEnumTypeDefImplBase implements SchemaEnumTypeDef {

  public SchemaEnumTypeDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaEnumTypeDefImpl(com.sumologic.epigraph.schema.parser.psi.stubs.SchemaEnumTypeDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitEnumTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaEnumTypeBody getEnumTypeBody() {
    return SchemaPsiTreeUtil.getChildOfType(this, SchemaEnumTypeBody.class);
  }

  @Override
  @Nullable
  public SchemaMetaDecl getMetaDecl() {
    return SchemaPsiTreeUtil.getChildOfType(this, SchemaMetaDecl.class);
  }

  @Override
  @NotNull
  public PsiElement getEnum() {
    return notNullChild(findChildByType(S_ENUM));
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(S_ID);
  }

}
