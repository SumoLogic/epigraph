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

public class EdlOperationDefImpl extends ASTWrapperPsiElement implements EdlOperationDef {

  public EdlOperationDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOperationDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlCreateOperationDef getCreateOperationDef() {
    return PsiTreeUtil.getChildOfType(this, EdlCreateOperationDef.class);
  }

  @Override
  @Nullable
  public EdlCustomOperationDef getCustomOperationDef() {
    return PsiTreeUtil.getChildOfType(this, EdlCustomOperationDef.class);
  }

  @Override
  @Nullable
  public EdlDeleteOperationDef getDeleteOperationDef() {
    return PsiTreeUtil.getChildOfType(this, EdlDeleteOperationDef.class);
  }

  @Override
  @Nullable
  public EdlReadOperationDef getReadOperationDef() {
    return PsiTreeUtil.getChildOfType(this, EdlReadOperationDef.class);
  }

  @Override
  @Nullable
  public EdlUpdateOperationDef getUpdateOperationDef() {
    return PsiTreeUtil.getChildOfType(this, EdlUpdateOperationDef.class);
  }

}
