package ws.epigraph.ideaplugin.schema.features;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import ws.epigraph.schema.parser.SchemaParserDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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
