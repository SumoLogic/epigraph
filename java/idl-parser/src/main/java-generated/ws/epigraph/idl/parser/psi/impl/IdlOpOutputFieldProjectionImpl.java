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

public class IdlOpOutputFieldProjectionImpl extends ASTWrapperPsiElement implements IdlOpOutputFieldProjection {

  public IdlOpOutputFieldProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpOutputFieldProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlOpOutputFieldProjectionBodyPart> getOpOutputFieldProjectionBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlOpOutputFieldProjectionBodyPart.class);
  }

  @Override
  @NotNull
  public IdlOpOutputVarProjection getOpOutputVarProjection() {
    return findNotNullChildByClass(IdlOpOutputVarProjection.class);
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

}
