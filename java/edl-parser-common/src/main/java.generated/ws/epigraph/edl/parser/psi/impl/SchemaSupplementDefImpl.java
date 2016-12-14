// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import ws.epigraph.edl.parser.psi.stubs.SchemaSupplementDefStub;
import ws.epigraph.edl.parser.psi.*;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class SchemaSupplementDefImpl extends StubBasedPsiElementBase<SchemaSupplementDefStub> implements SchemaSupplementDef {

  public SchemaSupplementDefImpl(SchemaSupplementDefStub stub, IStubElementType type) {
    super(stub, type);
  }

  public SchemaSupplementDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaSupplementDefImpl(SchemaSupplementDefStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitSupplementDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaQnTypeRef> getQnTypeRefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaQnTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getSupplement() {
    return notNullChild(findChildByType(S_SUPPLEMENT));
  }

  @Override
  @Nullable
  public PsiElement getWith() {
    return findChildByType(S_WITH);
  }

  @Nullable
  public SchemaQnTypeRef sourceRef() {
    return SchemaPsiImplUtil.sourceRef(this);
  }

  @NotNull
  public List<SchemaQnTypeRef> supplementedRefs() {
    return SchemaPsiImplUtil.supplementedRefs(this);
  }

  @Nullable
  public SchemaTypeDef source() {
    return SchemaPsiImplUtil.source(this);
  }

  @NotNull
  public List<SchemaTypeDef> supplemented() {
    return SchemaPsiImplUtil.supplemented(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return SchemaPsiImplUtil.getPresentation(this);
  }

  @NotNull
  public String toString() {
    return SchemaPsiImplUtil.toString(this);
  }

}
