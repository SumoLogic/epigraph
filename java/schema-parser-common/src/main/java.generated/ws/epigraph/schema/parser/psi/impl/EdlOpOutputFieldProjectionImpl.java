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

public class EdlOpOutputFieldProjectionImpl extends ASTWrapperPsiElement implements EdlOpOutputFieldProjection {

  public EdlOpOutputFieldProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull EdlVisitor visitor) {
    visitor.visitOpOutputFieldProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof EdlVisitor) accept((EdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<EdlOpOutputFieldProjectionBodyPart> getOpOutputFieldProjectionBodyPartList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, EdlOpOutputFieldProjectionBodyPart.class);
  }

  @Override
  @NotNull
  public EdlOpOutputVarProjection getOpOutputVarProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, EdlOpOutputVarProjection.class));
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
