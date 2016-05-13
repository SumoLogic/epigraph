package com.sumologic.epigraph.ideaplugin.schema.features;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;

import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.S_ID;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaQuoteHandler extends SimpleTokenSetQuoteHandler {
  public SchemaQuoteHandler() {
    super(S_ID);
  }
  // TODO more elaborate implementation. Only insert `` around IDs and "" around strings
  // see TypedHandler:441
}
