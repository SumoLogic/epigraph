package com.sumologic.dohyo.plugin.schema.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.sumologic.dohyo.plugin.schema.psi.SchemaElementWithId;
import org.jetbrains.annotations.NotNull;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class SchemaElementWithIdImpl extends ASTWrapperPsiElement implements SchemaElementWithId {
  SchemaElementWithIdImpl(@NotNull ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public PsiElement getId() {
    throw new RuntimeException("Should never happen");
  }
}
