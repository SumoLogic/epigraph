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

public class EdlMapDatumEntryImpl extends ASTWrapperPsiElement implements EdlMapDatumEntry {

  public EdlMapDatumEntryImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitMapDatumEntry(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public EdlDataValue getDataValue() {
    return PsiTreeUtil.getChildOfType(this, EdlDataValue.class);
  }

  @Override
  @NotNull
  public EdlDatum getDatum() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlDatum.class));
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return notNullChild(findChildByType(E_COLON));
  }

  @Override
  @Nullable
  public PsiElement getComma() {
    return findChildByType(E_COMMA);
  }

}
