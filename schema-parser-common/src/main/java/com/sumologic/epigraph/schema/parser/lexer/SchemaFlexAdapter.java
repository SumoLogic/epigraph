package com.sumologic.epigraph.schema.parser.lexer;

import com.intellij.lexer.FlexAdapter;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFlexAdapter extends FlexAdapter {
  private SchemaFlexAdapter(SchemaLexer flex) {
    super(flex);
  }

  public static SchemaFlexAdapter newInstance() {
    return new SchemaFlexAdapter(new SchemaLexer());
  }
}
