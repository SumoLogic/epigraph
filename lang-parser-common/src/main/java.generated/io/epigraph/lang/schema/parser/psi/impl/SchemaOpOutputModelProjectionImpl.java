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

public class SchemaOpOutputModelProjectionImpl extends ASTWrapperPsiElement implements SchemaOpOutputModelProjection {

  public SchemaOpOutputModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpOutputModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpOutputEnumModelProjection getOpOutputEnumModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpOutputEnumModelProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpOutputListModelProjection getOpOutputListModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpOutputListModelProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpOutputMapModelProjection getOpOutputMapModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpOutputMapModelProjection.class);
  }

  @Override
  @NotNull
  public SchemaOpOutputModelProjectionBody getOpOutputModelProjectionBody() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, SchemaOpOutputModelProjectionBody.class));
  }

  @Override
  @Nullable
  public SchemaOpOutputPrimitiveModelProjection getOpOutputPrimitiveModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpOutputPrimitiveModelProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpOutputRecordModelProjection getOpOutputRecordModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpOutputRecordModelProjection.class);
  }

}
