package com.sumologic.dohyo.plugin.schema.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.FlexLexer;
import org.jetbrains.annotations.NotNull;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFlexAdapter extends FlexAdapter {
  private final SchemaLexer flex;

  public SchemaFlexAdapter(SchemaLexer flex) {
    super(flex);
    this.flex = flex;
  }

  @Override
  public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
    flex.curlyCount = curlyCount(initialState);
    super.start(buffer, startOffset, endOffset, yyState(initialState));
  }

  @Override
  public int getState() {
    return adapterState(super.getState(), flex.curlyCount);
  }

  private int yyState(int adapterState) {
    return adapterState & 0xff;
  }

  private int curlyCount(int adapterState) {
    return adapterState >> 8;
  }

  private int adapterState(int yyState, int curlyCount) {
    assert yyState < 255;
    return (curlyCount << 8) | yyState;
  }
}
