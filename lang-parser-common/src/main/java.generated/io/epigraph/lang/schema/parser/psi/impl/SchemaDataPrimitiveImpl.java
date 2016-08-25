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

public class SchemaDataPrimitiveImpl extends ASTWrapperPsiElement implements SchemaDataPrimitive {

  public SchemaDataPrimitiveImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitDataPrimitive(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getBoolean() {
    return findChildByType(E_BOOLEAN);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(E_NUMBER);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(E_STRING);
  }

}
