// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.SchemaElementTypes.*;
import ws.epigraph.schema.parser.psi.*;

public class SchemaPrimitiveDatumImpl extends SchemaDatumImpl implements SchemaPrimitiveDatum {

  public SchemaPrimitiveDatumImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitPrimitiveDatum(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaTypeRef getTypeRef() {
    return PsiTreeUtil.getChildOfType(this, SchemaTypeRef.class);
  }

  @Override
  @Nullable
  public PsiElement getAt() {
    return findChildByType(S_AT);
  }

  @Override
  @Nullable
  public PsiElement getBoolean() {
    return findChildByType(S_BOOLEAN);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(S_NUMBER);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(S_STRING);
  }

}
