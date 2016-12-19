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

public class EdlDefsImpl extends ASTWrapperPsiElement implements EdlDefs {

  public EdlDefsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitDefs(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlResourceDef> getResourceDefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlResourceDef.class);
  }

  @Override
  @NotNull
  public List<EdlSupplementDef> getSupplementDefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlSupplementDef.class);
  }

  @Override
  @NotNull
  public List<EdlTypeDefWrapper> getTypeDefWrapperList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlTypeDefWrapper.class);
  }

}
