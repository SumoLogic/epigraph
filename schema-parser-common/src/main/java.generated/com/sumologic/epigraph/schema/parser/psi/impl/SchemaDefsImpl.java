// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.sumologic.epigraph.schema.parser.psi.util.SchemaPsiTreeUtil;
import static com.sumologic.epigraph.schema.parser.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.sumologic.epigraph.schema.parser.psi.*;

public class SchemaDefsImpl extends ASTWrapperPsiElement implements SchemaDefs {

  public SchemaDefsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitDefs(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaSupplementDef> getSupplementDefList() {
    return SchemaPsiTreeUtil.getChildrenOfTypeAsList(this, SchemaSupplementDef.class);
  }

  @Override
  @NotNull
  public List<SchemaTypeDefWrapper> getTypeDefWrapperList() {
    return SchemaPsiTreeUtil.getChildrenOfTypeAsList(this, SchemaTypeDefWrapper.class);
  }

}
