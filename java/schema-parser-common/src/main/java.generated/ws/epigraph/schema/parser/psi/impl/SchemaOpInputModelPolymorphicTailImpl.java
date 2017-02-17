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

public class SchemaOpInputModelPolymorphicTailImpl extends ASTWrapperPsiElement implements SchemaOpInputModelPolymorphicTail {

  public SchemaOpInputModelPolymorphicTailImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpInputModelPolymorphicTail(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpInputModelMultiTail getOpInputModelMultiTail() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputModelMultiTail.class);
  }

  @Override
  @Nullable
  public SchemaOpInputModelSingleTail getOpInputModelSingleTail() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputModelSingleTail.class);
  }

}
