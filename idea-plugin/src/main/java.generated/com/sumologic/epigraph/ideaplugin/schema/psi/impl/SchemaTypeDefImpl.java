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
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaTypeDefStub;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import javax.swing.Icon;
import com.intellij.psi.stubs.IStubElementType;

public class SchemaTypeDefImpl extends StubBasedPsiElementBase<SchemaTypeDefStub> implements SchemaTypeDef {

  public SchemaTypeDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaTypeDefImpl(SchemaTypeDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitTypeDef(this);
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
  public SchemaTypeDefElement element() {
    return SchemaPsiImplUtil.element(this);
  }

  @Nullable
  public SchemaMetaDecl getMetaDecl() {
    return SchemaPsiImplUtil.getMetaDecl(this);
  }

  @Nullable
  public PsiElement getAbstract() {
    return SchemaPsiImplUtil.getAbstract(this);
  }

  @Nullable
  public PsiElement getPolymorphic() {
    return SchemaPsiImplUtil.getPolymorphic(this);
  }

  @Nullable
  public SchemaExtendsDecl getExtendsDecl() {
    return SchemaPsiImplUtil.getExtendsDecl(this);
  }

  @Nullable
  public PsiElement getId() {
    return SchemaPsiImplUtil.getId(this);
  }

  @NotNull
  public String toString() {
    return SchemaPsiImplUtil.toString(this);
  }

  @Nullable
  public String getName() {
    return SchemaPsiImplUtil.getName(this);
  }

  @Nullable
  public PsiElement setName(String name) {
    return SchemaPsiImplUtil.setName(this, name);
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    return SchemaPsiImplUtil.getNameIdentifier(this);
  }

  public int getTextOffset() {
    return SchemaPsiImplUtil.getTextOffset(this);
  }

  public void delete() {
    SchemaPsiImplUtil.delete(this);
  }

  @NotNull
  public TypeKind getKind() {
    return SchemaPsiImplUtil.getKind(this);
  }

  public Icon getIcon(int flags) {
    return SchemaPsiImplUtil.getIcon(this, flags);
  }

  @NotNull
  public List<SchemaTypeDef> parents() {
    return SchemaPsiImplUtil.parents(this);
  }

}
