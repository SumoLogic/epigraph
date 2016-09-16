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

public class IdlOpOutputModelProjectionBodyPartImpl extends ASTWrapperPsiElement implements IdlOpOutputModelProjectionBodyPart {

  public IdlOpOutputModelProjectionBodyPartImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpOutputModelProjectionBodyPart(this);
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
  public IdlOpParam getOpParam() {
    return findChildByClass(IdlOpParam.class);
  }

}
