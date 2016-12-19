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
import com.intellij.navigation.ItemPresentation;

public class EdlFieldDeclImpl extends AnnotationsHolderImpl implements EdlFieldDecl {

  public EdlFieldDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitFieldDecl(this);
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
  @NotNull
  public EdlQid getQid() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlQid.class));
  }

  @Override
  @Nullable
  public EdlValueTypeRef getValueTypeRef() {
    return PsiTreeUtil.getChildOfType(this, EdlValueTypeRef.class);
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
    return EdlPsiImplUtil.getName(this);
  }

  public PsiElement setName(String name) {
    return EdlPsiImplUtil.setName(this, name);
  }

  @NotNull
  public PsiElement getNameIdentifier() {
    return EdlPsiImplUtil.getNameIdentifier(this);
  }

  public int getTextOffset() {
    return EdlPsiImplUtil.getTextOffset(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return EdlPsiImplUtil.getPresentation(this);
  }

  @NotNull
  public EdlRecordTypeDef getRecordTypeDef() {
    return EdlPsiImplUtil.getRecordTypeDef(this);
  }

}
