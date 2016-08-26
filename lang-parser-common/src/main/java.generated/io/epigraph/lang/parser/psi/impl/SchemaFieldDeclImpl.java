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
import com.intellij.navigation.ItemPresentation;

public class SchemaFieldDeclImpl extends CustomParamHolderImpl implements SchemaFieldDecl {

  public SchemaFieldDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitFieldDecl(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaCustomParam> getCustomParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaCustomParam.class);
  }

  @Override
  @NotNull
  public SchemaQid getQid() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaQid.class));
  }

  @Override
  @Nullable
  public SchemaValueTypeRef getValueTypeRef() {
    return PsiTreeUtil.getChildOfType(this, SchemaValueTypeRef.class);
  }

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(E_ABSTRACT);
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return notNullChild(findChildByType(E_COLON));
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(E_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(E_CURLY_RIGHT);
  }

  @Override
  @Nullable
  public PsiElement getOverride() {
    return findChildByType(E_OVERRIDE);
  }

  @Nullable
  public String getName() {
    return EpigraphPsiImplUtil.getName(this);
  }

  public PsiElement setName(String name) {
    return EpigraphPsiImplUtil.setName(this, name);
  }

  @NotNull
  public PsiElement getNameIdentifier() {
    return EpigraphPsiImplUtil.getNameIdentifier(this);
  }

  public int getTextOffset() {
    return EpigraphPsiImplUtil.getTextOffset(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return EpigraphPsiImplUtil.getPresentation(this);
  }

  @NotNull
  public SchemaRecordTypeDef getRecordTypeDef() {
    return EpigraphPsiImplUtil.getRecordTypeDef(this);
  }

}
