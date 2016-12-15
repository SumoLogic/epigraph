// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.EdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.edl.parser.psi.*;
import com.intellij.psi.PsiReference;
import ws.epigraph.lang.Qn;

public class EdlQnSegmentImpl extends ASTWrapperPsiElement implements EdlQnSegment {

  public EdlQnSegmentImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitQnSegment(this);
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

  @Nullable
  public String getName() {
    return EdlPsiImplUtil.getName(this);
  }

  @NotNull
  public PsiElement setName(String name) {
    return EdlPsiImplUtil.setName(this, name);
  }

  @NotNull
  public PsiElement getNameIdentifier() {
    return EdlPsiImplUtil.getNameIdentifier(this);
  }

  @Nullable
  public EdlQn getEdlFqn() {
    return EdlPsiImplUtil.getEdlFqn(this);
  }

  @Nullable
  public EdlQnTypeRef getEdlFqnTypeRef() {
    return EdlPsiImplUtil.getEdlFqnTypeRef(this);
  }

  public boolean isLast() {
    return EdlPsiImplUtil.isLast(this);
  }

  @Nullable
  public PsiReference getReference() {
    return EdlPsiImplUtil.getReference(this);
  }

  @NotNull
  public Qn getQn() {
    return EdlPsiImplUtil.getQn(this);
  }

}
