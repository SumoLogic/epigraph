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

public class SchemaAnonListImpl extends SchemaTypeRefImpl implements SchemaAnonList {

  public SchemaAnonListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitAnonList(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
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
  @NotNull
  public PsiElement getList() {
    return notNullChild(findChildByType(E_LIST));
  }

}
