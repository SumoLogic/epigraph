package com.sumologic.dohyo.plugin.schema.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface TypeDefSchemaElement extends PsiElement {
  PsiElement getId();
}
