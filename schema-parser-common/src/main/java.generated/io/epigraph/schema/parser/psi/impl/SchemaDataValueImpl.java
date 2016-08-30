// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.schema.parser.psi.*;

public class SchemaDataValueImpl extends ASTWrapperPsiElement implements SchemaDataValue {

  public SchemaDataValueImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitDataValue(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaDataPrimitive getDataPrimitive() {
    return PsiTreeUtil.getChildOfType(this, SchemaDataPrimitive.class);
  }

  @Override
  @Nullable
  public SchemaDataValue getDataValue() {
    return PsiTreeUtil.getChildOfType(this, SchemaDataValue.class);
  }

  @Override
  @NotNull
  public List<SchemaFqnTypeRef> getFqnTypeRefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaFqnTypeRef.class);
  }

  @Override
  @Nullable
  public PsiElement getNull() {
    return findChildByType(S_NULL);
  }

}
