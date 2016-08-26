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
  public EpigraphEnumTypeDef getEnumTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EpigraphEnumTypeDef.class);
  }

  @Override
  @Nullable
  public EpigraphListTypeDef getListTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EpigraphListTypeDef.class);
  }

  @Override
  @Nullable
  public EpigraphMapTypeDef getMapTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EpigraphMapTypeDef.class);
  }

  @Override
  @Nullable
  public EpigraphPrimitiveTypeDef getPrimitiveTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EpigraphPrimitiveTypeDef.class);
  }

  @Override
  @Nullable
  public EpigraphRecordTypeDef getRecordTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EpigraphRecordTypeDef.class);
  }

  @Override
  @Nullable
  public EpigraphVarTypeDef getVarTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EpigraphVarTypeDef.class);
  }

  @NotNull
  public EpigraphTypeDef getElement() {
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
