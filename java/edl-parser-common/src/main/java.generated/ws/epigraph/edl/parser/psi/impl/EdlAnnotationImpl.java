// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.EdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.edl.parser.psi.*;

public class EdlAnnotationImpl extends ASTWrapperPsiElement implements EdlAnnotation {

  public EdlAnnotationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitAnnotation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlDataValue getDataValue() {
    return PsiTreeUtil.getChildOfType(this, EdlDataValue.class);
  }

  @Override
  @NotNull
  public EdlQid getQid() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlQid.class));
  }

  @Override
  @NotNull
  public PsiElement getEq() {
    return notNullChild(findChildByType(E_EQ));
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
