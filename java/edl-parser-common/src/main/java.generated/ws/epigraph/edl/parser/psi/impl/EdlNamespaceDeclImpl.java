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
import ws.epigraph.edl.parser.psi.stubs.EdlNamespaceDeclStub;
import ws.epigraph.edl.parser.psi.*;
import ws.epigraph.lang.Qn;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class EdlNamespaceDeclImpl extends StubBasedPsiElementBase<EdlNamespaceDeclStub> implements EdlNamespaceDecl {

  public EdlNamespaceDeclImpl(EdlNamespaceDeclStub stub, IStubElementType type) {
    super(stub, type);
  }

  public EdlNamespaceDeclImpl(ASTNode node) {
    super(node);
  }

  public EdlNamespaceDeclImpl(EdlNamespaceDeclStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitNamespaceDecl(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlAnnotation> getAnnotationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlAnnotation.class);
  }

  @Override
  @Nullable
  public EdlQn getQn() {
    return PsiTreeUtil.getChildOfType(this, EdlQn.class);
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(E_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(E_CURLY_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getNamespace() {
    return notNullChild(findChildByType(E_NAMESPACE));
  }

  @Nullable
  public Qn getFqn() {
    return EdlPsiImplUtil.getFqn(this);
  }

  @NotNull
  public String toString() {
    return EdlPsiImplUtil.toString(this);
  }

}
