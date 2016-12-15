// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.EdlElementTypes.*;
import ws.epigraph.edl.parser.psi.*;
import ws.epigraph.edl.parser.psi.stubs.EdlRecordTypeDefStub;
import com.intellij.psi.stubs.IStubElementType;

public class EdlRecordTypeDefImpl extends EdlRecordTypeDefImplBase implements EdlRecordTypeDef {

  public EdlRecordTypeDefImpl(ASTNode node) {
    super(node);
  }

  public EdlRecordTypeDefImpl(EdlRecordTypeDefStub stub, IStubElementType type) {
    super(stub, type);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitRecordTypeDef(this);
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
  public EdlQid getQid() {
    return PsiTreeUtil.getChildOfType(this, EdlQid.class);
  }

  @Override
  @Nullable
  public EdlRecordTypeBody getRecordTypeBody() {
    return PsiTreeUtil.getChildOfType(this, EdlRecordTypeBody.class);
  }

  @Override
  @Nullable
  public EdlSupplementsDecl getSupplementsDecl() {
    return PsiTreeUtil.getChildOfType(this, EdlSupplementsDecl.class);
  }

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(E_ABSTRACT);
  }

  @Override
  @NotNull
  public PsiElement getRecord() {
    return notNullChild(findChildByType(E_RECORD));
  }

  @NotNull
  public List<EdlTypeDef> supplemented() {
    return EdlPsiImplUtil.supplemented(this);
  }

}
