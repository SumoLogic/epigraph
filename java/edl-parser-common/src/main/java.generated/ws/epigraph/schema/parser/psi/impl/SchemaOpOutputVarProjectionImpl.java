// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.schema.parser.psi.*;

public class SchemaOpOutputVarProjectionImpl extends ASTWrapperPsiElement implements SchemaOpOutputVarProjection {

  public SchemaOpOutputVarProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpOutputVarProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpOutputMultiTagProjection getOpOutputMultiTagProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpOutputMultiTagProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpOutputSingleTagProjection getOpOutputSingleTagProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpOutputSingleTagProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpOutputVarPolymorphicTail getOpOutputVarPolymorphicTail() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpOutputVarPolymorphicTail.class);
  }

}
