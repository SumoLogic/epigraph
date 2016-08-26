// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.lang.schema.parser.psi.*;

public class SchemaOpOutputKeyProjectionPartImpl extends ASTWrapperPsiElement implements SchemaOpOutputKeyProjectionPart {

  public SchemaOpOutputKeyProjectionPartImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpOutputKeyProjectionPart(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaCustomParam getCustomParam() {
    return PsiTreeUtil.getChildOfType(this, SchemaCustomParam.class);
  }

  @Override
  @Nullable
  public SchemaOpParameters getOpParameters() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpParameters.class);
  }

}
