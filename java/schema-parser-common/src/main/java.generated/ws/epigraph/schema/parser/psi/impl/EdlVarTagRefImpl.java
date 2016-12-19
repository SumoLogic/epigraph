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
import com.intellij.psi.PsiReference;

public class EdlVarTagRefImpl extends ASTWrapperPsiElement implements EdlVarTagRef {

  public EdlVarTagRefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitVarTagRef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public EdlQid getQid() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlQid.class));
  }

  public PsiElement setName(String name) {
    return EdlPsiImplUtil.setName(this, name);
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    return EdlPsiImplUtil.getNameIdentifier(this);
  }

  @Nullable
  public PsiReference getReference() {
    return EdlPsiImplUtil.getReference(this);
  }

}
