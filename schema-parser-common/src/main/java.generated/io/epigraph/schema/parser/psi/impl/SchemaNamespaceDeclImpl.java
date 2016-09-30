// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import io.epigraph.schema.parser.psi.stubs.SchemaNamespaceDeclStub;
import io.epigraph.schema.parser.psi.*;
import io.epigraph.lang.Fqn;

public class SchemaNamespaceDeclImpl extends StubBasedPsiElementBase<SchemaNamespaceDeclStub> implements SchemaNamespaceDecl {

  public SchemaNamespaceDeclImpl(SchemaNamespaceDeclStub stub, com.intellij.psi.stubs.IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public SchemaNamespaceDeclImpl(ASTNode node) {
    super(node);
  }

  public SchemaNamespaceDeclImpl(SchemaNamespaceDeclStub stub, com.intellij.psi.tree.IElementType nodeType, ASTNode node) {
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
  public List<SchemaAnnotation> getAnnotationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaAnnotation.class);
  }

  @Override
  @Nullable
  public SchemaFqn getFqn() {
    return PsiTreeUtil.getChildOfType(this, SchemaFqn.class);
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(S_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(S_CURLY_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getNamespace() {
    return notNullChild(findChildByType(S_NAMESPACE));
  }

  @Nullable
  public Fqn getFqn2() {
    return SchemaPsiImplUtil.getFqn2(this);
  }

  @NotNull
  public String toString() {
    return SchemaPsiImplUtil.toString(this);
  }

}
