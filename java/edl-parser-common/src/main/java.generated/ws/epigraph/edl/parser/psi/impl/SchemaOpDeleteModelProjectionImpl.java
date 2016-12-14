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

public class SchemaOpDeleteModelProjectionImpl extends ASTWrapperPsiElement implements SchemaOpDeleteModelProjection {

  public SchemaOpDeleteModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpDeleteModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpDeleteListModelProjection getOpDeleteListModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpDeleteListModelProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpDeleteMapModelProjection getOpDeleteMapModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpDeleteMapModelProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpDeleteRecordModelProjection getOpDeleteRecordModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpDeleteRecordModelProjection.class);
  }

}
