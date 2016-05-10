package com.sumologic.epigraph.ideaplugin.schema.features;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaFlexAdapter;
import com.sumologic.epigraph.ideaplugin.schema.parser.SchemaParserDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaNamesValidator implements NamesValidator {
  private SchemaFlexAdapter lexer = SchemaFlexAdapter.newInstance();

  @Override
  public boolean isKeyword(@NotNull String name, Project project) {
    lexer.start(name);
    return SchemaParserDefinition.KEYWORDS.contains(lexer.getTokenType()) && lexer.getTokenEnd() == name.length();
  }

  @Override
  public boolean isIdentifier(@NotNull String name, Project project) {
    lexer.start(name);
    return SchemaParserDefinition.IDENTIFIERS.contains(lexer.getTokenType()) && lexer.getTokenEnd() == name.length();
  }
}
