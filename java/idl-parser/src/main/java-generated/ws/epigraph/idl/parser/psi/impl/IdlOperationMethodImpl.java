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

public class IdlOperationMethodImpl extends ASTWrapperPsiElement implements IdlOperationMethod {

  public IdlOperationMethodImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOperationMethod(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getDelete() {
    return findChildByType(I_DELETE);
  }

  @Override
  @Nullable
  public PsiElement getGet() {
    return findChildByType(I_GET);
  }

  @Override
  @NotNull
  public PsiElement getMethod() {
    return findNotNullChildByType(I_METHOD);
  }

  @Override
  @Nullable
  public PsiElement getPost() {
    return findChildByType(I_POST);
  }

  @Override
  @Nullable
  public PsiElement getPut() {
    return findChildByType(I_PUT);
  }

}
