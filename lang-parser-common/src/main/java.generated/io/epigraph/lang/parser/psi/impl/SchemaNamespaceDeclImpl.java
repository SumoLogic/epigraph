// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import java.util.List;

import io.epigraph.lang.parser.psi.stubs.EpigraphNamespaceDeclStub;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import io.epigraph.lang.parser.psi.*;
import io.epigraph.lang.parser.Fqn;

public class SchemaNamespaceDeclImpl extends StubBasedPsiElementBase<EpigraphNamespaceDeclStub> implements SchemaNamespaceDecl {

  public SchemaNamespaceDeclImpl(EpigraphNamespaceDeclStub stub, com.intellij.psi.stubs.IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public SchemaNamespaceDeclImpl(ASTNode node) {
    super(node);
  }

  public SchemaNamespaceDeclImpl(EpigraphNamespaceDeclStub stub, com.intellij.psi.tree.IElementType nodeType, ASTNode node) {
    super(stub, nodeType, node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitNamespaceDecl(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaCustomParam> getCustomParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaCustomParam.class);
  }

  @Override
  @Nullable
  public SchemaFqn getFqn() {
    return PsiTreeUtil.getChildOfType(this, SchemaFqn.class);
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
  public Fqn getFqn2() {
    return EpigraphPsiImplUtil.getFqn2(this);
  }

  @NotNull
  public String toString() {
    return EpigraphPsiImplUtil.toString(this);
  }

}
