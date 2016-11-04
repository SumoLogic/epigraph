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

public class IdlOpOutputVarSingleTailImpl extends ASTWrapperPsiElement implements IdlOpOutputVarSingleTail {

  public IdlOpOutputVarSingleTailImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpOutputVarSingleTail(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public IdlOpOutputVarProjection getOpOutputVarProjection() {
    return findNotNullChildByClass(IdlOpOutputVarProjection.class);
  }

  @Override
  @NotNull
  public IdlTypeRef getTypeRef() {
    return findNotNullChildByClass(IdlTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getTilda() {
    return findNotNullChildByType(I_TILDA);
  }

}
