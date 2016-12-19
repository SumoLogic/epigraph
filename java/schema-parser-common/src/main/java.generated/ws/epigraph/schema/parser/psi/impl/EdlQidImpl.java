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

public class EdlQidImpl extends ASTWrapperPsiElement implements EdlQid {

  public EdlQidImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitQid(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return notNullChild(findChildByType(S_ID));
  }

  @NotNull
  public String getName() {
    return EdlPsiImplUtil.getName(this);
  }

  @NotNull
  public PsiElement setName(String name) {
    return EdlPsiImplUtil.setName(this, name);
  }

  @NotNull
  public String getCanonicalName() {
    return EdlPsiImplUtil.getCanonicalName(this);
  }

}
