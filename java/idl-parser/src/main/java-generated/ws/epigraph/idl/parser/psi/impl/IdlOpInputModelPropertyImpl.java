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

public class IdlOpInputModelPropertyImpl extends ASTWrapperPsiElement implements IdlOpInputModelProperty {

  public IdlOpInputModelPropertyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpInputModelProperty(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlAnnotation getAnnotation() {
    return findChildByClass(IdlAnnotation.class);
  }

  @Override
  @Nullable
  public IdlOpInputDefaultValue getOpInputDefaultValue() {
    return findChildByClass(IdlOpInputDefaultValue.class);
  }

  @Override
  @Nullable
  public IdlOpInputModelMeta getOpInputModelMeta() {
    return findChildByClass(IdlOpInputModelMeta.class);
  }

  @Override
  @Nullable
  public IdlOpParam getOpParam() {
    return findChildByClass(IdlOpParam.class);
  }

}
