// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.sumologic.dohyo.plugin.schema.psi.impl.*;

public interface SchemaElementTypes {

  IElementType S_ANON_LIST = new SchemaElementType("S_ANON_LIST");
  IElementType S_ANON_MAP = new SchemaElementType("S_ANON_MAP");
  IElementType S_CUSTOM_PARAM = new SchemaElementType("S_CUSTOM_PARAM");
  IElementType S_DEFAULT_OVERRIDE = new SchemaElementType("S_DEFAULT_OVERRIDE");
  IElementType S_ENUM_MEMBER = new SchemaElementType("S_ENUM_MEMBER");
  IElementType S_ENUM_TYPE_DEF = new SchemaElementType("S_ENUM_TYPE_DEF");
  IElementType S_EXTENDS_DECL = new SchemaElementType("S_EXTENDS_DECL");
  IElementType S_FIELD_DECL = new SchemaElementType("S_FIELD_DECL");
  IElementType S_FQN = new SchemaElementType("S_FQN");
  IElementType S_LIST_TYPE_DEF = new SchemaElementType("S_LIST_TYPE_DEF");
  IElementType S_MAP_TYPE_DEF = new SchemaElementType("S_MAP_TYPE_DEF");
  IElementType S_MEMBER_DECL = new SchemaElementType("S_MEMBER_DECL");
  IElementType S_MULTI_TYPE_DEF = new SchemaElementType("S_MULTI_TYPE_DEF");
  IElementType S_NAMESPACE_DECL = new SchemaElementType("S_NAMESPACE_DECL");
  IElementType S_NAMESPACE_NAME = new SchemaElementType("S_NAMESPACE_NAME");
  IElementType S_PRIMITIVE_KIND = new SchemaElementType("S_PRIMITIVE_KIND");
  IElementType S_PRIMITIVE_TYPE_DEF = new SchemaElementType("S_PRIMITIVE_TYPE_DEF");
  IElementType S_RECORD_TYPE_DEF = new SchemaElementType("S_RECORD_TYPE_DEF");
  IElementType S_TAG_DECL = new SchemaElementType("S_TAG_DECL");
  IElementType S_TYPE_DEFS = new SchemaElementType("S_TYPE_DEFS");
  IElementType S_TYPE_REF = new SchemaElementType("S_TYPE_REF");
  IElementType S_UNION_TYPE_DEF = new SchemaElementType("S_UNION_TYPE_DEF");

  IElementType S_BLOCK_COMMENT = new SchemaElementType("block_comment");
  IElementType S_BOOLEAN_T = new SchemaElementType("boolean");
  IElementType S_BRACKET_LEFT = new SchemaElementType("[");
  IElementType S_BRACKET_RIGHT = new SchemaElementType("]");
  IElementType S_COLON = new SchemaElementType(":");
  IElementType S_COMMA = new SchemaElementType(",");
  IElementType S_COMMENT = new SchemaElementType("comment");
  IElementType S_CURLY_LEFT = new SchemaElementType("{");
  IElementType S_CURLY_RIGHT = new SchemaElementType("}");
  IElementType S_DEFAULT = new SchemaElementType("default");
  IElementType S_DOT = new SchemaElementType(".");
  IElementType S_DOUBLE_T = new SchemaElementType("double");
  IElementType S_ENUM = new SchemaElementType("enum");
  IElementType S_EQ = new SchemaElementType("=");
  IElementType S_EXTENDS = new SchemaElementType("extends");
  IElementType S_ID = new SchemaElementType("id");
  IElementType S_INTEGER_T = new SchemaElementType("integer");
  IElementType S_LIST = new SchemaElementType("list");
  IElementType S_LONG_T = new SchemaElementType("long");
  IElementType S_MAP = new SchemaElementType("map");
  IElementType S_MULTI = new SchemaElementType("multi");
  IElementType S_NAMESPACE = new SchemaElementType("namespace");
  IElementType S_RECORD = new SchemaElementType("record");
  IElementType S_STRING = new SchemaElementType("string");
  IElementType S_STRING_T = new SchemaElementType("string");
  IElementType S_UNION = new SchemaElementType("union");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == S_ANON_LIST) {
        return new SchemaAnonListImpl(node);
      }
      else if (type == S_ANON_MAP) {
        return new SchemaAnonMapImpl(node);
      }
      else if (type == S_CUSTOM_PARAM) {
        return new SchemaCustomParamImpl(node);
      }
      else if (type == S_DEFAULT_OVERRIDE) {
        return new SchemaDefaultOverrideImpl(node);
      }
      else if (type == S_ENUM_MEMBER) {
        return new SchemaEnumMemberImpl(node);
      }
      else if (type == S_ENUM_TYPE_DEF) {
        return new SchemaEnumTypeDefImpl(node);
      }
      else if (type == S_EXTENDS_DECL) {
        return new SchemaExtendsDeclImpl(node);
      }
      else if (type == S_FIELD_DECL) {
        return new SchemaFieldDeclImpl(node);
      }
      else if (type == S_FQN) {
        return new SchemaFqnImpl(node);
      }
      else if (type == S_LIST_TYPE_DEF) {
        return new SchemaListTypeDefImpl(node);
      }
      else if (type == S_MAP_TYPE_DEF) {
        return new SchemaMapTypeDefImpl(node);
      }
      else if (type == S_MEMBER_DECL) {
        return new SchemaMemberDeclImpl(node);
      }
      else if (type == S_MULTI_TYPE_DEF) {
        return new SchemaMultiTypeDefImpl(node);
      }
      else if (type == S_NAMESPACE_DECL) {
        return new SchemaNamespaceDeclImpl(node);
      }
      else if (type == S_NAMESPACE_NAME) {
        return new SchemaNamespaceNameImpl(node);
      }
      else if (type == S_PRIMITIVE_KIND) {
        return new SchemaPrimitiveKindImpl(node);
      }
      else if (type == S_PRIMITIVE_TYPE_DEF) {
        return new SchemaPrimitiveTypeDefImpl(node);
      }
      else if (type == S_RECORD_TYPE_DEF) {
        return new SchemaRecordTypeDefImpl(node);
      }
      else if (type == S_TAG_DECL) {
        return new SchemaTagDeclImpl(node);
      }
      else if (type == S_TYPE_DEFS) {
        return new SchemaTypeDefsImpl(node);
      }
      else if (type == S_TYPE_REF) {
        return new SchemaTypeRefImpl(node);
      }
      else if (type == S_UNION_TYPE_DEF) {
        return new SchemaUnionTypeDefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
