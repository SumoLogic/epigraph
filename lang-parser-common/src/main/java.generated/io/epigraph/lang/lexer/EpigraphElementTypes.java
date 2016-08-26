// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.epigraph.lang.parser.psi.stubs.EpigraphEnumTypeDefStubElementType;
import io.epigraph.lang.parser.psi.stubs.EpigraphListTypeDefStubElementType;
import io.epigraph.lang.parser.psi.stubs.EpigraphMapTypeDefStubElementType;
import io.epigraph.lang.parser.psi.stubs.EpigraphNamespaceDeclStubElementType;
import io.epigraph.lang.parser.psi.stubs.EpigraphPrimitiveTypeDefStubElementType;
import io.epigraph.lang.parser.psi.stubs.EpigraphRecordTypeDefStubElementType;
import io.epigraph.lang.parser.psi.stubs.EpigraphSupplementDefStubElementType;
import io.epigraph.lang.parser.psi.stubs.EpigraphTypeDefWrapperStubElementType;
import io.epigraph.lang.parser.psi.stubs.EpigraphVarTypeDefStubElementType;
import io.epigraph.lang.parser.psi.impl.*;

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
  IElementType E_ENUM_TYPE_DEF = new EpigraphEnumTypeDefStubElementType("E_ENUM_TYPE_DEF");
  IElementType E_EXTENDS_DECL = new EpigraphElementType("E_EXTENDS_DECL");
  IElementType E_FIELD_DECL = new EpigraphElementType("E_FIELD_DECL");
  IElementType E_FQN = new EpigraphElementType("E_FQN");
  IElementType E_FQN_SEGMENT = new EpigraphElementType("E_FQN_SEGMENT");
  IElementType E_FQN_TYPE_REF = new EpigraphElementType("E_FQN_TYPE_REF");
  IElementType E_IMPORTS = new EpigraphElementType("E_IMPORTS");
  IElementType E_IMPORT_STATEMENT = new EpigraphElementType("E_IMPORT_STATEMENT");
  IElementType E_LIST_TYPE_BODY = new EpigraphElementType("E_LIST_TYPE_BODY");
  IElementType E_LIST_TYPE_DEF = new EpigraphListTypeDefStubElementType("E_LIST_TYPE_DEF");
  IElementType E_MAP_TYPE_BODY = new EpigraphElementType("E_MAP_TYPE_BODY");
  IElementType E_MAP_TYPE_DEF = new EpigraphMapTypeDefStubElementType("E_MAP_TYPE_DEF");
  IElementType E_META_DECL = new EpigraphElementType("E_META_DECL");
  IElementType E_NAMESPACE_DECL = new EpigraphNamespaceDeclStubElementType("E_NAMESPACE_DECL");
  IElementType E_OP_INPUT_MODEL_PROJECTION = new EpigraphElementType("E_OP_INPUT_MODEL_PROJECTION");
  IElementType E_OP_OUTPUT_ENUM_MODEL_PROJECTION = new EpigraphElementType("E_OP_OUTPUT_ENUM_MODEL_PROJECTION");
  IElementType E_OP_OUTPUT_FIELD_PROJECTION = new EpigraphElementType("E_OP_OUTPUT_FIELD_PROJECTION");
  IElementType E_OP_OUTPUT_FIELD_PROJECTION_BODY = new EpigraphElementType("E_OP_OUTPUT_FIELD_PROJECTION_BODY");
  IElementType E_OP_OUTPUT_FIELD_PROJECTION_BODY_PART = new EpigraphElementType("E_OP_OUTPUT_FIELD_PROJECTION_BODY_PART");
  IElementType E_OP_OUTPUT_KEY_PROJECTION = new EpigraphElementType("E_OP_OUTPUT_KEY_PROJECTION");
  IElementType E_OP_OUTPUT_KEY_PROJECTION_PART = new EpigraphElementType("E_OP_OUTPUT_KEY_PROJECTION_PART");
  IElementType E_OP_OUTPUT_LIST_MODEL_PROJECTION = new EpigraphElementType("E_OP_OUTPUT_LIST_MODEL_PROJECTION");
  IElementType E_OP_OUTPUT_LIST_POLY_BRANCH = new EpigraphElementType("E_OP_OUTPUT_LIST_POLY_BRANCH");
  IElementType E_OP_OUTPUT_MAP_MODEL_PROJECTION = new EpigraphElementType("E_OP_OUTPUT_MAP_MODEL_PROJECTION");
  IElementType E_OP_OUTPUT_MAP_POLY_BRANCH = new EpigraphElementType("E_OP_OUTPUT_MAP_POLY_BRANCH");
  IElementType E_OP_OUTPUT_MODEL_PROJECTION = new EpigraphElementType("E_OP_OUTPUT_MODEL_PROJECTION");
  IElementType E_OP_OUTPUT_MODEL_PROJECTION_BODY = new EpigraphElementType("E_OP_OUTPUT_MODEL_PROJECTION_BODY");
  IElementType E_OP_OUTPUT_MODEL_PROJECTION_BODY_PART = new EpigraphElementType("E_OP_OUTPUT_MODEL_PROJECTION_BODY_PART");
  IElementType E_OP_OUTPUT_PRIMITIVE_MODEL_PROJECTION = new EpigraphElementType("E_OP_OUTPUT_PRIMITIVE_MODEL_PROJECTION");
  IElementType E_OP_OUTPUT_RECORD_MODEL_PROJECTION = new EpigraphElementType("E_OP_OUTPUT_RECORD_MODEL_PROJECTION");
  IElementType E_OP_OUTPUT_RECORD_POLY_BRANCH = new EpigraphElementType("E_OP_OUTPUT_RECORD_POLY_BRANCH");
  IElementType E_OP_OUTPUT_TAG_PROJECTION = new EpigraphElementType("E_OP_OUTPUT_TAG_PROJECTION");
  IElementType E_OP_OUTPUT_VAR_PROJECTION = new EpigraphElementType("E_OP_OUTPUT_VAR_PROJECTION");
  IElementType E_OP_PARAMETERS = new EpigraphElementType("E_OP_PARAMETERS");
  IElementType E_OP_PARAM_PROJECTION = new EpigraphElementType("E_OP_PARAM_PROJECTION");
  IElementType E_PRIMITIVE_TYPE_BODY = new EpigraphElementType("E_PRIMITIVE_TYPE_BODY");
  IElementType E_PRIMITIVE_TYPE_DEF = new EpigraphPrimitiveTypeDefStubElementType("E_PRIMITIVE_TYPE_DEF");
  IElementType E_QID = new EpigraphElementType("E_QID");
  IElementType E_RECORD_TYPE_BODY = new EpigraphElementType("E_RECORD_TYPE_BODY");
  IElementType E_RECORD_TYPE_DEF = new EpigraphRecordTypeDefStubElementType("E_RECORD_TYPE_DEF");
  IElementType E_SUPPLEMENTS_DECL = new EpigraphElementType("E_SUPPLEMENTS_DECL");
  IElementType E_SUPPLEMENT_DEF = new EpigraphSupplementDefStubElementType("E_SUPPLEMENT_DEF");
  IElementType E_TYPE_DEF_WRAPPER = new EpigraphTypeDefWrapperStubElementType("E_TYPE_DEF_WRAPPER");
  IElementType E_TYPE_REF = new EpigraphElementType("E_TYPE_REF");
  IElementType E_VALUE_TYPE_REF = new EpigraphElementType("E_VALUE_TYPE_REF");
  IElementType E_VAR_TAG_DECL = new EpigraphElementType("E_VAR_TAG_DECL");
  IElementType E_VAR_TAG_REF = new EpigraphElementType("E_VAR_TAG_REF");
  IElementType E_VAR_TYPE_BODY = new EpigraphElementType("E_VAR_TYPE_BODY");
  IElementType E_VAR_TYPE_DEF = new EpigraphVarTypeDefStubElementType("E_VAR_TYPE_DEF");

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
        return new EpigraphAnonListImpl(node);
      }
      else if (type == E_ANON_MAP) {
        return new EpigraphAnonMapImpl(node);
      }
      else if (type == E_CUSTOM_PARAM) {
        return new EpigraphCustomParamImpl(node);
      }
      else if (type == E_DATA_ENUM) {
        return new EpigraphDataEnumImpl(node);
      }
      else if (type == E_DATA_LIST) {
        return new EpigraphDataListImpl(node);
      }
      else if (type == E_DATA_MAP) {
        return new EpigraphDataMapImpl(node);
      }
      else if (type == E_DATA_MAP_ENTRY) {
        return new EpigraphDataMapEntryImpl(node);
      }
      else if (type == E_DATA_PRIMITIVE) {
        return new EpigraphDataPrimitiveImpl(node);
      }
      else if (type == E_DATA_RECORD) {
        return new EpigraphDataRecordImpl(node);
      }
      else if (type == E_DATA_RECORD_ENTRY) {
        return new EpigraphDataRecordEntryImpl(node);
      }
      else if (type == E_DATA_VALUE) {
        return new EpigraphDataValueImpl(node);
      }
      else if (type == E_DATA_VAR) {
        return new EpigraphDataVarImpl(node);
      }
      else if (type == E_DATA_VAR_ENTRY) {
        return new EpigraphDataVarEntryImpl(node);
      }
      else if (type == E_DEFAULT_OVERRIDE) {
        return new EpigraphDefaultOverrideImpl(node);
      }
      else if (type == E_DEFS) {
        return new EpigraphDefsImpl(node);
      }
      else if (type == E_ENUM_MEMBER_DECL) {
        return new EpigraphEnumMemberDeclImpl(node);
      }
      else if (type == E_ENUM_TYPE_BODY) {
        return new EpigraphEnumTypeBodyImpl(node);
      }
      else if (type == E_ENUM_TYPE_DEF) {
        return new EpigraphEnumTypeDefImpl(node);
      }
      else if (type == E_EXTENDS_DECL) {
        return new EpigraphExtendsDeclImpl(node);
      }
      else if (type == E_FIELD_DECL) {
        return new EpigraphFieldDeclImpl(node);
      }
      else if (type == E_FQN) {
        return new EpigraphFqnImpl(node);
      }
      else if (type == E_FQN_SEGMENT) {
        return new EpigraphFqnSegmentImpl(node);
      }
      else if (type == E_FQN_TYPE_REF) {
        return new EpigraphFqnTypeRefImpl(node);
      }
      else if (type == E_IMPORTS) {
        return new EpigraphImportsImpl(node);
      }
      else if (type == E_IMPORT_STATEMENT) {
        return new EpigraphImportStatementImpl(node);
      }
      else if (type == E_LIST_TYPE_BODY) {
        return new EpigraphListTypeBodyImpl(node);
      }
      else if (type == E_LIST_TYPE_DEF) {
        return new EpigraphListTypeDefImpl(node);
      }
      else if (type == E_MAP_TYPE_BODY) {
        return new EpigraphMapTypeBodyImpl(node);
      }
      else if (type == E_MAP_TYPE_DEF) {
        return new EpigraphMapTypeDefImpl(node);
      }
      else if (type == E_META_DECL) {
        return new EpigraphMetaDeclImpl(node);
      }
      else if (type == E_NAMESPACE_DECL) {
        return new EpigraphNamespaceDeclImpl(node);
      }
      else if (type == E_OP_INPUT_MODEL_PROJECTION) {
        return new EpigraphOpInputModelProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_ENUM_MODEL_PROJECTION) {
        return new EpigraphOpOutputEnumModelProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_FIELD_PROJECTION) {
        return new EpigraphOpOutputFieldProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_FIELD_PROJECTION_BODY) {
        return new EpigraphOpOutputFieldProjectionBodyImpl(node);
      }
      else if (type == E_OP_OUTPUT_FIELD_PROJECTION_BODY_PART) {
        return new EpigraphOpOutputFieldProjectionBodyPartImpl(node);
      }
      else if (type == E_OP_OUTPUT_KEY_PROJECTION) {
        return new EpigraphOpOutputKeyProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_KEY_PROJECTION_PART) {
        return new EpigraphOpOutputKeyProjectionPartImpl(node);
      }
      else if (type == E_OP_OUTPUT_LIST_MODEL_PROJECTION) {
        return new EpigraphOpOutputListModelProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_LIST_POLY_BRANCH) {
        return new EpigraphOpOutputListPolyBranchImpl(node);
      }
      else if (type == E_OP_OUTPUT_MAP_MODEL_PROJECTION) {
        return new EpigraphOpOutputMapModelProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_MAP_POLY_BRANCH) {
        return new EpigraphOpOutputMapPolyBranchImpl(node);
      }
      else if (type == E_OP_OUTPUT_MODEL_PROJECTION) {
        return new EpigraphOpOutputModelProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_MODEL_PROJECTION_BODY) {
        return new EpigraphOpOutputModelProjectionBodyImpl(node);
      }
      else if (type == E_OP_OUTPUT_MODEL_PROJECTION_BODY_PART) {
        return new EpigraphOpOutputModelProjectionBodyPartImpl(node);
      }
      else if (type == E_OP_OUTPUT_PRIMITIVE_MODEL_PROJECTION) {
        return new EpigraphOpOutputPrimitiveModelProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_RECORD_MODEL_PROJECTION) {
        return new EpigraphOpOutputRecordModelProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_RECORD_POLY_BRANCH) {
        return new EpigraphOpOutputRecordPolyBranchImpl(node);
      }
      else if (type == E_OP_OUTPUT_TAG_PROJECTION) {
        return new EpigraphOpOutputTagProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_VAR_PROJECTION) {
        return new EpigraphOpOutputVarProjectionImpl(node);
      }
      else if (type == E_OP_PARAMETERS) {
        return new EpigraphOpParametersImpl(node);
      }
      else if (type == E_OP_PARAM_PROJECTION) {
        return new EpigraphOpParamProjectionImpl(node);
      }
      else if (type == E_PRIMITIVE_TYPE_BODY) {
        return new EpigraphPrimitiveTypeBodyImpl(node);
      }
      else if (type == E_PRIMITIVE_TYPE_DEF) {
        return new EpigraphPrimitiveTypeDefImpl(node);
      }
      else if (type == E_QID) {
        return new EpigraphQidImpl(node);
      }
      else if (type == E_RECORD_TYPE_BODY) {
        return new EpigraphRecordTypeBodyImpl(node);
      }
      else if (type == E_RECORD_TYPE_DEF) {
        return new EpigraphRecordTypeDefImpl(node);
      }
      else if (type == E_SUPPLEMENTS_DECL) {
        return new EpigraphSupplementsDeclImpl(node);
      }
      else if (type == E_SUPPLEMENT_DEF) {
        return new EpigraphSupplementDefImpl(node);
      }
      else if (type == E_TYPE_DEF_WRAPPER) {
        return new EpigraphTypeDefWrapperImpl(node);
      }
      else if (type == E_TYPE_REF) {
        return new EpigraphTypeRefImpl(node);
      }
      else if (type == E_VALUE_TYPE_REF) {
        return new EpigraphValueTypeRefImpl(node);
      }
      else if (type == E_VAR_TAG_DECL) {
        return new EpigraphVarTagDeclImpl(node);
      }
      else if (type == E_VAR_TAG_REF) {
        return new EpigraphVarTagRefImpl(node);
      }
      else if (type == E_VAR_TYPE_BODY) {
        return new EpigraphVarTypeBodyImpl(node);
      }
      else if (type == E_VAR_TYPE_DEF) {
        return new EpigraphVarTypeDefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
