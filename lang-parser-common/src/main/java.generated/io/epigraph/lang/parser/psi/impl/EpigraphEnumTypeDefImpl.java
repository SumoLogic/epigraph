// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import io.epigraph.lang.parser.psi.*;
import io.epigraph.lang.parser.psi.stubs.EpigraphEnumTypeDefStub;
import com.intellij.psi.stubs.IStubElementType;

public class EpigraphEnumTypeDefImpl extends EpigraphEnumTypeDefImplBase implements EpigraphEnumTypeDef {

  public EpigraphEnumTypeDefImpl(ASTNode node) {
    super(node);
  }

  public EpigraphEnumTypeDefImpl(EpigraphEnumTypeDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitEnumTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaEnumTypeBody getEnumTypeBody() {
    return PsiTreeUtil.getChildOfType(this, SchemaEnumTypeBody.class);
  }

  @Override
  @Nullable
  public SchemaMetaDecl getMetaDecl() {
    return PsiTreeUtil.getChildOfType(this, SchemaMetaDecl.class);
  }

  @Override
  @Nullable
  public SchemaQid getQid() {
    return PsiTreeUtil.getChildOfType(this, SchemaQid.class);
  }

  @Override
  @NotNull
  public PsiElement getEnum() {
    return notNullChild(findChildByType(E_ENUM));
  }

}
