package com.sumologic.epigraph.ideaplugin.schema.features.usages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaFlexAdapter;
import com.sumologic.epigraph.ideaplugin.schema.parser.SchemaParserDefinition;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
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
    if (element instanceof SchemaListTypeDef) return "List type";
    if (element instanceof SchemaPrimitiveTypeDef) return "Primitive type";
    if (element instanceof SchemaEnumTypeDef) return "Enum type";
    if (element instanceof SchemaVarTypeDef) return "Var type";
    if (element instanceof SchemaRecordTypeDef) return "Record type";
    if (element instanceof SchemaMapTypeDef) return "Map type";
    if (element instanceof SchemaFqnSegment) return "Namespace";

    return "Unknown getElement: " + element;
  }

  @NotNull
  @Override
  public String getDescriptiveName(@NotNull PsiElement element) {
    if (element instanceof SchemaTypeDef) {
      SchemaTypeDef typeDef = (SchemaTypeDef) element;
      String name = SchemaPresentationUtil.getName(typeDef, true);
      return name == null ? "" : name;
    }

    if (element instanceof SchemaFqnSegment) {
      SchemaFqnSegment fqnSegment = (SchemaFqnSegment) element;
      return fqnSegment.getFqn().toString();
    }

    return "Unknown getElement: " + element;
  }

  @NotNull
  @Override
  public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
    if (useFullName) return getDescriptiveName(element);

    if (element instanceof SchemaTypeDef) {
      SchemaTypeDef schemaTypeDef = (SchemaTypeDef) element;
      String name = schemaTypeDef.getName();
      if (name != null) return name;
    }

    if (element instanceof SchemaFqnSegment) {
      SchemaFqnSegment fqnSegment = (SchemaFqnSegment) element;
      String name = fqnSegment.getName();
      if (name != null) return name;
    }

    return "";
  }
}
