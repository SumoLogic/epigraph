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

public class IdlOpOutputModelProjectionImpl extends ASTWrapperPsiElement implements IdlOpOutputModelProjection {

  public IdlOpOutputModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpOutputModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlOpOutputEnumModelProjection getOpOutputEnumModelProjection() {
    return findChildByClass(IdlOpOutputEnumModelProjection.class);
  }

  @Override
  @Nullable
  public IdlOpOutputModelProjection getOpOutputModelProjection() {
    return findChildByClass(IdlOpOutputModelProjection.class);
  }

  @Override
  @Nullable
  public IdlOpOutputModelProjectionBody getOpOutputModelProjectionBody() {
    return findChildByClass(IdlOpOutputModelProjectionBody.class);
  }

  @Override
  @Nullable
  public IdlOpOutputPrimitiveModelProjection getOpOutputPrimitiveModelProjection() {
    return findChildByClass(IdlOpOutputPrimitiveModelProjection.class);
  }

}
