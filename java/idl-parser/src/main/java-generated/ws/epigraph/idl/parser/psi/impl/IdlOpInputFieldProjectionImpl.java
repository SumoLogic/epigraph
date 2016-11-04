// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.idl.lexer.IdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.idl.parser.psi.*;

public class IdlOpInputFieldProjectionImpl extends ASTWrapperPsiElement implements IdlOpInputFieldProjection {

  public IdlOpInputFieldProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpInputFieldProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlOpInputFieldProjectionBodyPart> getOpInputFieldProjectionBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlOpInputFieldProjectionBodyPart.class);
  }

  @Override
  @NotNull
  public IdlOpInputVarProjection getOpInputVarProjection() {
    return findNotNullChildByClass(IdlOpInputVarProjection.class);
  }

  @Override
  @NotNull
  public IdlQid getQid() {
    return findNotNullChildByClass(IdlQid.class);
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
