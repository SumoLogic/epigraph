// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import io.epigraph.lang.schema.parser.psi.*;
import io.epigraph.lang.schema.parser.psi.stubs.SchemaVarTypeDefStub;
import com.intellij.psi.stubs.IStubElementType;

public class SchemaVarTypeDefImpl extends SchemaVarTypeDefImplBase implements SchemaVarTypeDef {

  public SchemaVarTypeDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaVarTypeDefImpl(SchemaVarTypeDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitVarTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaDefaultOverride getDefaultOverride() {
    return PsiTreeUtil.getChildOfType(this, SchemaDefaultOverride.class);
  }

  @Override
  @Nullable
  public SchemaExtendsDecl getExtendsDecl() {
    return PsiTreeUtil.getChildOfType(this, SchemaExtendsDecl.class);
  }

  @Override
  @Nullable
  public SchemaQid getQid() {
    return PsiTreeUtil.getChildOfType(this, SchemaQid.class);
  }

  @Override
  @Nullable
  public SchemaSupplementsDecl getSupplementsDecl() {
    return PsiTreeUtil.getChildOfType(this, SchemaSupplementsDecl.class);
  }

  @Override
  @Nullable
  public SchemaVarTypeBody getVarTypeBody() {
    return PsiTreeUtil.getChildOfType(this, SchemaVarTypeBody.class);
  }

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(E_ABSTRACT);
  }

  @Override
  @NotNull
  public PsiElement getVartype() {
    return notNullChild(findChildByType(E_VARTYPE));
  }

  @NotNull
  public List<SchemaTypeDef> supplemented() {
    return SchemaPsiImplUtil.supplemented(this);
  }

}
