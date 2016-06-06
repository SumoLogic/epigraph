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

public class SchemaVarTypeDefImpl extends SchemaVarTypeDefImplBase implements SchemaVarTypeDef {

  public SchemaVarTypeDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaVarTypeDefImpl(com.sumologic.epigraph.schema.parser.psi.stubs.SchemaVarTypeDefStub stub, IStubElementType nodeType) {
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
    return SchemaPsiTreeUtil.getChildOfType(this, SchemaDefaultOverride.class);
  }

  @Override
  @Nullable
  public SchemaExtendsDecl getExtendsDecl() {
    return SchemaPsiTreeUtil.getChildOfType(this, SchemaExtendsDecl.class);
  }

  @Override
  @Nullable
  public SchemaSupplementsDecl getSupplementsDecl() {
    return SchemaPsiTreeUtil.getChildOfType(this, SchemaSupplementsDecl.class);
  }

  @Override
  @Nullable
  public SchemaVarTypeBody getVarTypeBody() {
    return SchemaPsiTreeUtil.getChildOfType(this, SchemaVarTypeBody.class);
  }

  @Override
  @NotNull
  public PsiElement getVartype() {
    return notNullChild(findChildByType(S_VARTYPE));
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(S_ID);
  }

  @NotNull
  public List<SchemaTypeDef> supplemented() {
    return SchemaPsiImplUtil.supplemented(this);
  }

}
