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

public class SchemaOpInputModelProjectionImpl extends ASTWrapperPsiElement implements SchemaOpInputModelProjection {

  public SchemaOpInputModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpInputModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpInputListModelProjection getOpInputListModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputListModelProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpInputMapModelProjection getOpInputMapModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputMapModelProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpInputRecordModelProjection getOpInputRecordModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputRecordModelProjection.class);
  }

}
