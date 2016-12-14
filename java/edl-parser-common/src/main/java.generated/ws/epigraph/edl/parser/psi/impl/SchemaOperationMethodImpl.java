// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.edl.parser.psi.*;

public class SchemaOperationMethodImpl extends ASTWrapperPsiElement implements SchemaOperationMethod {

  public SchemaOperationMethodImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOperationMethod(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
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
