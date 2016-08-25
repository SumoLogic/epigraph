// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import io.epigraph.lang.schema.parser.psi.*;

public class SchemaAnonMapImpl extends SchemaTypeRefImpl implements SchemaAnonMap {

  public SchemaAnonMapImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitAnonMap(this);
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
  public SchemaValueTypeRef getValueTypeRef() {
    return PsiTreeUtil.getChildOfType(this, SchemaValueTypeRef.class);
  }

  @Override
  @Nullable
  public PsiElement getBracketLeft() {
    return findChildByType(E_BRACKET_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getBracketRight() {
    return findChildByType(E_BRACKET_RIGHT);
  }

  @Override
  @Nullable
  public PsiElement getComma() {
    return findChildByType(E_COMMA);
  }

  @Override
  @NotNull
  public PsiElement getMap() {
    return notNullChild(findChildByType(E_MAP));
  }

}
