// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.sumologic.epigraph.ideaplugin.schema.psi.impl.*;

public interface SchemaElementTypes {

  IElementType S_ANON_LIST = new SchemaElementType("S_ANON_LIST");
  IElementType S_ANON_MAP = new SchemaElementType("S_ANON_MAP");
  IElementType S_ANON_UNION = new SchemaElementType("S_ANON_UNION");
  IElementType S_COMBINED_FQNS = new SchemaElementType("S_COMBINED_FQNS");
  IElementType S_CUSTOM_PARAM = new SchemaElementType("S_CUSTOM_PARAM");
  IElementType S_DEFAULT_OVERRIDE = new SchemaElementType("S_DEFAULT_OVERRIDE");
  IElementType S_DEFS = new SchemaElementType("S_DEFS");
  IElementType S_ENUM_MEMBER_DECL = new SchemaElementType("S_ENUM_MEMBER_DECL");
  IElementType S_ENUM_TYPE_BODY = new SchemaElementType("S_ENUM_TYPE_BODY");
  IElementType S_ENUM_TYPE_DEF = new SchemaElementType("S_ENUM_TYPE_DEF");
  IElementType S_EXTENDS_DECL = new SchemaElementType("S_EXTENDS_DECL");
  IElementType S_FIELD_DECL = new SchemaElementType("S_FIELD_DECL");
  IElementType S_FQN = new SchemaElementType("S_FQN");
  IElementType S_FQN_SEGMENT = new SchemaElementType("S_FQN_SEGMENT");
  IElementType S_FQN_TYPE_REF = new SchemaElementType("S_FQN_TYPE_REF");
  IElementType S_IMPORTS = new SchemaElementType("S_IMPORTS");
  IElementType S_IMPORT_STATEMENT = new SchemaElementType("S_IMPORT_STATEMENT");
  IElementType S_LIST_TYPE_BODY = new SchemaElementType("S_LIST_TYPE_BODY");
  IElementType S_LIST_TYPE_DEF = new SchemaElementType("S_LIST_TYPE_DEF");
  IElementType S_MAP_TYPE_BODY = new SchemaElementType("S_MAP_TYPE_BODY");
  IElementType S_MAP_TYPE_DEF = new SchemaElementType("S_MAP_TYPE_DEF");
  IElementType S_META_DECL = new SchemaElementType("S_META_DECL");
  IElementType S_NAMESPACE_DECL = new SchemaElementType("S_NAMESPACE_DECL");
  IElementType S_PRIMITIVE_KIND = new SchemaElementType("S_PRIMITIVE_KIND");
  IElementType S_PRIMITIVE_TYPE_BODY = new SchemaElementType("S_PRIMITIVE_TYPE_BODY");
  IElementType S_PRIMITIVE_TYPE_DEF = new SchemaElementType("S_PRIMITIVE_TYPE_DEF");
  IElementType S_RECORD_SUPPLEMENTS_DECL = new SchemaElementType("S_RECORD_SUPPLEMENTS_DECL");
  IElementType S_RECORD_TYPE_BODY = new SchemaElementType("S_RECORD_TYPE_BODY");
  IElementType S_RECORD_TYPE_DEF = new SchemaElementType("S_RECORD_TYPE_DEF");
  IElementType S_STAR_IMPORT_SUFFIX = new SchemaElementType("S_STAR_IMPORT_SUFFIX");
  IElementType S_SUPPLEMENT_DEF = new SchemaElementType("S_SUPPLEMENT_DEF");
  IElementType S_TAG_COMMON_TYPE = new SchemaElementType("S_TAG_COMMON_TYPE");
  IElementType S_TAG_DECL = new SchemaElementType("S_TAG_DECL");
  IElementType S_TYPE_DEF = new SchemaElementType("S_TYPE_DEF");
  IElementType S_TYPE_REF = new SchemaElementType("S_TYPE_REF");
  IElementType S_UNION_TYPE_BODY = new SchemaElementType("S_UNION_TYPE_BODY");
  IElementType S_UNION_TYPE_DEF = new SchemaElementType("S_UNION_TYPE_DEF");
  IElementType S_VAR_TYPE_BODY = new SchemaElementType("S_VAR_TYPE_BODY");
  IElementType S_VAR_TYPE_DEF = new SchemaElementType("S_VAR_TYPE_DEF");
  IElementType S_VAR_TYPE_MEMBER_DECL = new SchemaElementType("S_VAR_TYPE_MEMBER_DECL");
  IElementType S_VAR_TYPE_SUPPLEMENTS_DECL = new SchemaElementType("S_VAR_TYPE_SUPPLEMENTS_DECL");

