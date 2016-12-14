// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import ws.epigraph.schema.parser.psi.stubs.SchemaNamespaceDeclStub;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.lang.Qn;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class SchemaNamespaceDeclImpl extends StubBasedPsiElementBase<SchemaNamespaceDeclStub> implements SchemaNamespaceDecl {

  public SchemaNamespaceDeclImpl(SchemaNamespaceDeclStub stub, IStubElementType type) {
    super(stub, type);
  }

  public SchemaNamespaceDeclImpl(ASTNode node) {
    super(node);
  }

  public SchemaNamespaceDeclImpl(SchemaNamespaceDeclStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
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
  public SchemaQn getQn() {
    return PsiTreeUtil.getChildOfType(this, SchemaQn.class);
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
  public Qn getFqn() {
    return SchemaPsiImplUtil.getFqn(this);
  }

  @NotNull
  public String toString() {
    return SchemaPsiImplUtil.toString(this);
  }

}
