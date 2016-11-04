package ws.epigraph.url.parser.projections;

import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import ws.epigraph.url.parser.UrlParserDefinition;
import ws.epigraph.url.parser.UrlSubParser;
import org.jetbrains.annotations.NotNull;

import static ws.epigraph.url.lexer.UrlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlSubParserDefinitions {
  public static final UrlSubParserDefinition READ_URL = new UrlSubParserDefinition(U_READ_URL);
  public static final UrlSubParserDefinition CREATE_URL = new UrlSubParserDefinition(U_CREATE_URL);
  public static final UrlSubParserDefinition UPDATE_URL = new UrlSubParserDefinition(U_UPDATE_URL);
  public static final UrlSubParserDefinition DELETE_URL = new UrlSubParserDefinition(U_DELETE_URL);
  public static final UrlSubParserDefinition CUSTOM_URL = new UrlSubParserDefinition(U_CUSTOM_URL);

  public static final UrlSubParserDefinition REQ_VAR_PATH = new UrlSubParserDefinition(U_REQ_VAR_PATH);
  public static final UrlSubParserDefinition REQ_OUTPUT_VAR_PROJECTION = new UrlSubParserDefinition(U_REQ_OUTPUT_TRUNK_VAR_PROJECTION);

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
