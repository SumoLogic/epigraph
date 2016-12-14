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

public class SchemaOpInputVarProjectionImpl extends ASTWrapperPsiElement implements SchemaOpInputVarProjection {

  public SchemaOpInputVarProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpInputVarProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpInputMultiTagProjection getOpInputMultiTagProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputMultiTagProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpInputSingleTagProjection getOpInputSingleTagProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputSingleTagProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpInputVarPolymorphicTail getOpInputVarPolymorphicTail() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputVarPolymorphicTail.class);
  }

}
