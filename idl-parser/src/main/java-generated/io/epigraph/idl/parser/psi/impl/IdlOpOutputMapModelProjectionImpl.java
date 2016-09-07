// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.idl.lexer.IdlElementTypes.*;
import io.epigraph.idl.parser.psi.*;

public class IdlOpOutputMapModelProjectionImpl extends IdlOpOutputModelProjectionImpl implements IdlOpOutputMapModelProjection {

  public IdlOpOutputMapModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpOutputMapModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public IdlOpOutputKeyProjection getOpOutputKeyProjection() {
    return findNotNullChildByClass(IdlOpOutputKeyProjection.class);
  }

  @Override
  @Nullable
  public IdlOpOutputMapPolyBranch getOpOutputMapPolyBranch() {
    return findChildByClass(IdlOpOutputMapPolyBranch.class);
  }

  @Override
  @Nullable
  public IdlOpOutputVarProjection getOpOutputVarProjection() {
    return findChildByClass(IdlOpOutputVarProjection.class);
  }

  @Override
  @Nullable
  public PsiElement getStar() {
    return findChildByType(I_STAR);
  }

}
