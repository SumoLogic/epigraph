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

public class IdlOpOutputMultiTagProjectionImpl extends ASTWrapperPsiElement implements IdlOpOutputMultiTagProjection {

  public IdlOpOutputMultiTagProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpOutputMultiTagProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlOpOutputMultiTagProjectionItem> getOpOutputMultiTagProjectionItemList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlOpOutputMultiTagProjectionItem.class);
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return findNotNullChildByType(I_COLON);
  }

  @Override
  @NotNull
  public PsiElement getParenLeft() {
    return findNotNullChildByType(I_PAREN_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getParenRight() {
    return findChildByType(I_PAREN_RIGHT);
  }

}
