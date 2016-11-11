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

public class IdlOperationOutputProjectionImpl extends ASTWrapperPsiElement implements IdlOperationOutputProjection {

  public IdlOperationOutputProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOperationOutputProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlOpOutputFieldProjection getOpOutputFieldProjection() {
    return findChildByClass(IdlOpOutputFieldProjection.class);
  }

  @Override
  @NotNull
  public PsiElement getOutputProjection() {
    return findNotNullChildByType(I_OUTPUT_PROJECTION);
  }

}
