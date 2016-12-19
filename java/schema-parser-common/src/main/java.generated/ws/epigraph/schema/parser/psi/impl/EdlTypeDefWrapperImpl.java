// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.EdlElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import ws.epigraph.schema.parser.psi.stubs.EdlTypeDefWrapperStub;
import ws.epigraph.schema.parser.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class EdlTypeDefWrapperImpl extends StubBasedPsiElementBase<EdlTypeDefWrapperStub> implements EdlTypeDefWrapper {

  public EdlTypeDefWrapperImpl(EdlTypeDefWrapperStub stub, IStubElementType type) {
    super(stub, type);
  }

  public EdlTypeDefWrapperImpl(ASTNode node) {
    super(node);
  }

  public EdlTypeDefWrapperImpl(EdlTypeDefWrapperStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitTypeDefWrapper(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlEnumTypeDef getEnumTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EdlEnumTypeDef.class);
  }

  @Override
  @Nullable
  public EdlListTypeDef getListTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EdlListTypeDef.class);
  }

  @Override
  @Nullable
  public EdlMapTypeDef getMapTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EdlMapTypeDef.class);
  }

  @Override
  @Nullable
  public EdlPrimitiveTypeDef getPrimitiveTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EdlPrimitiveTypeDef.class);
  }

  @Override
  @Nullable
  public EdlRecordTypeDef getRecordTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EdlRecordTypeDef.class);
  }

  @Override
  @Nullable
  public EdlVarTypeDef getVarTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, EdlVarTypeDef.class);
  }

  @NotNull
  public EdlTypeDef getElement() {
    return EdlPsiImplUtil.getElement(this);
  }

  public void delete() {
    EdlPsiImplUtil.delete(this);
  }

  @NotNull
  public String toString() {
    return EdlPsiImplUtil.toString(this);
  }

}
