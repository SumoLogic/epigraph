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
import ws.epigraph.schema.parser.psi.stubs.SchemaTypeDefWrapperStub;
import ws.epigraph.schema.parser.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class SchemaTypeDefWrapperImpl extends StubBasedPsiElementBase<SchemaTypeDefWrapperStub> implements SchemaTypeDefWrapper {

  public SchemaTypeDefWrapperImpl(SchemaTypeDefWrapperStub stub, IStubElementType type) {
    super(stub, type);
  }

  public SchemaTypeDefWrapperImpl(ASTNode node) {
    super(node);
  }

  public SchemaTypeDefWrapperImpl(SchemaTypeDefWrapperStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitTypeDefWrapper(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaEnumTypeDef getEnumTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, SchemaEnumTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaListTypeDef getListTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, SchemaListTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaMapTypeDef getMapTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, SchemaMapTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaPrimitiveTypeDef getPrimitiveTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, SchemaPrimitiveTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaRecordTypeDef getRecordTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, SchemaRecordTypeDef.class);
  }

  @Override
  @Nullable
  public SchemaVarTypeDef getVarTypeDef() {
    return PsiTreeUtil.getStubChildOfType(this, SchemaVarTypeDef.class);
  }

  @NotNull
  public SchemaTypeDef getElement() {
    return SchemaPsiImplUtil.getElement(this);
  }

  public void delete() {
    SchemaPsiImplUtil.delete(this);
  }

  @NotNull
  public String toString() {
    return SchemaPsiImplUtil.toString(this);
  }

}
