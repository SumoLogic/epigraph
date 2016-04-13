package com.sumologic.dohyo.plugin.schema.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaTokenTypesOld {
  IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
  IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

  IElementType IDENTIFIER = new SchemaElementType("IDENTIFIER");

  IElementType STRING_LITERAL = new SchemaElementType("STRING_LITERAL");

  IElementType LINE_COMMENT = new SchemaElementType("LINE_COMMENT");
  IElementType BLOCK_COMMENT = new SchemaElementType("BLOCK_COMMENT");

  IElementType NAMESPACE_KEYWORD = new SchemaElementType("namespace");
  IElementType RECORD_KEYWORD = new SchemaElementType("record");
  IElementType UNION_KEYWORD = new SchemaElementType("union");
  IElementType MAP_KEYWORD = new SchemaElementType("map");
  IElementType LIST_KEYWORD = new SchemaElementType("list");
  IElementType PRIMITIVE_KEYWORD = new SchemaElementType("primitive");

  IElementType INLINE_MAP_KEYWORD = new SchemaElementType("Map");
  IElementType INLINE_LIST_KEYWORD = new SchemaElementType("List");

  IElementType LONG_PRIMITIVE = new SchemaElementType("long");
  IElementType INT_PRIMITIVE = new SchemaElementType("int");
  IElementType STRING_PRIMITIVE = new SchemaElementType("string");
  IElementType BOOL_PRIMITIVE = new SchemaElementType("boolean");
  IElementType DOUBLE_PRIMITIVE = new SchemaElementType("double");

  IElementType COLON = new SchemaElementType(":");
  IElementType COMMA = new SchemaElementType(",");
  IElementType L_BRACKET = new SchemaElementType("[");
  IElementType R_BRACKET = new SchemaElementType("]");
  IElementType L_CURLY = new SchemaElementType("{");
  IElementType R_CURLY = new SchemaElementType("}");

}

