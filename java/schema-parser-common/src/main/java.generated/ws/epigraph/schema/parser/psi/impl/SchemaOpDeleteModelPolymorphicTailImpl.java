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

public class SchemaOpDeleteModelPolymorphicTailImpl extends ASTWrapperPsiElement implements SchemaOpDeleteModelPolymorphicTail {

  public SchemaOpDeleteModelPolymorphicTailImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpDeleteModelPolymorphicTail(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpDeleteModelMultiTail getOpDeleteModelMultiTail() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpDeleteModelMultiTail.class);
  }

  @Override
  @Nullable
  public SchemaOpDeleteModelSingleTail getOpDeleteModelSingleTail() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpDeleteModelSingleTail.class);
  }

  @Override
  @NotNull
  public PsiElement getTilda() {
    return notNullChild(findChildByType(S_TILDA));
  }

}
