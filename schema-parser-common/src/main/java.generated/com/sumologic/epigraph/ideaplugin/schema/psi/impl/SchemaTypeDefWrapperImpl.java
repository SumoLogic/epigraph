// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaTypeDefWrapperStub;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class SchemaTypeDefWrapperImpl extends StubBasedPsiElementBase<SchemaTypeDefWrapperStub> implements SchemaTypeDefWrapper {

  public SchemaTypeDefWrapperImpl(ASTNode node) {
    super(node);
  }

  public SchemaTypeDefWrapperImpl(SchemaTypeDefWrapperStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitTypeDefWrapper(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaEnumTypeDef getEnumTypeDef() {
    return findChildByClass(SchemaEnumTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaListTypeDef getListTypeDef() {
    return findChildByClass(SchemaListTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaMapTypeDef getMapTypeDef() {
    return findChildByClass(SchemaMapTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaPrimitiveTypeDef getPrimitiveTypeDef() {
    return findChildByClass(SchemaPrimitiveTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaRecordTypeDef getRecordTypeDef() {
    return findChildByClass(SchemaRecordTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaVarTypeDef getVarTypeDef() {
    return findChildByClass(SchemaVarTypeDef.class);
  }

  @NotNull
  public SchemaTypeDef getElement() {
    return SchemaPsiImplUtil.getElement(this);
  }

  public void delete() {
    SchemaPsiImplUtil.delete(this);
  }

  @NotNull
  public String toString() {
    return SchemaPsiImplUtil.toString(this);
  }

}
