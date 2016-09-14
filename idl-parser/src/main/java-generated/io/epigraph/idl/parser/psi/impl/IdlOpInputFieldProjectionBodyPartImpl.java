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

public class IdlOpInputFieldProjectionBodyPartImpl extends ASTWrapperPsiElement implements IdlOpInputFieldProjectionBodyPart {

  public IdlOpInputFieldProjectionBodyPartImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpInputFieldProjectionBodyPart(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlCustomParam getCustomParam() {
    return findChildByClass(IdlCustomParam.class);
  }

  @Override
  @Nullable
  public IdlOpInputRequired getOpInputRequired() {
    return findChildByClass(IdlOpInputRequired.class);
  }

}
