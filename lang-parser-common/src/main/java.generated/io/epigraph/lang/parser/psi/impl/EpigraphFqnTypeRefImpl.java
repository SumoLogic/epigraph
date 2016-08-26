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

public class EpigraphFqnTypeRefImpl extends EpigraphTypeRefImpl implements EpigraphFqnTypeRef {

  public EpigraphFqnTypeRefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitFqnTypeRef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EpigraphFqn getFqn() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EpigraphFqn.class));
  }

  @Nullable
  public EpigraphTypeDef resolve() {
    return EpigraphPsiImplUtil.resolve(this);
  }

}
