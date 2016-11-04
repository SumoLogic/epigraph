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

public class IdlOpDeleteMapModelProjectionImpl extends ASTWrapperPsiElement implements IdlOpDeleteMapModelProjection {

  public IdlOpDeleteMapModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpDeleteMapModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public IdlOpDeleteKeyProjection getOpDeleteKeyProjection() {
    return findNotNullChildByClass(IdlOpDeleteKeyProjection.class);
  }

  @Override
  @Nullable
  public IdlOpDeleteVarProjection getOpDeleteVarProjection() {
    return findChildByClass(IdlOpDeleteVarProjection.class);
  }

  @Override
  @Nullable
  public PsiElement getParenLeft() {
    return findChildByType(I_PAREN_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getParenRight() {
    return findChildByType(I_PAREN_RIGHT);
  }

}
