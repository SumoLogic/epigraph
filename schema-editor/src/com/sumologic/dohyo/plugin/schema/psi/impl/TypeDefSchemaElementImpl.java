package com.sumologic.dohyo.plugin.schema.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.sumologic.dohyo.plugin.schema.psi.TypeDefSchemaElement;
import org.jetbrains.annotations.NotNull;

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

}
