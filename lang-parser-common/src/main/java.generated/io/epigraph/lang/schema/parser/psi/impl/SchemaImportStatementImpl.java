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

public class SchemaImportStatementImpl extends ASTWrapperPsiElement implements SchemaImportStatement {

  public SchemaImportStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitImportStatement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaFqn getFqn() {
    return PsiTreeUtil.getChildOfType(this, SchemaFqn.class);
  }

  @Override
  @NotNull
  public PsiElement getImport() {
    return notNullChild(findChildByType(E_IMPORT));
  }

}
