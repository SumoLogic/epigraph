package com.sumologic.dohyo.plugin.schema.psi.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.sumologic.dohyo.plugin.schema.SchemaLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaTypeReferenceProvider extends PsiReferenceProvider {
  @NotNull
  @Override
  public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    if (!element.getLanguage().is(SchemaLanguage.INSTANCE)) {
      return PsiReference.EMPTY_ARRAY;
    }

    if (element instanceof PsiNamedElement) {
      PsiNamedElement namedElement = (PsiNamedElement) element;
      return new PsiReference[]{new SchemaTypeReference(namedElement, namedElement.getTextRange(), null)}; // TODO infer kind
    }

    return PsiReference.EMPTY_ARRAY;
  }
}
