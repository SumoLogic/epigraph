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

public class SchemaReadOperationBodyPartImpl extends ASTWrapperPsiElement implements SchemaReadOperationBodyPart {

  public SchemaReadOperationBodyPartImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitReadOperationBodyPart(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaAnnotation getAnnotation() {
    return PsiTreeUtil.getChildOfType(this, SchemaAnnotation.class);
  }

  @Override
  @Nullable
  public SchemaOperationOutputProjection getOperationOutputProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOperationOutputProjection.class);
  }

  @Override
  @Nullable
  public SchemaOperationPath getOperationPath() {
    return PsiTreeUtil.getChildOfType(this, SchemaOperationPath.class);
  }

}
