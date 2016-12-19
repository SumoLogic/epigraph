// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.EdlElementTypes.*;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.schema.parser.psi.stubs.EdlPrimitiveTypeDefStub;
import com.intellij.psi.stubs.IStubElementType;

public class EdlPrimitiveTypeDefImpl extends EdlPrimitiveTypeDefImplBase implements EdlPrimitiveTypeDef {

  public EdlPrimitiveTypeDefImpl(ASTNode node) {
    super(node);
  }

  public EdlPrimitiveTypeDefImpl(EdlPrimitiveTypeDefStub stub, IStubElementType type) {
    super(stub, type);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitPrimitiveTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlExtendsDecl getExtendsDecl() {
    return PsiTreeUtil.getChildOfType(this, EdlExtendsDecl.class);
  }

  @Override
  @Nullable
  public EdlMetaDecl getMetaDecl() {
    return PsiTreeUtil.getChildOfType(this, EdlMetaDecl.class);
  }

  @Override
  @Nullable
  public EdlPrimitiveTypeBody getPrimitiveTypeBody() {
    return PsiTreeUtil.getChildOfType(this, EdlPrimitiveTypeBody.class);
  }

  @Override
  @Nullable
  public EdlQid getQid() {
    return PsiTreeUtil.getChildOfType(this, EdlQid.class);
  }

  @Override
  @Nullable
  public EdlSupplementsDecl getSupplementsDecl() {
    return PsiTreeUtil.getChildOfType(this, EdlSupplementsDecl.class);
  }

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(S_ABSTRACT);
  }

  @Override
  @Nullable
  public PsiElement getBooleanT() {
    return findChildByType(S_BOOLEAN_T);
  }

  @Override
  @Nullable
  public PsiElement getDoubleT() {
    return findChildByType(S_DOUBLE_T);
  }

  @Override
  @Nullable
  public PsiElement getIntegerT() {
    return findChildByType(S_INTEGER_T);
  }

  @Override
  @Nullable
  public PsiElement getLongT() {
    return findChildByType(S_LONG_T);
  }

  @Override
  @Nullable
  public PsiElement getStringT() {
    return findChildByType(S_STRING_T);
  }

  @NotNull
  public PrimitiveTypeKind getPrimitiveTypeKind() {
    return EdlPsiImplUtil.getPrimitiveTypeKind(this);
  }

}
