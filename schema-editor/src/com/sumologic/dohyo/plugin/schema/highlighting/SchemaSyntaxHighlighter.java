package com.sumologic.dohyo.plugin.schema.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.sumologic.dohyo.plugin.schema.lexer.SchemaFlexAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.*;


/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaSyntaxHighlighter extends SyntaxHighlighterBase {
  // TODO provide customizable color scheme?
  // see also http://www.jetbrains.org/intellij/sdk/docs/reference_guide/color_scheme_management.html

  public static final TextAttributesKey ID = DefaultLanguageHighlighterColors.IDENTIFIER;
  public static final TextAttributesKey COMMA = DefaultLanguageHighlighterColors.COMMA;
  public static final TextAttributesKey LINE_COMMENT = DefaultLanguageHighlighterColors.LINE_COMMENT;
  public static final TextAttributesKey BLOCK_COMMENT = DefaultLanguageHighlighterColors.BLOCK_COMMENT;
  public static final TextAttributesKey STRING = DefaultLanguageHighlighterColors.STRING;
//  public static final TextAttributesKey CUSTOM_PARAM = DefaultLanguageHighlighterColors.METADATA; // PARAMETER?
  // public static final TextAttributesKey MEMBER_NAME = DefaultLanguageHighlighterColors.INSTANCE_FIELD;
  //  public static final TextAttributesKey COLON = DefaultLanguageHighlighterColors.
  public static final TextAttributesKey CURLY_BR = DefaultLanguageHighlighterColors.BRACES;
  public static final TextAttributesKey BRACKETS = DefaultLanguageHighlighterColors.BRACKETS;
  public static final TextAttributesKey KEYWORDS = DefaultLanguageHighlighterColors.KEYWORD;

  private static final Map<IElementType, TextAttributesKey> keys;


  static {
    keys = new HashMap<IElementType, TextAttributesKey>();
    keys.put(S_ID, ID);
    keys.put(S_COMMA, COMMA);
    keys.put(S_COMMENT, LINE_COMMENT);
    keys.put(S_BLOCK_COMMENT, BLOCK_COMMENT);
    keys.put(S_STRING, STRING);
//    keys.put(S_CUSTOM_PARAM, CUSTOM_PARAM); // no effect at lexer stage
    add(CURLY_BR, S_CURLY_LEFT, S_CURLY_RIGHT);
    add(BRACKETS, S_BRACKET_LEFT, S_BRACKET_RIGHT);
    add(KEYWORDS, S_NAMESPACE, S_RECORD, S_UNION, S_MULTI, S_MAP, S_LIST, S_ENUM, S_DEFAULT, S_EXTENDS,
        S_INTEGER_T, S_STRING_T, S_BOOLEAN_T, S_DOUBLE_T, S_DOUBLE_T, S_LONG_T);
  }

  private static void add(TextAttributesKey key, IElementType... types) {
    for (IElementType type : types) {
      keys.put(type, key);
    }
  }

  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    return SchemaFlexAdapter.newInstance();
  }

  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(keys.get(tokenType), EMPTY);
  }
}
