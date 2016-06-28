package com.sumologic.epigraph.ideaplugin.schema.features;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import com.sumologic.epigraph.schema.parser.SchemaParserDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaNamesValidator implements NamesValidator {
  @Override
  public boolean isKeyword(@NotNull String name, Project project) {
    return SchemaParserDefinition.isKeyword(name);
  }

  @Override
  public boolean isIdentifier(@NotNull String name, Project project) {
    return SchemaParserDefinition.isIdentifier(name);
  }
}
