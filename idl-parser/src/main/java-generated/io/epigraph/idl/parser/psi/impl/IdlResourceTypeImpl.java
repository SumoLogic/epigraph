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

public class IdlResourceTypeImpl extends ASTWrapperPsiElement implements IdlResourceType {

  public IdlResourceTypeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitResourceType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlDefaultOverride getDefaultOverride() {
    return findChildByClass(IdlDefaultOverride.class);
  }

  @Override
  @NotNull
  public IdlFqnTypeRef getFqnTypeRef() {
    return findNotNullChildByClass(IdlFqnTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return findNotNullChildByType(I_COLON);
  }

}
