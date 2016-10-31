// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.schema.lexer.SchemaElementTypes.*;
import io.epigraph.schema.parser.psi.*;
import com.intellij.navigation.ItemPresentation;

public class SchemaVarTagDeclImpl extends AnnotationsHolderImpl implements SchemaVarTagDecl {

  public SchemaVarTagDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitVarTagDecl(this);
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
  @NotNull
  public SchemaQid getQid() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaQid.class));
  }

  @Override
  @Nullable
  public SchemaTypeRef getTypeRef() {
    return PsiTreeUtil.getChildOfType(this, SchemaTypeRef.class);
  }

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(S_ABSTRACT);
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return notNullChild(findChildByType(S_COLON));
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
  @Nullable
  public PsiElement getOverride() {
    return findChildByType(S_OVERRIDE);
  }

  @Nullable
  public String getName() {
    return SchemaPsiImplUtil.getName(this);
  }

  public PsiElement setName(String name) {
    return SchemaPsiImplUtil.setName(this, name);
  }

  @NotNull
  public PsiElement getNameIdentifier() {
    return SchemaPsiImplUtil.getNameIdentifier(this);
  }

  public int getTextOffset() {
    return SchemaPsiImplUtil.getTextOffset(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return SchemaPsiImplUtil.getPresentation(this);
  }

  @NotNull
  public SchemaVarTypeDef getVarTypeDef() {
    return SchemaPsiImplUtil.getVarTypeDef(this);
  }

}
