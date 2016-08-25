package io.epigraph.lang.lexer;

import com.intellij.lexer.FlexAdapter;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphFlexAdapter extends FlexAdapter {
  private EpigraphFlexAdapter(EpigraphLexer flex) {
    super(flex);
  }

  public static EpigraphFlexAdapter newInstance() {
    return new EpigraphFlexAdapter(new EpigraphLexer());
  }
}
