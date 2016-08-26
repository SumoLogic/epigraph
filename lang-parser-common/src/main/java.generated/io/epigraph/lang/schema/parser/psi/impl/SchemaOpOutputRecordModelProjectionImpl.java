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

public class SchemaOpOutputRecordModelProjectionImpl extends ASTWrapperPsiElement implements SchemaOpOutputRecordModelProjection {

  public SchemaOpOutputRecordModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpOutputRecordModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaOpOutputFieldProjection> getOpOutputFieldProjectionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpOutputFieldProjection.class);
  }

  @Override
  @NotNull
  public List<SchemaOpOutputRecordPolyBranch> getOpOutputRecordPolyBranchList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpOutputRecordPolyBranch.class);
  }

  @Override
  @NotNull
  public PsiElement getParenLeft() {
    return notNullChild(findChildByType(E_PAREN_LEFT));
  }

  @Override
  @NotNull
  public PsiElement getParenRight() {
    return notNullChild(findChildByType(E_PAREN_RIGHT));
  }

}
