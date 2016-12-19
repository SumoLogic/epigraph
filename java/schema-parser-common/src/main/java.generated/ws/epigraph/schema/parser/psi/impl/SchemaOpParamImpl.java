// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.schema.parser.psi.*;

public class SchemaOpParamImpl extends ASTWrapperPsiElement implements SchemaOpParam {

  public SchemaOpParamImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpParam(this);
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
  @Nullable
  public SchemaDatum getDatum() {
    return PsiTreeUtil.getChildOfType(this, SchemaDatum.class);
  }

  @Override
  @Nullable
  public SchemaOpInputModelProjection getOpInputModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputModelProjection.class);
  }

  @Override
  @NotNull
  public List<SchemaOpParam> getOpParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpParam.class);
  }

  @Override
  @Nullable
  public SchemaQid getQid() {
    return PsiTreeUtil.getChildOfType(this, SchemaQid.class);
  }

  @Override
  @Nullable
  public SchemaTypeRef getTypeRef() {
    return PsiTreeUtil.getChildOfType(this, SchemaTypeRef.class);
  }

  @Override
  @Nullable
  public PsiElement getColon() {
    return findChildByType(S_COLON);
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
  public PsiElement getEq() {
    return findChildByType(S_EQ);
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(S_PLUS);
  }

  @Override
  @NotNull
  public PsiElement getSemicolon() {
    return notNullChild(findChildByType(S_SEMICOLON));
  }

}
