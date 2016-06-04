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
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaSupplementDefStub;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.stubs.IStubElementType;

public class SchemaSupplementDefImpl extends StubBasedPsiElementBase<SchemaSupplementDefStub> implements SchemaSupplementDef {

  public SchemaSupplementDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaSupplementDefImpl(SchemaSupplementDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitSupplementDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaFqnTypeRef> getFqnTypeRefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaFqnTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getSupplement() {
    return findNotNullChildByType(S_SUPPLEMENT);
  }

  @Override
  @Nullable
  public PsiElement getWith() {
    return findChildByType(S_WITH);
  }

  @Nullable
  public SchemaFqnTypeRef sourceRef() {
    return SchemaPsiImplUtil.sourceRef(this);
  }

  @NotNull
  public List<SchemaFqnTypeRef> supplementedRefs() {
    return SchemaPsiImplUtil.supplementedRefs(this);
  }

  @Nullable
  public SchemaTypeDef source() {
    return SchemaPsiImplUtil.source(this);
  }

  @NotNull
  public List<SchemaTypeDef> supplemented() {
    return SchemaPsiImplUtil.supplemented(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return SchemaPsiImplUtil.getPresentation(this);
  }

  @NotNull
  public String toString() {
    return SchemaPsiImplUtil.toString(this);
  }

}
