// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.EdlElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import ws.epigraph.edl.parser.psi.stubs.EdlSupplementDefStub;
import ws.epigraph.edl.parser.psi.*;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class EdlSupplementDefImpl extends StubBasedPsiElementBase<EdlSupplementDefStub> implements EdlSupplementDef {

  public EdlSupplementDefImpl(EdlSupplementDefStub stub, IStubElementType type) {
    super(stub, type);
  }

  public EdlSupplementDefImpl(ASTNode node) {
    super(node);
  }

  public EdlSupplementDefImpl(EdlSupplementDefStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitSupplementDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlQnTypeRef> getQnTypeRefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlQnTypeRef.class);
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
  public EdlQnTypeRef sourceRef() {
    return EdlPsiImplUtil.sourceRef(this);
  }

  @NotNull
  public List<EdlQnTypeRef> supplementedRefs() {
    return EdlPsiImplUtil.supplementedRefs(this);
  }

  @Nullable
  public EdlTypeDef source() {
    return EdlPsiImplUtil.source(this);
  }

  @NotNull
  public List<EdlTypeDef> supplemented() {
    return EdlPsiImplUtil.supplemented(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return EdlPsiImplUtil.getPresentation(this);
  }

  @NotNull
  public String toString() {
    return EdlPsiImplUtil.toString(this);
  }

}
