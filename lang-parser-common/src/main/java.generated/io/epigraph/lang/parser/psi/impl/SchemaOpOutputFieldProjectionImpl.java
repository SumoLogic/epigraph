// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.lang.parser.psi.*;

public class SchemaOpOutputFieldProjectionImpl extends ASTWrapperPsiElement implements SchemaOpOutputFieldProjection {

  public SchemaOpOutputFieldProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpOutputFieldProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpOutputFieldProjectionBody getOpOutputFieldProjectionBody() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpOutputFieldProjectionBody.class);
  }

  @Override
  @Nullable
  public SchemaOpOutputVarProjection getOpOutputVarProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpOutputVarProjection.class);
  }

  @Override
  @NotNull
  public SchemaQid getQid() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaQid.class));
  }

  @Override
  @Nullable
  public PsiElement getColon() {
    return findChildByType(E_COLON);
  }

}
