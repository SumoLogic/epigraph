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

public class SchemaDataMapEntryImpl extends ASTWrapperPsiElement implements SchemaDataMapEntry {

  public SchemaDataMapEntryImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitDataMapEntry(this);
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
  @NotNull
  public List<SchemaDataValue> getDataValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaDataValue.class);
  }

  @Override
  @NotNull
  public List<SchemaFqnTypeRef> getFqnTypeRefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaFqnTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return notNullChild(findChildByType(E_COLON));
  }

  @Override
  @Nullable
  public PsiElement getComma() {
    return findChildByType(E_COMMA);
  }

  @Override
  @Nullable
  public PsiElement getNull() {
    return findChildByType(E_NULL);
  }

}
