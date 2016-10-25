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

public class IdlOperationDefImpl extends ASTWrapperPsiElement implements IdlOperationDef {

  public IdlOperationDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOperationDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlCreateOperationDef getCreateOperationDef() {
    return findChildByClass(IdlCreateOperationDef.class);
  }

  @Override
  @Nullable
  public IdlCustomOperationDef getCustomOperationDef() {
    return findChildByClass(IdlCustomOperationDef.class);
  }

  @Override
  @Nullable
  public IdlDeleteOperationDef getDeleteOperationDef() {
    return findChildByClass(IdlDeleteOperationDef.class);
  }

  @Override
  @Nullable
  public IdlReadOperationDef getReadOperationDef() {
    return findChildByClass(IdlReadOperationDef.class);
  }

  @Override
  @Nullable
  public IdlUpdateOperationDef getUpdateOperationDef() {
    return findChildByClass(IdlUpdateOperationDef.class);
  }

}
