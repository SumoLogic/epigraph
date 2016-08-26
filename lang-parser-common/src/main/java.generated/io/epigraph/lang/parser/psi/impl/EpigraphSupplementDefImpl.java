// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import io.epigraph.lang.parser.psi.stubs.EpigraphSupplementDefStub;
import io.epigraph.lang.parser.psi.*;
import com.intellij.navigation.ItemPresentation;

public class EpigraphSupplementDefImpl extends StubBasedPsiElementBase<EpigraphSupplementDefStub> implements EpigraphSupplementDef {

  public EpigraphSupplementDefImpl(EpigraphSupplementDefStub stub, com.intellij.psi.stubs.IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public EpigraphSupplementDefImpl(ASTNode node) {
    super(node);
  }

  public EpigraphSupplementDefImpl(EpigraphSupplementDefStub stub, com.intellij.psi.tree.IElementType nodeType, ASTNode node) {
    super(stub, nodeType, node);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitSupplementDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EpigraphFqnTypeRef> getFqnTypeRefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EpigraphFqnTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getSupplement() {
    return notNullChild(findChildByType(E_SUPPLEMENT));
  }

  @Override
  @Nullable
  public PsiElement getWith() {
    return findChildByType(E_WITH);
  }

  @Nullable
  public EpigraphFqnTypeRef sourceRef() {
    return EpigraphPsiImplUtil.sourceRef(this);
  }

  @NotNull
  public List<EpigraphFqnTypeRef> supplementedRefs() {
    return EpigraphPsiImplUtil.supplementedRefs(this);
  }

  @Nullable
  public EpigraphTypeDef source() {
    return EpigraphPsiImplUtil.source(this);
  }

  @NotNull
  public List<EpigraphTypeDef> supplemented() {
    return EpigraphPsiImplUtil.supplemented(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return EpigraphPsiImplUtil.getPresentation(this);
  }

  @NotNull
  public String toString() {
    return EpigraphPsiImplUtil.toString(this);
  }

}
