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

public class EdlDataImpl extends ASTWrapperPsiElement implements EdlData {

  public EdlDataImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitData(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlDataEntry> getDataEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlDataEntry.class);
  }

  @Override
  @Nullable
  public EdlTypeRef getTypeRef() {
    return PsiTreeUtil.getChildOfType(this, EdlTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getAngleLeft() {
    return notNullChild(findChildByType(S_ANGLE_LEFT));
  }

  @Override
  @Nullable
  public PsiElement getAngleRight() {
    return findChildByType(S_ANGLE_RIGHT);
  }

}
