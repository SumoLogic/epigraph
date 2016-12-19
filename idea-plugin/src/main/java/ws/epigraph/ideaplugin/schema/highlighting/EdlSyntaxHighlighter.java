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

package ws.epigraph.ideaplugin.schema.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.schema.lexer.EdlFlexAdapter;
import ws.epigraph.schema.parser.EdlParserDefinition;

import java.util.HashMap;
import java.util.Map;

import static ws.epigraph.schema.lexer.EdlElementTypes.*;


/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlSyntaxHighlighter extends SyntaxHighlighterBase {
  // TODO provide customizable color scheme?
  // see also http://www.jetbrains.org/intellij/sdk/docs/reference_guide/color_scheme_management.html
  // http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/syntax_highlighting_and_error_highlighting.html (bottom)
  // http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/syntax_highlighter_and_color_settings_page.html

  public static final TextAttributesKey ID = DefaultLanguageHighlighterColors.IDENTIFIER;
  public static final TextAttributesKey COMMA = DefaultLanguageHighlighterColors.COMMA;
  public static final TextAttributesKey PLUS = DefaultLanguageHighlighterColors.OPERATION_SIGN; // ??
  public static final TextAttributesKey LINE_COMMENT = DefaultLanguageHighlighterColors.LINE_COMMENT;
  public static final TextAttributesKey BLOCK_COMMENT = DefaultLanguageHighlighterColors.BLOCK_COMMENT;
  public static final TextAttributesKey CURLY_BR = DefaultLanguageHighlighterColors.BRACES;
  public static final TextAttributesKey BRACKETS = DefaultLanguageHighlighterColors.BRACKETS;
  public static final TextAttributesKey KEYWORDS = DefaultLanguageHighlighterColors.KEYWORD;

  public static final TextAttributesKey FIELD = DefaultLanguageHighlighterColors.INSTANCE_FIELD;
  public static final TextAttributesKey VAR_MEMBER = DefaultLanguageHighlighterColors.INSTANCE_FIELD;

  public static final TextAttributesKey DECL_TYPE_NAME = DefaultLanguageHighlighterColors.CLASS_NAME;
  public static final TextAttributesKey TYPE_REF = DefaultLanguageHighlighterColors.CLASS_REFERENCE;

  public static final TextAttributesKey PARAM_NAME = DefaultLanguageHighlighterColors.METADATA;

  public static final TextAttributesKey STRING_DATA = DefaultLanguageHighlighterColors.STRING;
  public static final TextAttributesKey NUMERIC_DATA = DefaultLanguageHighlighterColors.NUMBER;

  private static final Map<IElementType, TextAttributesKey> keys;


  static {
    VAR_MEMBER.getDefaultAttributes().setEffectType(EffectType.LINE_UNDERSCORE);

    keys = new HashMap<>();
    keys.put(S_ID, ID);
    keys.put(S_COMMA, COMMA);
    keys.put(S_COMMENT, LINE_COMMENT);
    keys.put(S_BLOCK_COMMENT, BLOCK_COMMENT);
    keys.put(S_STRING, STRING_DATA);
    keys.put(S_NUMBER, NUMERIC_DATA);
    add(BRACKETS, S_BRACKET_LEFT, S_BRACKET_RIGHT);
    add(CURLY_BR, EdlParserDefinition.CURLY_BRACES.getTypes());
    add(KEYWORDS, EdlParserDefinition.KEYWORDS.getTypes());
  }

  private static void add(TextAttributesKey key, IElementType... types) {
    for (IElementType type : types) {
      keys.put(type, key);
    }
  }

  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    return EdlFlexAdapter.newInstance();
  }

  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(keys.get(tokenType), EMPTY);
  }
}
