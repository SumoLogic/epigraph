package ws.epigraph.schema.parser;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import ws.epigraph.schema.lexer.SchemaLexer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ws.epigraph.schema.lexer.SchemaElementTypes.*;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class LexerTest {
  @Test
  public void testId() throws IOException {
    testInput("id", S_ID);
//    testInput("_record", S_ID);
    testInput("Foo", S_ID);
  }

  @Test
  public void testKeywords() throws IOException {
    testInput("record", S_RECORD);
    testInput("record { record } record",
        S_RECORD, S_CURLY_LEFT, S_RECORD, S_CURLY_RIGHT, S_RECORD);
  }

  @Test
  public void testBlockComment() throws IOException {
    testInput("id /* foo */ id", S_ID, S_BLOCK_COMMENT, S_ID);
    testInput("/* foo ", S_BLOCK_COMMENT);
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
        S_NAMESPACE, S_ID, S_DOT, S_ID,
        S_INTEGER_T, S_ID,
        S_RECORD, S_ID, S_EXTENDS, S_ID, S_DOT, S_ID, S_CURLY_LEFT,
        S_ID, S_EQ, S_PAREN_LEFT, S_STRING, S_COLON, S_NUMBER, S_COMMA, S_STRING, S_COLON, S_NULL, S_PAREN_RIGHT,
        S_ID, S_EQ, S_PAREN_LEFT, S_ID, S_DOT, S_ID, S_SLASH, S_ID, S_COLON, S_ID, S_PAREN_RIGHT,
        S_ID, S_COLON, S_ID,
        S_CURLY_RIGHT,
        S_VARTYPE, S_ID, S_CURLY_LEFT,
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
        Assert.assertTrue("Got zero-length token '" + e + "' at " + lex.getTokenEnd(), lex.yylength() > 0);
        debug.append(e).append("\n");
        actual.add(e);
      }
      e = lex.advance();
    }

    Assert.assertArrayEquals(debug.toString(), actual.toArray(), expected);
  }
}
