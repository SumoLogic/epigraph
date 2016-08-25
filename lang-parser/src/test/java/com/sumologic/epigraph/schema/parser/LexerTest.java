package com.sumologic.epigraph.schema.parser;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.sumologic.epigraph.schema.parser.lexer.SchemaLexer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.epigraph.lang.lexer.EpigraphElementTypes.*;


/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class LexerTest {
  @Test
  public void testId() throws IOException {
    testInput("id", E_ID);
//    testInput("_record", E_ID);
    testInput("Foo", E_ID);
  }

  @Test
  public void testKeywords() throws IOException {
    testInput("record", E_RECORD);
    testInput("record { record } record",
        E_RECORD, E_CURLY_LEFT, E_RECORD, E_CURLY_RIGHT, E_RECORD);
  }

  @Test
  public void testBlockComment() throws IOException {
    testInput("id /* foo */ id", E_ID, E_BLOCK_COMMENT, E_ID);
    testInput("/* foo ", E_BLOCK_COMMENT);
  }

  @Test
  public void testInput1() throws IOException {
    String input = "" +
        "namespace foo.bar\n" +
        "integer Ыва\n" +
        "record MyType2 extends foo.Type {\n" +
        "  prop = ( \"str1\" : 11.22, \"str\\\"2\" : null )\n" +
        "  prop2 = ( a.b/c:d )\n" +
        "  `field`: Integer\n" +
        "}\n" +
        "vartype MyType3 {\n" +
        "  `record`: MyType2\n" +
        "}";

    IElementType[] expected = {
        E_NAMESPACE, E_ID, E_DOT, E_ID,
        E_INTEGER_T, E_ID,
        E_RECORD, E_ID, E_EXTENDS, E_ID, E_DOT, E_ID, E_CURLY_LEFT,
        E_ID, E_EQ, E_PAREN_LEFT, E_STRING, E_COLON, E_NUMBER, E_COMMA, E_STRING, E_COLON, E_NULL, E_PAREN_RIGHT,
        E_ID, E_EQ, E_PAREN_LEFT, E_ID, E_DOT, E_ID, E_SLASH, E_ID, E_COLON, E_ID, E_PAREN_RIGHT,
        E_ID, E_COLON, E_ID,
        E_CURLY_RIGHT,
        E_VARTYPE, E_ID, E_CURLY_LEFT,
        E_ID, E_COLON, E_ID,
        E_CURLY_RIGHT
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
        Assert.assertTrue("Got zero-length token '" + e + "' at " + lex.getTokenEnd(), lex.yylength() > 0);
        debug.append(e).append("\n");
        actual.add(e);
      }
      e = lex.advance();
    }

    Assert.assertArrayEquals(debug.toString(), actual.toArray(), expected);
  }
}
