package com.sumologic.dohyo.plugin.schema.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.*;
import static org.testng.Assert.assertEquals;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
@Test
public class LexerTest {
  public void testId() throws IOException {
    testInput("id", S_ID);
    testInput("_record", S_ID);
    testInput("Foo", S_ID);
  }

  public void testKeywords() throws IOException {
    testInput("record", S_RECORD);
    testInput("record { record } record",
        S_RECORD, S_CURLY_LEFT, S_ID, S_CURLY_RIGHT, S_RECORD);
  }

  public void testBlockComment() throws IOException {
    testInput("id /* foo */ id", S_ID, S_BLOCK_COMMENT, S_ID);
    testInput("/* foo ", S_BLOCK_COMMENT);
  }

  public void testInput1() throws IOException {
    String input = "" +
        "namespace foo.bar\n" +
        "integer Ыва\n" +
        "record MyType2 extends foo.Type {\n" +
        "  field: Integer\n" +
        "}\n" +
        "multi MyType3 {\n" +
        "  record: MyType2\n" +
        "}";

    IElementType[] expected = {
        S_NAMESPACE, S_ID, S_DOT, S_ID,
        S_INTEGER_T, S_ID,
        S_RECORD, S_ID, S_EXTENDS, S_ID, S_DOT, S_ID, S_CURLY_LEFT,
        S_ID, S_COLON, S_ID,
        S_CURLY_RIGHT,
        S_MULTI, S_ID, S_CURLY_LEFT,
        S_ID, S_COLON, S_ID,
        S_CURLY_RIGHT
    };

    testInput(input, expected);
  }

  private void testInput(String input, IElementType... expected) throws IOException {
    SchemaLexer lex = new SchemaLexer();
    lex.reset(input, 0, input.length(), SchemaLexer.YYINITIAL);

    List<IElementType> actual = new ArrayList<>();

    StringBuilder debug = new StringBuilder();
    IElementType e = lex.advance();
    while (e != null) {
      if (e != TokenType.WHITE_SPACE) {
        debug.append(e).append("\n");
        actual.add(e);
      }
      e = lex.advance();
    }

    assertEquals(actual.toArray(), expected, debug.toString());
  }
}
