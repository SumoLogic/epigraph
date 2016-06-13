package com.sumologic.epigraph.ideaplugin.schema.features.usages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.sumologic.epigraph.schema.parser.lexer.SchemaFlexAdapter;
import com.sumologic.epigraph.schema.parser.SchemaParserDefinition;
import com.sumologic.epigraph.schema.parser.psi.*;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFindUsagesProvider implements FindUsagesProvider {
  @Nullable
  @Override
  public WordsScanner getWordsScanner() {
    return new DefaultWordsScanner(SchemaFlexAdapter.newInstance(),
        SchemaParserDefinition.IDENTIFIERS,
        SchemaParserDefinition.COMMENTS,
        SchemaParserDefinition.LITERALS);
  }

  @Override
  public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
    // TODO Support multi aliases, and later fields used by projections

    if (psiElement instanceof SchemaTypeDef) {
      SchemaTypeDef element = (SchemaTypeDef) psiElement;
      return element.getName() != null;
    }

    if (psiElement instanceof SchemaFqnSegment) {
      SchemaFqnSegment fqnSegment = (SchemaFqnSegment) psiElement;
      return fqnSegment.getName() != null;
    }

    return false;
  }

  @Nullable
  @Override
  public String getHelpId(@NotNull PsiElement psiElement) {
    return null;
  }

  @NotNull
  @Override
  public String getType(@NotNull PsiElement element) {
    return SchemaPresentationUtil.getType(element);
  }

  @NotNull
  @Override
  public String getDescriptiveName(@NotNull PsiElement element) {
    if (element instanceof PsiNamedElement) {
      PsiNamedElement namedElement = (PsiNamedElement) element;
      String name = SchemaPresentationUtil.getName(namedElement, false);
      if (name != null) return name;
    }

    return "Unknown getElement: " + element;
  }

  @NotNull
  @Override
  public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
    if (element instanceof PsiNamedElement) {
      PsiNamedElement namedElement = (PsiNamedElement) element;
      String name = SchemaPresentationUtil.getName(namedElement, useFullName);
      if (name != null) return name;
    }
   
    return "";
  }
}
