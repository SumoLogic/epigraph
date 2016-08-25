// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
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

  IElementType E_ANON_LIST = new EpigraphElementType("E_ANON_LIST");
  IElementType E_ANON_MAP = new EpigraphElementType("E_ANON_MAP");
  IElementType E_CUSTOM_PARAM = new EpigraphElementType("E_CUSTOM_PARAM");
  IElementType E_DATA_ENUM = new EpigraphElementType("E_DATA_ENUM");
  IElementType E_DATA_LIST = new EpigraphElementType("E_DATA_LIST");
  IElementType E_DATA_MAP = new EpigraphElementType("E_DATA_MAP");
  IElementType E_DATA_MAP_ENTRY = new EpigraphElementType("E_DATA_MAP_ENTRY");
  IElementType E_DATA_PRIMITIVE = new EpigraphElementType("E_DATA_PRIMITIVE");
  IElementType E_DATA_RECORD = new EpigraphElementType("E_DATA_RECORD");
  IElementType E_DATA_RECORD_ENTRY = new EpigraphElementType("E_DATA_RECORD_ENTRY");
  IElementType E_DATA_VALUE = new EpigraphElementType("E_DATA_VALUE");
  IElementType E_DATA_VAR = new EpigraphElementType("E_DATA_VAR");
  IElementType E_DATA_VAR_ENTRY = new EpigraphElementType("E_DATA_VAR_ENTRY");
  IElementType E_DEFAULT_OVERRIDE = new EpigraphElementType("E_DEFAULT_OVERRIDE");
  IElementType E_DEFS = new EpigraphElementType("E_DEFS");
  IElementType E_ENUM_MEMBER_DECL = new EpigraphElementType("E_ENUM_MEMBER_DECL");
  IElementType E_ENUM_TYPE_BODY = new EpigraphElementType("E_ENUM_TYPE_BODY");
  IElementType E_ENUM_TYPE_DEF = new SchemaEnumTypeDefStubElementType("E_ENUM_TYPE_DEF");
  IElementType E_EXTENDS_DECL = new EpigraphElementType("E_EXTENDS_DECL");
  IElementType E_FIELD_DECL = new EpigraphElementType("E_FIELD_DECL");
  IElementType E_FQN = new EpigraphElementType("E_FQN");
  IElementType E_FQN_SEGMENT = new EpigraphElementType("E_FQN_SEGMENT");
  IElementType E_FQN_TYPE_REF = new EpigraphElementType("E_FQN_TYPE_REF");
  IElementType E_IMPORTS = new EpigraphElementType("E_IMPORTS");
  IElementType E_IMPORT_STATEMENT = new EpigraphElementType("E_IMPORT_STATEMENT");
  IElementType E_LIST_TYPE_BODY = new EpigraphElementType("E_LIST_TYPE_BODY");
  IElementType E_LIST_TYPE_DEF = new SchemaListTypeDefStubElementType("E_LIST_TYPE_DEF");
  IElementType E_MAP_TYPE_BODY = new EpigraphElementType("E_MAP_TYPE_BODY");
  IElementType E_MAP_TYPE_DEF = new SchemaMapTypeDefStubElementType("E_MAP_TYPE_DEF");
  IElementType E_META_DECL = new EpigraphElementType("E_META_DECL");
  IElementType E_NAMESPACE_DECL = new SchemaNamespaceDeclStubElementType("E_NAMESPACE_DECL");
  IElementType E_PRIMITIVE_TYPE_BODY = new EpigraphElementType("E_PRIMITIVE_TYPE_BODY");
  IElementType E_PRIMITIVE_TYPE_DEF = new SchemaPrimitiveTypeDefStubElementType("E_PRIMITIVE_TYPE_DEF");
  IElementType E_QID = new EpigraphElementType("E_QID");
  IElementType E_RECORD_TYPE_BODY = new EpigraphElementType("E_RECORD_TYPE_BODY");
  IElementType E_RECORD_TYPE_DEF = new SchemaRecordTypeDefStubElementType("E_RECORD_TYPE_DEF");
  IElementType E_SUPPLEMENTS_DECL = new EpigraphElementType("E_SUPPLEMENTS_DECL");
  IElementType E_SUPPLEMENT_DEF = new SchemaSupplementDefStubElementType("E_SUPPLEMENT_DEF");
  IElementType E_TYPE_DEF_WRAPPER = new SchemaTypeDefWrapperStubElementType("E_TYPE_DEF_WRAPPER");
  IElementType E_TYPE_REF = new EpigraphElementType("E_TYPE_REF");
  IElementType E_VALUE_TYPE_REF = new EpigraphElementType("E_VALUE_TYPE_REF");
  IElementType E_VAR_TAG_DECL = new EpigraphElementType("E_VAR_TAG_DECL");
  IElementType E_VAR_TAG_REF = new EpigraphElementType("E_VAR_TAG_REF");
  IElementType E_VAR_TYPE_BODY = new EpigraphElementType("E_VAR_TYPE_BODY");
  IElementType E_VAR_TYPE_DEF = new SchemaVarTypeDefStubElementType("E_VAR_TYPE_DEF");

  IElementType E_ABSTRACT = new EpigraphElementType("abstract");
  IElementType E_ANGLE_LEFT = new EpigraphElementType("<");
  IElementType E_ANGLE_RIGHT = new EpigraphElementType(">");
  IElementType E_BLOCK_COMMENT = new EpigraphElementType("block_comment");
  IElementType E_BOOLEAN = new EpigraphElementType("boolean");
  IElementType E_BOOLEAN_T = new EpigraphElementType("boolean");
  IElementType E_BRACKET_LEFT = new EpigraphElementType("[");
  IElementType E_BRACKET_RIGHT = new EpigraphElementType("]");
  IElementType E_COLON = new EpigraphElementType(":");
  IElementType E_COMMA = new EpigraphElementType(",");
  IElementType E_COMMENT = new EpigraphElementType("comment");
  IElementType E_CURLY_LEFT = new EpigraphElementType("{");
  IElementType E_CURLY_RIGHT = new EpigraphElementType("}");
  IElementType E_DEFAULT = new EpigraphElementType("default");
  IElementType E_DOT = new EpigraphElementType(".");
  IElementType E_DOUBLE_T = new EpigraphElementType("double");
  IElementType E_ENUM = new EpigraphElementType("enum");
  IElementType E_EQ = new EpigraphElementType("=");
  IElementType E_EXTENDS = new EpigraphElementType("extends");
  IElementType E_ID = new EpigraphElementType("id");
  IElementType E_IMPORT = new EpigraphElementType("import");
  IElementType E_INTEGER_T = new EpigraphElementType("integer");
  IElementType E_LIST = new EpigraphElementType("list");
  IElementType E_LONG_T = new EpigraphElementType("long");
  IElementType E_MAP = new EpigraphElementType("map");
  IElementType E_META = new EpigraphElementType("meta");
  IElementType E_NAMESPACE = new EpigraphElementType("namespace");
  IElementType E_NODEFAULT = new EpigraphElementType("nodefault");
  IElementType E_NULL = new EpigraphElementType("null");
  IElementType E_NUMBER = new EpigraphElementType("number");
  IElementType E_OVERRIDE = new EpigraphElementType("override");
  IElementType E_PAREN_LEFT = new EpigraphElementType("(");
  IElementType E_PAREN_RIGHT = new EpigraphElementType(")");
  IElementType E_POLYMORPHIC = new EpigraphElementType("polymorphic");
  IElementType E_RECORD = new EpigraphElementType("record");
  IElementType E_SLASH = new EpigraphElementType("/");
  IElementType E_STRING = new EpigraphElementType("string");
  IElementType E_STRING_T = new EpigraphElementType("string");
  IElementType E_SUPPLEMENT = new EpigraphElementType("supplement");
  IElementType E_SUPPLEMENTS = new EpigraphElementType("supplements");
  IElementType E_VARTYPE = new EpigraphElementType("vartype");
  IElementType E_WITH = new EpigraphElementType("with");

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
