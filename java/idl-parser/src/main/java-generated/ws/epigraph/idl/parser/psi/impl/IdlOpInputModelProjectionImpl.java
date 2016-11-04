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

public class IdlOpInputModelProjectionImpl extends ASTWrapperPsiElement implements IdlOpInputModelProjection {

  public IdlOpInputModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpInputModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlOpInputListModelProjection getOpInputListModelProjection() {
    return findChildByClass(IdlOpInputListModelProjection.class);
  }

  @Override
  @Nullable
  public IdlOpInputMapModelProjection getOpInputMapModelProjection() {
    return findChildByClass(IdlOpInputMapModelProjection.class);
  }

  @Override
  @Nullable
  public IdlOpInputRecordModelProjection getOpInputRecordModelProjection() {
    return findChildByClass(IdlOpInputRecordModelProjection.class);
  }

}
