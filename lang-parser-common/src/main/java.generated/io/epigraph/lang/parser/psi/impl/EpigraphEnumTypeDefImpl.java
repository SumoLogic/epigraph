// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import java.util.List;
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

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitEnumTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EpigraphEnumTypeBody getEnumTypeBody() {
    return PsiTreeUtil.getChildOfType(this, EpigraphEnumTypeBody.class);
  }

  @Override
  @Nullable
  public EpigraphMetaDecl getMetaDecl() {
    return PsiTreeUtil.getChildOfType(this, EpigraphMetaDecl.class);
  }

  @Override
  @Nullable
  public EpigraphQid getQid() {
    return PsiTreeUtil.getChildOfType(this, EpigraphQid.class);
  }

  @Override
  @NotNull
  public PsiElement getEnum() {
    return notNullChild(findChildByType(E_ENUM));
  }

}
