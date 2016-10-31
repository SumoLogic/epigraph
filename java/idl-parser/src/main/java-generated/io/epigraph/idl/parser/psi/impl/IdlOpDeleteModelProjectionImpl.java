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

public class IdlOpDeleteModelProjectionImpl extends ASTWrapperPsiElement implements IdlOpDeleteModelProjection {

  public IdlOpDeleteModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpDeleteModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlOpDeleteListModelProjection getOpDeleteListModelProjection() {
    return findChildByClass(IdlOpDeleteListModelProjection.class);
  }

  @Override
  @Nullable
  public IdlOpDeleteMapModelProjection getOpDeleteMapModelProjection() {
    return findChildByClass(IdlOpDeleteMapModelProjection.class);
  }

  @Override
  @Nullable
  public IdlOpDeleteRecordModelProjection getOpDeleteRecordModelProjection() {
    return findChildByClass(IdlOpDeleteRecordModelProjection.class);
  }

}
