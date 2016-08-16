// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.sumologic.epigraph.schema.parser.lexer.SchemaElementTypes.*;
import com.sumologic.epigraph.schema.parser.psi.*;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaRecordTypeDefStub;
import com.intellij.psi.stubs.IStubElementType;

public class SchemaRecordTypeDefImpl extends SchemaRecordTypeDefImplBase implements SchemaRecordTypeDef {

  public SchemaRecordTypeDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaRecordTypeDefImpl(SchemaRecordTypeDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitRecordTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaExtendsDecl getExtendsDecl() {
    return PsiTreeUtil.getChildOfType(this, SchemaExtendsDecl.class);
  }

  @Override
  @Nullable
  public SchemaMetaDecl getMetaDecl() {
    return PsiTreeUtil.getChildOfType(this, SchemaMetaDecl.class);
  }

  @Override
  @Nullable
  public SchemaQid getQid() {
    return PsiTreeUtil.getChildOfType(this, SchemaQid.class);
  }

  @Override
  @Nullable
  public SchemaRecordTypeBody getRecordTypeBody() {
    return PsiTreeUtil.getChildOfType(this, SchemaRecordTypeBody.class);
  }

  @Override
  @Nullable
  public SchemaSupplementsDecl getSupplementsDecl() {
    return PsiTreeUtil.getChildOfType(this, SchemaSupplementsDecl.class);
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
  @NotNull
  public PsiElement getRecord() {
    return notNullChild(findChildByType(S_RECORD));
  }

  @NotNull
  public List<SchemaTypeDef> supplemented() {
    return SchemaPsiImplUtil.supplemented(this);
  }

}
