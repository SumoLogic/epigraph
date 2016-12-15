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

public class EdlEnumMemberDeclImpl extends AnnotationsHolderImpl implements EdlEnumMemberDecl {

  public EdlEnumMemberDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitEnumMemberDecl(this);
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
  public PsiElement getCurlyLeft() {
    return findChildByType(E_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(E_CURLY_RIGHT);
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

}
