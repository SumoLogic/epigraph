package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaExtendsDecl;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaMetaDecl;
import com.sumologic.epigraph.ideaplugin.schema.psi.TypeDefSchemaElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class TypeDefSchemaElementImpl extends ASTWrapperPsiElement implements TypeDefSchemaElement {
  TypeDefSchemaElementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public PsiElement getId() {
    throw new RuntimeException("Should never happen: " + getClass());
  }

  @Nullable
  public SchemaExtendsDecl getExtendsDecl() {
    return null;
  }

  @Nullable
  @Override
  public SchemaMetaDecl getMetaDecl() {
    return null;
  }
}
