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

public class SchemaOpOutputListModelProjectionImpl extends ASTWrapperPsiElement implements SchemaOpOutputListModelProjection {

  public SchemaOpOutputListModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpOutputListModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaOpOutputListPolyBranch> getOpOutputListPolyBranchList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpOutputListPolyBranch.class);
  }

  @Override
  @NotNull
  public SchemaOpOutputVarProjection getOpOutputVarProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpOutputVarProjection.class));
  }

}
