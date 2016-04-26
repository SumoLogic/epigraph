package com.sumologic.dohyo.plugin.schema.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface TypeDefSchemaElement extends PsiNamedElement {
  PsiElement getId();
}
