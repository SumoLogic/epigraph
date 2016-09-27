// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.idl.lexer.IdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.idl.parser.psi.*;

public class IdlOpInputTrunkSingleTagProjectionImpl extends ASTWrapperPsiElement implements IdlOpInputTrunkSingleTagProjection {

  public IdlOpInputTrunkSingleTagProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpInputTrunkSingleTagProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlOpInputModelProperty> getOpInputModelPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlOpInputModelProperty.class);
  }

  @Override
  @Nullable
  public IdlOpInputTrunkModelProjection getOpInputTrunkModelProjection() {
    return findChildByClass(IdlOpInputTrunkModelProjection.class);
  }

  @Override
  @Nullable
  public IdlOpTagName getOpTagName() {
    return findChildByClass(IdlOpTagName.class);
  }

  @Override
  @Nullable
  public PsiElement getColon() {
    return findChildByType(I_COLON);
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(I_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(I_CURLY_RIGHT);
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(I_PLUS);
  }

}
