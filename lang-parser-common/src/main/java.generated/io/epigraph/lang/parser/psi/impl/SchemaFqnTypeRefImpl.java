// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import io.epigraph.lang.parser.psi.*;

public class SchemaFqnTypeRefImpl extends SchemaTypeRefImpl implements SchemaFqnTypeRef {

  public SchemaFqnTypeRefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitFqnTypeRef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SchemaFqn getFqn() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaFqn.class));
  }

  @Nullable
  public SchemaTypeDef resolve() {
    return EpigraphPsiImplUtil.resolve(this);
  }

}
