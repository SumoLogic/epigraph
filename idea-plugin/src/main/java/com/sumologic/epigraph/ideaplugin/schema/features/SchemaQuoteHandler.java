package com.sumologic.epigraph.ideaplugin.schema.features;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;

import static io.epigraph.lang.lexer.EpigraphElementTypes.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaQuoteHandler extends SimpleTokenSetQuoteHandler {
  public SchemaQuoteHandler() {
    super(E_ID, E_STRING, E_NUMBER, E_NULL);
  }
  // TODO more elaborate implementation. Only insert `` around IDs and "" around strings
  // see TypedHandler:441

  // see TypedHandlerDelegate, we will need one to insert matching () {} <> in data
}
