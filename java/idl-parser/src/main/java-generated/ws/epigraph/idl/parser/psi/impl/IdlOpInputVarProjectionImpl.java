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

public class IdlOpInputVarProjectionImpl extends ASTWrapperPsiElement implements IdlOpInputVarProjection {

  public IdlOpInputVarProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpInputVarProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlOpInputMultiTagProjection getOpInputMultiTagProjection() {
    return findChildByClass(IdlOpInputMultiTagProjection.class);
  }

  @Override
  @Nullable
  public IdlOpInputSingleTagProjection getOpInputSingleTagProjection() {
    return findChildByClass(IdlOpInputSingleTagProjection.class);
  }

  @Override
  @Nullable
  public IdlOpInputVarPolymorphicTail getOpInputVarPolymorphicTail() {
    return findChildByClass(IdlOpInputVarPolymorphicTail.class);
  }

}