  IElementType S_BACKTICK = new SchemaElementType("`");
  IElementType S_BLOCK_COMMENT = new SchemaElementType("block_comment");
  IElementType S_BOOLEAN_T = new SchemaElementType("boolean");
  IElementType S_BRACKET_LEFT = new SchemaElementType("[");
  IElementType S_BRACKET_RIGHT = new SchemaElementType("]");
  IElementType S_COLON = new SchemaElementType(":");
  IElementType S_COMMA = new SchemaElementType(",");
  IElementType S_COMMENT = new SchemaElementType("comment");
  IElementType S_CURLY_LEFT = new SchemaElementType("{");
  IElementType S_CURLY_RIGHT = new SchemaElementType("}");
  IElementType S_DATA_VALUE = new SchemaElementType("data_value");
  IElementType S_DEFAULT = new SchemaElementType("default");
  IElementType S_DOT = new SchemaElementType(".");
  IElementType S_DOUBLE_T = new SchemaElementType("double");
  IElementType S_ENUM = new SchemaElementType("enum");
  IElementType S_EQ = new SchemaElementType("=");
  IElementType S_EXTENDS = new SchemaElementType("extends");
  IElementType S_ID = new SchemaElementType("id");
  IElementType S_IMPORT = new SchemaElementType("import");
  IElementType S_INTEGER_T = new SchemaElementType("integer");
  IElementType S_LIST = new SchemaElementType("list");
  IElementType S_LONG_T = new SchemaElementType("long");
  IElementType S_MAP = new SchemaElementType("map");
  IElementType S_META = new SchemaElementType("meta");
  IElementType S_NAMESPACE = new SchemaElementType("namespace");
  IElementType S_NODEFAULT = new SchemaElementType("nodefault");
  IElementType S_PLUS = new SchemaElementType("+");
  IElementType S_RECORD = new SchemaElementType("record");
  IElementType S_SEMI_COLON = new SchemaElementType(";");
  IElementType S_STAR = new SchemaElementType("*");
  IElementType S_STRING_T = new SchemaElementType("string");
  IElementType S_SUPPLEMENT = new SchemaElementType("supplement");
  IElementType S_SUPPLEMENTS = new SchemaElementType("supplements");
  IElementType S_UNION = new SchemaElementType("union");
  IElementType S_VARTYPE = new SchemaElementType("vartype");
  IElementType S_WITH = new SchemaElementType("with");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == S_ANON_LIST) {
        return new SchemaAnonListImpl(node);
      }
      else if (type == S_ANON_MAP) {
        return new SchemaAnonMapImpl(node);
      }
      else if (type == S_ANON_UNION) {
        return new SchemaAnonUnionImpl(node);
      }
      else if (type == S_COMBINED_FQNS) {
        return new SchemaCombinedFqnsImpl(node);
      }
      else if (type == S_CUSTOM_PARAM) {
        return new SchemaCustomParamImpl(node);
      }
      else if (type == S_DEFAULT_OVERRIDE) {
        return new SchemaDefaultOverrideImpl(node);
      }
      else if (type == S_DEFS) {
        return new SchemaDefsImpl(node);
      }
      else if (type == S_ENUM_MEMBER_DECL) {
        return new SchemaEnumMemberDeclImpl(node);
      }
      else if (type == S_ENUM_TYPE_BODY) {
        return new SchemaEnumTypeBodyImpl(node);
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
      else if (type == S_FQN_SEGMENT) {
        return new SchemaFqnSegmentImpl(node);
      }
      else if (type == S_FQN_TYPE_REF) {
        return new SchemaFqnTypeRefImpl(node);
      }
      else if (type == S_IMPORTS) {
        return new SchemaImportsImpl(node);
      }
      else if (type == S_IMPORT_STATEMENT) {
        return new SchemaImportStatementImpl(node);
      }
      else if (type == S_LIST_TYPE_BODY) {
        return new SchemaListTypeBodyImpl(node);
      }
      else if (type == S_LIST_TYPE_DEF) {
        return new SchemaListTypeDefImpl(node);
      }
      else if (type == S_MAP_TYPE_BODY) {
        return new SchemaMapTypeBodyImpl(node);
      }
      else if (type == S_MAP_TYPE_DEF) {
        return new SchemaMapTypeDefImpl(node);
      }
      else if (type == S_META_DECL) {
        return new SchemaMetaDeclImpl(node);
      }
      else if (type == S_NAMESPACE_DECL) {
        return new SchemaNamespaceDeclImpl(node);
      }
      else if (type == S_PRIMITIVE_KIND) {
        return new SchemaPrimitiveKindImpl(node);
      }
      else if (type == S_PRIMITIVE_TYPE_BODY) {
        return new SchemaPrimitiveTypeBodyImpl(node);
      }
      else if (type == S_PRIMITIVE_TYPE_DEF) {
        return new SchemaPrimitiveTypeDefImpl(node);
      }
      else if (type == S_RECORD_SUPPLEMENTS_DECL) {
        return new SchemaRecordSupplementsDeclImpl(node);
      }
      else if (type == S_RECORD_TYPE_BODY) {
        return new SchemaRecordTypeBodyImpl(node);
      }
      else if (type == S_RECORD_TYPE_DEF) {
        return new SchemaRecordTypeDefImpl(node);
      }
      else if (type == S_STAR_IMPORT_SUFFIX) {
        return new SchemaStarImportSuffixImpl(node);
      }
      else if (type == S_SUPPLEMENT_DEF) {
        return new SchemaSupplementDefImpl(node);
      }
      else if (type == S_TAG_COMMON_TYPE) {
        return new SchemaTagCommonTypeImpl(node);
      }
      else if (type == S_TAG_DECL) {
        return new SchemaTagDeclImpl(node);
      }
      else if (type == S_TYPE_DEF) {
        return new SchemaTypeDefImpl(node);
      }
      else if (type == S_TYPE_REF) {
        return new SchemaTypeRefImpl(node);
      }
      else if (type == S_UNION_TYPE_BODY) {
        return new SchemaUnionTypeBodyImpl(node);
      }
      else if (type == S_UNION_TYPE_DEF) {
        return new SchemaUnionTypeDefImpl(node);
      }
      else if (type == S_VAR_TYPE_BODY) {
        return new SchemaVarTypeBodyImpl(node);
      }
      else if (type == S_VAR_TYPE_DEF) {
        return new SchemaVarTypeDefImpl(node);
      }
      else if (type == S_VAR_TYPE_MEMBER_DECL) {
        return new SchemaVarTypeMemberDeclImpl(node);
      }
      else if (type == S_VAR_TYPE_SUPPLEMENTS_DECL) {
        return new SchemaVarTypeSupplementsDeclImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
