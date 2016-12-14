// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.edl.lexer.SchemaElementTypes.*;
import ws.epigraph.edl.parser.psi.*;

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
  public SchemaTypeRef getTypeRef() {
    return PsiTreeUtil.getChildOfType(this, SchemaTypeRef.class);
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
