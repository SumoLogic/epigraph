package com.sumologic.dohyo.plugin.schema.psi.references;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.sumologic.dohyo.plugin.schema.SchemaLanguage;
import com.sumologic.dohyo.plugin.schema.psi.SchemaDefs;
import com.sumologic.dohyo.plugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaTypeReferenceContributor extends PsiReferenceContributor {
  @Override
  public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
    PsiElementPattern.Capture<SchemaTypeDef> typeDefCapture =
        PlatformPatterns.psiElement(SchemaTypeDef.class)
            .withParent(SchemaDefs.class)
            .withLanguage(SchemaLanguage.INSTANCE);

    registrar.registerReferenceProvider(typeDefCapture, new SchemaTypeReferenceProvider());
  }
}
