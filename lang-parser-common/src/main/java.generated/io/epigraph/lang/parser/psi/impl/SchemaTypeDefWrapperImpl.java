// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import io.epigraph.lang.parser.psi.stubs.SchemaTypeDefWrapperStub;
import io.epigraph.lang.parser.psi.*;

public class SchemaTypeDefWrapperImpl extends StubBasedPsiElementBase<SchemaTypeDefWrapperStub> implements SchemaTypeDefWrapper {

  public SchemaTypeDefWrapperImpl(SchemaTypeDefWrapperStub stub, com.intellij.psi.stubs.IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public SchemaTypeDefWrapperImpl(ASTNode node) {
    super(node);
  }

  public SchemaTypeDefWrapperImpl(SchemaTypeDefWrapperStub stub, com.intellij.psi.tree.IElementType nodeType, ASTNode node) {
    super(stub, nodeType, node);
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
    return PsiTreeUtil.getStubChildOfType(this, SchemaEnumTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaListTypeDef getListTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, SchemaListTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaMapTypeDef getMapTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, SchemaMapTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaPrimitiveTypeDef getPrimitiveTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, SchemaPrimitiveTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaRecordTypeDef getRecordTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, SchemaRecordTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaVarTypeDef getVarTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, SchemaVarTypeDef.class);
  }

  @NotNull
  public SchemaTypeDef getElement() {
    return EpigraphPsiImplUtil.getElement(this);
  }

  public void delete() {
    EpigraphPsiImplUtil.delete(this);
  }

  @NotNull
  public String toString() {
    return EpigraphPsiImplUtil.toString(this);
  }

}
