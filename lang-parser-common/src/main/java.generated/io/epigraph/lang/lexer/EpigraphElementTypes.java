// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.sumologic.epigraph.schema.parser.lexer.SchemaElementType;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaEnumTypeDefStubElementType;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaListTypeDefStubElementType;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaMapTypeDefStubElementType;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaNamespaceDeclStubElementType;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaPrimitiveTypeDefStubElementType;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaRecordTypeDefStubElementType;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaSupplementDefStubElementType;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaTypeDefWrapperStubElementType;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaVarTypeDefStubElementType;
import com.sumologic.epigraph.schema.parser.psi.impl.*;

public interface EpigraphElementTypes {

  IElementType E_ANON_LIST = new SchemaElementType("E_ANON_LIST");
  IElementType E_ANON_MAP = new SchemaElementType("E_ANON_MAP");
  IElementType E_CUSTOM_PARAM = new SchemaElementType("E_CUSTOM_PARAM");
  IElementType E_DATA_ENUM = new SchemaElementType("E_DATA_ENUM");
  IElementType E_DATA_LIST = new SchemaElementType("E_DATA_LIST");
  IElementType E_DATA_MAP = new SchemaElementType("E_DATA_MAP");
  IElementType E_DATA_MAP_ENTRY = new SchemaElementType("E_DATA_MAP_ENTRY");
  IElementType E_DATA_PRIMITIVE = new SchemaElementType("E_DATA_PRIMITIVE");
  IElementType E_DATA_RECORD = new SchemaElementType("E_DATA_RECORD");
  IElementType E_DATA_RECORD_ENTRY = new SchemaElementType("E_DATA_RECORD_ENTRY");
  IElementType E_DATA_VALUE = new SchemaElementType("E_DATA_VALUE");
  IElementType E_DATA_VAR = new SchemaElementType("E_DATA_VAR");
  IElementType E_DATA_VAR_ENTRY = new SchemaElementType("E_DATA_VAR_ENTRY");
  IElementType E_DEFAULT_OVERRIDE = new SchemaElementType("E_DEFAULT_OVERRIDE");
  IElementType E_DEFS = new SchemaElementType("E_DEFS");
  IElementType E_ENUM_MEMBER_DECL = new SchemaElementType("E_ENUM_MEMBER_DECL");
  IElementType E_ENUM_TYPE_BODY = new SchemaElementType("E_ENUM_TYPE_BODY");
  IElementType E_ENUM_TYPE_DEF = new SchemaEnumTypeDefStubElementType("E_ENUM_TYPE_DEF");
  IElementType E_EXTENDS_DECL = new SchemaElementType("E_EXTENDS_DECL");
  IElementType E_FIELD_DECL = new SchemaElementType("E_FIELD_DECL");
  IElementType E_FQN = new SchemaElementType("E_FQN");
  IElementType E_FQN_SEGMENT = new SchemaElementType("E_FQN_SEGMENT");
  IElementType E_FQN_TYPE_REF = new SchemaElementType("E_FQN_TYPE_REF");
  IElementType E_IMPORTS = new SchemaElementType("E_IMPORTS");
  IElementType E_IMPORT_STATEMENT = new SchemaElementType("E_IMPORT_STATEMENT");
  IElementType E_LIST_TYPE_BODY = new SchemaElementType("E_LIST_TYPE_BODY");
  IElementType E_LIST_TYPE_DEF = new SchemaListTypeDefStubElementType("E_LIST_TYPE_DEF");
  IElementType E_MAP_TYPE_BODY = new SchemaElementType("E_MAP_TYPE_BODY");
  IElementType E_MAP_TYPE_DEF = new SchemaMapTypeDefStubElementType("E_MAP_TYPE_DEF");
  IElementType E_META_DECL = new SchemaElementType("E_META_DECL");
  IElementType E_NAMESPACE_DECL = new SchemaNamespaceDeclStubElementType("E_NAMESPACE_DECL");
  IElementType E_PRIMITIVE_TYPE_BODY = new SchemaElementType("E_PRIMITIVE_TYPE_BODY");
  IElementType E_PRIMITIVE_TYPE_DEF = new SchemaPrimitiveTypeDefStubElementType("E_PRIMITIVE_TYPE_DEF");
  IElementType E_QID = new SchemaElementType("E_QID");
  IElementType E_RECORD_TYPE_BODY = new SchemaElementType("E_RECORD_TYPE_BODY");
  IElementType E_RECORD_TYPE_DEF = new SchemaRecordTypeDefStubElementType("E_RECORD_TYPE_DEF");
  IElementType E_SUPPLEMENTS_DECL = new SchemaElementType("E_SUPPLEMENTS_DECL");
  IElementType E_SUPPLEMENT_DEF = new SchemaSupplementDefStubElementType("E_SUPPLEMENT_DEF");
  IElementType E_TYPE_DEF_WRAPPER = new SchemaTypeDefWrapperStubElementType("E_TYPE_DEF_WRAPPER");
  IElementType E_TYPE_REF = new SchemaElementType("E_TYPE_REF");
  IElementType E_VALUE_TYPE_REF = new SchemaElementType("E_VALUE_TYPE_REF");
  IElementType E_VAR_TAG_DECL = new SchemaElementType("E_VAR_TAG_DECL");
  IElementType E_VAR_TAG_REF = new SchemaElementType("E_VAR_TAG_REF");
  IElementType E_VAR_TYPE_BODY = new SchemaElementType("E_VAR_TYPE_BODY");
  IElementType E_VAR_TYPE_DEF = new SchemaVarTypeDefStubElementType("E_VAR_TYPE_DEF");

