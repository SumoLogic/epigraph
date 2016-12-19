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

public class EdlOpDeleteFieldProjectionImpl extends ASTWrapperPsiElement implements EdlOpDeleteFieldProjection {

  public EdlOpDeleteFieldProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpDeleteFieldProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlOpDeleteFieldProjectionBodyPart> getOpDeleteFieldProjectionBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlOpDeleteFieldProjectionBodyPart.class);
  }

  @Override
  @NotNull
  public EdlOpDeleteVarProjection getOpDeleteVarProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlOpDeleteVarProjection.class));
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(S_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(S_CURLY_RIGHT);
  }

}
