package com.sumologic.epigraph.ideaPlugin.schema.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface TypeDefSchemaElement extends PsiNameIdentifierOwner {
  PsiElement getId();
}
