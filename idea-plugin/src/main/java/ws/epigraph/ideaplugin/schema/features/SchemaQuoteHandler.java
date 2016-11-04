package ws.epigraph.ideaplugin.schema.features;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;

import static io.epigraph.schema.lexer.SchemaElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class SchemaQuoteHandler extends SimpleTokenSetQuoteHandler {
  public SchemaQuoteHandler() {
    super(S_ID, S_STRING, S_NUMBER, S_NULL);
  }
  // TODO more elaborate implementation. Only insert `` around IDs and "" around strings
  // see TypedHandler:441

  // see TypedHandlerDelegate, we will need one to insert matching () {} <> in data
}
