// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.EdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.schema.parser.psi.*;

public class EdlOpParamImpl extends ASTWrapperPsiElement implements EdlOpParam {

  public EdlOpParamImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpParam(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlAnnotation> getAnnotationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlAnnotation.class);
  }

  @Override
  @Nullable
  public EdlDatum getDatum() {
    return PsiTreeUtil.getChildOfType(this, EdlDatum.class);
  }

  @Override
  @Nullable
  public EdlOpInputModelProjection getOpInputModelProjection() {
    return PsiTreeUtil.getChildOfType(this, EdlOpInputModelProjection.class);
  }

  @Override
  @NotNull
  public List<EdlOpParam> getOpParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlOpParam.class);
  }

  @Override
  @Nullable
  public EdlQid getQid() {
    return PsiTreeUtil.getChildOfType(this, EdlQid.class);
  }

  @Override
  @Nullable
  public EdlTypeRef getTypeRef() {
    return PsiTreeUtil.getChildOfType(this, EdlTypeRef.class);
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
