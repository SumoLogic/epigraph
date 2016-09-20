// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.schema.lexer.SchemaElementTypes.*;
import io.epigraph.schema.parser.psi.*;

public class SchemaNullDatumImpl extends SchemaDatumImpl implements SchemaNullDatum {

  public SchemaNullDatumImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitNullDatum(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaFqnTypeRef getFqnTypeRef() {
    return PsiTreeUtil.getChildOfType(this, SchemaFqnTypeRef.class);
  }

  @Override
  @Nullable
  public PsiElement getAt() {
    return findChildByType(S_AT);
  }

  @Override
  @NotNull
  public PsiElement getNull() {
    return notNullChild(findChildByType(S_NULL));
  }

}
