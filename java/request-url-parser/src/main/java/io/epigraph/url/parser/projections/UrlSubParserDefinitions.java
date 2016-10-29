package io.epigraph.url.parser.projections;

import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import io.epigraph.url.parser.UrlParserDefinition;
import io.epigraph.url.parser.UrlSubParser;
import org.jetbrains.annotations.NotNull;

import static io.epigraph.url.lexer.UrlElementTypes.U_READ_URL;
import static io.epigraph.url.lexer.UrlElementTypes.U_REQ_OUTPUT_TRUNK_VAR_PROJECTION;
import static io.epigraph.url.lexer.UrlElementTypes.U_REQ_VAR_PATH;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlSubParserDefinitions {
  public static final UrlSubParserDefinition READ_URL = new UrlSubParserDefinition(U_READ_URL);

  public static final UrlSubParserDefinition REQ_VAR_PATH = new UrlSubParserDefinition(U_REQ_VAR_PATH);
  public static final UrlSubParserDefinition
      REQ_OUTPUT_VAR_PROJECTION = new UrlSubParserDefinition(U_REQ_OUTPUT_TRUNK_VAR_PROJECTION);

  public static class UrlSubParserDefinition extends UrlParserDefinition {
    @NotNull
    private final IElementType rootElementType;

    public UrlSubParserDefinition(@NotNull IElementType rootElementType) {this.rootElementType = rootElementType;}

    @Override
    public PsiParser createParser(Project project) {
      return new UrlSubParser(rootElementType);
    }

    @NotNull
    public IElementType rootElementType() {
      return rootElementType;
    }
  }
}
