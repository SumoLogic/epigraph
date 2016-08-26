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

public class SchemaOpOutputMapPolyBranchImpl extends ASTWrapperPsiElement implements SchemaOpOutputMapPolyBranch {

  public SchemaOpOutputMapPolyBranchImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpOutputMapPolyBranch(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SchemaFqnTypeRef getFqnTypeRef() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaFqnTypeRef.class));
  }

  @Override
  @NotNull
  public SchemaOpOutputMapModelProjection getOpOutputMapModelProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpOutputMapModelProjection.class));
  }

  @Override
  @NotNull
  public PsiElement getAngleLeft() {
    return notNullChild(findChildByType(E_ANGLE_LEFT));
  }

  @Override
  @NotNull
  public PsiElement getAngleRight() {
    return notNullChild(findChildByType(E_ANGLE_RIGHT));
  }

}
