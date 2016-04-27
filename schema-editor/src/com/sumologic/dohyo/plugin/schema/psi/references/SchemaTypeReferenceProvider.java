package com.sumologic.dohyo.plugin.schema.psi.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.sumologic.dohyo.plugin.schema.SchemaLanguage;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFqn;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFqnSegment;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFqnTypeRef;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaTypeReferenceProvider extends PsiReferenceProvider {
  @Override
  public boolean acceptsTarget(@NotNull PsiElement target) {
    return super.acceptsTarget(target);
  }

  @NotNull
  @Override
  public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
//    if (!element.getLanguage().is(SchemaLanguage.INSTANCE)) {
//      new java.util.ArrayList<Void>();
//      return PsiReference.EMPTY_ARRAY;
//    }
//
//    if (element instanceof SchemaFqnTypeRef) {
//      SchemaFqnTypeRef schemaFqnTypeRef = (SchemaFqnTypeRef) element;
//      SchemaFqn fqn = schemaFqnTypeRef.getFqn();
//      SchemaFqnSegment lastSegment = fqn.getLastSegment();
//      if (lastSegment != null) {
//        return new PsiReference[]{new SchemaTypeReference(lastSegment, schemaFqnTypeRef.getName(), extRange(), null)};
//      }
//    }

    return PsiReference.EMPTY_ARRAY;
  }
}