  IElementType E_ABSTRACT = new SchemaElementType("abstract");
  IElementType E_ANGLE_LEFT = new SchemaElementType("<");
  IElementType E_ANGLE_RIGHT = new SchemaElementType(">");
  IElementType E_BLOCK_COMMENT = new SchemaElementType("block_comment");
  IElementType E_BOOLEAN = new SchemaElementType("boolean");
  IElementType E_BOOLEAN_T = new SchemaElementType("boolean");
  IElementType E_BRACKET_LEFT = new SchemaElementType("[");
  IElementType E_BRACKET_RIGHT = new SchemaElementType("]");
  IElementType E_COLON = new SchemaElementType(":");
  IElementType E_COMMA = new SchemaElementType(",");
  IElementType E_COMMENT = new SchemaElementType("comment");
  IElementType E_CURLY_LEFT = new SchemaElementType("{");
  IElementType E_CURLY_RIGHT = new SchemaElementType("}");
  IElementType E_DEFAULT = new SchemaElementType("default");
  IElementType E_DOT = new SchemaElementType(".");
  IElementType E_DOUBLE_T = new SchemaElementType("double");
  IElementType E_ENUM = new SchemaElementType("enum");
  IElementType E_EQ = new SchemaElementType("=");
  IElementType E_EXTENDS = new SchemaElementType("extends");
  IElementType E_ID = new SchemaElementType("id");
  IElementType E_IMPORT = new SchemaElementType("import");
  IElementType E_INTEGER_T = new SchemaElementType("integer");
  IElementType E_LIST = new SchemaElementType("list");
  IElementType E_LONG_T = new SchemaElementType("long");
  IElementType E_MAP = new SchemaElementType("map");
  IElementType E_META = new SchemaElementType("meta");
  IElementType E_NAMESPACE = new SchemaElementType("namespace");
  IElementType E_NODEFAULT = new SchemaElementType("nodefault");
  IElementType E_NULL = new SchemaElementType("null");
  IElementType E_NUMBER = new SchemaElementType("number");
  IElementType E_OVERRIDE = new SchemaElementType("override");
  IElementType E_PAREN_LEFT = new SchemaElementType("(");
  IElementType E_PAREN_RIGHT = new SchemaElementType(")");
  IElementType E_POLYMORPHIC = new SchemaElementType("polymorphic");
  IElementType E_RECORD = new SchemaElementType("record");
  IElementType E_SLASH = new SchemaElementType("/");
  IElementType E_STRING = new SchemaElementType("string");
  IElementType E_STRING_T = new SchemaElementType("string");
  IElementType E_SUPPLEMENT = new SchemaElementType("supplement");
  IElementType E_SUPPLEMENTS = new SchemaElementType("supplements");
  IElementType E_VARTYPE = new SchemaElementType("vartype");
  IElementType E_WITH = new SchemaElementType("with");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == E_ANON_LIST) {
        return new SchemaAnonListImpl(node);
      }
      else if (type == E_ANON_MAP) {
        return new SchemaAnonMapImpl(node);
      }
      else if (type == E_CUSTOM_PARAM) {
        return new SchemaCustomParamImpl(node);
      }
      else if (type == E_DATA_ENUM) {
        return new SchemaDataEnumImpl(node);
      }
      else if (type == E_DATA_LIST) {
        return new SchemaDataListImpl(node);
      }
      else if (type == E_DATA_MAP) {
        return new SchemaDataMapImpl(node);
      }
      else if (type == E_DATA_MAP_ENTRY) {
        return new SchemaDataMapEntryImpl(node);
      }
      else if (type == E_DATA_PRIMITIVE) {
        return new SchemaDataPrimitiveImpl(node);
      }
      else if (type == E_DATA_RECORD) {
        return new SchemaDataRecordImpl(node);
      }
      else if (type == E_DATA_RECORD_ENTRY) {
        return new SchemaDataRecordEntryImpl(node);
      }
      else if (type == E_DATA_VALUE) {
        return new SchemaDataValueImpl(node);
      }
      else if (type == E_DATA_VAR) {
        return new SchemaDataVarImpl(node);
      }
      else if (type == E_DATA_VAR_ENTRY) {
        return new SchemaDataVarEntryImpl(node);
      }
      else if (type == E_DEFAULT_OVERRIDE) {
        return new SchemaDefaultOverrideImpl(node);
      }
      else if (type == E_DEFS) {
        return new SchemaDefsImpl(node);
      }
      else if (type == E_ENUM_MEMBER_DECL) {
        return new SchemaEnumMemberDeclImpl(node);
      }
      else if (type == E_ENUM_TYPE_BODY) {
        return new SchemaEnumTypeBodyImpl(node);
      }
      else if (type == E_ENUM_TYPE_DEF) {
        return new SchemaEnumTypeDefImpl(node);
      }
      else if (type == E_EXTENDS_DECL) {
        return new SchemaExtendsDeclImpl(node);
      }
      else if (type == E_FIELD_DECL) {
        return new SchemaFieldDeclImpl(node);
      }
      else if (type == E_FQN) {
        return new SchemaFqnImpl(node);
      }
      else if (type == E_FQN_SEGMENT) {
        return new SchemaFqnSegmentImpl(node);
      }
      else if (type == E_FQN_TYPE_REF) {
        return new SchemaFqnTypeRefImpl(node);
      }
      else if (type == E_IMPORTS) {
        return new SchemaImportsImpl(node);
      }
      else if (type == E_IMPORT_STATEMENT) {
        return new SchemaImportStatementImpl(node);
      }
      else if (type == E_LIST_TYPE_BODY) {
        return new SchemaListTypeBodyImpl(node);
      }
      else if (type == E_LIST_TYPE_DEF) {
        return new SchemaListTypeDefImpl(node);
      }
      else if (type == E_MAP_TYPE_BODY) {
        return new SchemaMapTypeBodyImpl(node);
      }
      else if (type == E_MAP_TYPE_DEF) {
        return new SchemaMapTypeDefImpl(node);
      }
      else if (type == E_META_DECL) {
        return new SchemaMetaDeclImpl(node);
      }
      else if (type == E_NAMESPACE_DECL) {
        return new SchemaNamespaceDeclImpl(node);
      }
      else if (type == E_PRIMITIVE_TYPE_BODY) {
        return new SchemaPrimitiveTypeBodyImpl(node);
      }
      else if (type == E_PRIMITIVE_TYPE_DEF) {
        return new SchemaPrimitiveTypeDefImpl(node);
      }
      else if (type == E_QID) {
        return new SchemaQidImpl(node);
      }
      else if (type == E_RECORD_TYPE_BODY) {
        return new SchemaRecordTypeBodyImpl(node);
      }
      else if (type == E_RECORD_TYPE_DEF) {
        return new SchemaRecordTypeDefImpl(node);
      }
      else if (type == E_SUPPLEMENTS_DECL) {
        return new SchemaSupplementsDeclImpl(node);
      }
      else if (type == E_SUPPLEMENT_DEF) {
        return new SchemaSupplementDefImpl(node);
      }
      else if (type == E_TYPE_DEF_WRAPPER) {
        return new SchemaTypeDefWrapperImpl(node);
      }
      else if (type == E_TYPE_REF) {
        return new SchemaTypeRefImpl(node);
      }
      else if (type == E_VALUE_TYPE_REF) {
        return new SchemaValueTypeRefImpl(node);
      }
      else if (type == E_VAR_TAG_DECL) {
        return new SchemaVarTagDeclImpl(node);
      }
      else if (type == E_VAR_TAG_REF) {
        return new SchemaVarTagRefImpl(node);
      }
      else if (type == E_VAR_TYPE_BODY) {
        return new SchemaVarTypeBodyImpl(node);
      }
      else if (type == E_VAR_TYPE_DEF) {
        return new SchemaVarTypeDefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
