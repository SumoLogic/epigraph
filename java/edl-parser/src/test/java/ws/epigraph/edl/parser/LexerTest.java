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
