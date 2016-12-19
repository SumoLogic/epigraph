// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.EdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.schema.parser.psi.*;

public class EdlOperationMethodImpl extends ASTWrapperPsiElement implements EdlOperationMethod {

  public EdlOperationMethodImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOperationMethod(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getDelete() {
    return findChildByType(S_DELETE);
  }

  @Override
  @Nullable
  public PsiElement getGet() {
    return findChildByType(S_GET);
  }

  @Override
  @NotNull
  public PsiElement getMethod() {
    return notNullChild(findChildByType(S_METHOD));
  }

  @Override
  @Nullable
  public PsiElement getPost() {
    return findChildByType(S_POST);
  }

  @Override
  @Nullable
  public PsiElement getPut() {
    return findChildByType(S_PUT);
  }

}
