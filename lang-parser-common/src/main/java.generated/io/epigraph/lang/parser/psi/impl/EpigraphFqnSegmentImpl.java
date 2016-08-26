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
import com.intellij.psi.PsiReference;
import io.epigraph.lang.parser.Fqn;

public class EpigraphFqnSegmentImpl extends ASTWrapperPsiElement implements EpigraphFqnSegment {

  public EpigraphFqnSegmentImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EpigraphVisitor visitor) {
    visitor.visitFqnSegment(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EpigraphVisitor) accept((EpigraphVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EpigraphQid getQid() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EpigraphQid.class));
  }

  @Nullable
  public String getName() {
    return EpigraphPsiImplUtil.getName(this);
  }

  @NotNull
  public PsiElement setName(String name) {
    return EpigraphPsiImplUtil.setName(this, name);
  }

  @NotNull
  public PsiElement getNameIdentifier() {
    return EpigraphPsiImplUtil.getNameIdentifier(this);
  }

  @Nullable
  public EpigraphFqn getSchemaFqn() {
    return EpigraphPsiImplUtil.getSchemaFqn(this);
  }

  @Nullable
  public EpigraphFqnTypeRef getSchemaFqnTypeRef() {
    return EpigraphPsiImplUtil.getSchemaFqnTypeRef(this);
  }

  public boolean isLast() {
    return EpigraphPsiImplUtil.isLast(this);
  }

  @Nullable
  public PsiReference getReference() {
    return EpigraphPsiImplUtil.getReference(this);
  }

  @NotNull
  public Fqn getFqn() {
    return EpigraphPsiImplUtil.getFqn(this);
  }

}
