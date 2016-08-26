// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.lang.parser.psi.*;

public class EpigraphValueTypeRefImpl extends ASTWrapperPsiElement implements EpigraphValueTypeRef {

  public EpigraphValueTypeRefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitValueTypeRef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EpigraphDefaultOverride getDefaultOverride() {
    return PsiTreeUtil.getChildOfType(this, EpigraphDefaultOverride.class);
  }

  @Override
  @NotNull
  public EpigraphTypeRef getTypeRef() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EpigraphTypeRef.class));
  }

  @Override
  @Nullable
  public PsiElement getPolymorphic() {
    return findChildByType(E_POLYMORPHIC);
  }

}
