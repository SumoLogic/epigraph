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

public class IdlOpDeleteVarProjectionImpl extends ASTWrapperPsiElement implements IdlOpDeleteVarProjection {

  public IdlOpDeleteVarProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpDeleteVarProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlOpDeleteMultiTagProjection getOpDeleteMultiTagProjection() {
    return findChildByClass(IdlOpDeleteMultiTagProjection.class);
  }

  @Override
  @Nullable
  public IdlOpDeleteSingleTagProjection getOpDeleteSingleTagProjection() {
    return findChildByClass(IdlOpDeleteSingleTagProjection.class);
  }

  @Override
  @Nullable
  public IdlOpDeleteVarPolymorphicTail getOpDeleteVarPolymorphicTail() {
    return findChildByClass(IdlOpDeleteVarPolymorphicTail.class);
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(I_PLUS);
  }

}
