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

public class SchemaOpOutputMapModelProjectionImpl extends ASTWrapperPsiElement implements SchemaOpOutputMapModelProjection {

  public SchemaOpOutputMapModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpOutputMapModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SchemaOpOutputKeyProjection getOpOutputKeyProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpOutputKeyProjection.class));
  }

  @Override
  @NotNull
  public SchemaOpOutputMapPolyBranch getOpOutputMapPolyBranch() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpOutputMapPolyBranch.class));
  }

  @Override
  @NotNull
  public SchemaOpOutputVarProjection getOpOutputVarProjection() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpOutputVarProjection.class));
  }

}
