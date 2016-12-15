/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.edl.parser;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import ws.epigraph.edl.lexer.EdlLexer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ws.epigraph.edl.lexer.EdlElementTypes.*;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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
    EdlLexer lex = new EdlLexer();
    lex.reset(input, 0, input.length(), EdlLexer.YYINITIAL);

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
