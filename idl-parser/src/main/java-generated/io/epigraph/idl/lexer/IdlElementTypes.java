// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.epigraph.idl.parser.psi.impl.*;

public interface IdlElementTypes {

  IElementType I_CUSTOM_PARAM = new IdlElementType("I_CUSTOM_PARAM");
  IElementType I_DATA_ENUM = new IdlElementType("I_DATA_ENUM");
  IElementType I_DATA_LIST = new IdlElementType("I_DATA_LIST");
  IElementType I_DATA_MAP = new IdlElementType("I_DATA_MAP");
  IElementType I_DATA_MAP_ENTRY = new IdlElementType("I_DATA_MAP_ENTRY");
  IElementType I_DATA_PRIMITIVE = new IdlElementType("I_DATA_PRIMITIVE");
  IElementType I_DATA_RECORD = new IdlElementType("I_DATA_RECORD");
  IElementType I_DATA_RECORD_ENTRY = new IdlElementType("I_DATA_RECORD_ENTRY");
  IElementType I_DATA_VALUE = new IdlElementType("I_DATA_VALUE");
  IElementType I_DATA_VAR = new IdlElementType("I_DATA_VAR");
  IElementType I_DATA_VAR_ENTRY = new IdlElementType("I_DATA_VAR_ENTRY");
  IElementType I_FQN = new IdlElementType("I_FQN");
  IElementType I_FQN_SEGMENT = new IdlElementType("I_FQN_SEGMENT");
  IElementType I_FQN_TYPE_REF = new IdlElementType("I_FQN_TYPE_REF");
  IElementType I_IMPORTS = new IdlElementType("I_IMPORTS");
  IElementType I_IMPORT_STATEMENT = new IdlElementType("I_IMPORT_STATEMENT");
  IElementType I_NAMESPACE_DECL = new IdlElementType("I_NAMESPACE_DECL");
  IElementType I_OP_INPUT_MODEL_PROJECTION = new IdlElementType("I_OP_INPUT_MODEL_PROJECTION");
  IElementType I_OP_OUTPUT_FIELD_PROJECTION = new IdlElementType("I_OP_OUTPUT_FIELD_PROJECTION");
  IElementType I_OP_OUTPUT_FIELD_PROJECTION_BODY = new IdlElementType("I_OP_OUTPUT_FIELD_PROJECTION_BODY");
  IElementType I_OP_OUTPUT_FIELD_PROJECTION_BODY_PART = new IdlElementType("I_OP_OUTPUT_FIELD_PROJECTION_BODY_PART");
  IElementType I_OP_OUTPUT_KEY_PROJECTION = new IdlElementType("I_OP_OUTPUT_KEY_PROJECTION");
  IElementType I_OP_OUTPUT_KEY_PROJECTION_PART = new IdlElementType("I_OP_OUTPUT_KEY_PROJECTION_PART");
  IElementType I_OP_OUTPUT_LIST_MODEL_PROJECTION = new IdlElementType("I_OP_OUTPUT_LIST_MODEL_PROJECTION");
  IElementType I_OP_OUTPUT_LIST_POLY_BRANCH = new IdlElementType("I_OP_OUTPUT_LIST_POLY_BRANCH");
  IElementType I_OP_OUTPUT_MAP_MODEL_PROJECTION = new IdlElementType("I_OP_OUTPUT_MAP_MODEL_PROJECTION");
  IElementType I_OP_OUTPUT_MAP_POLY_BRANCH = new IdlElementType("I_OP_OUTPUT_MAP_POLY_BRANCH");
  IElementType I_OP_OUTPUT_MODEL_PROJECTION = new IdlElementType("I_OP_OUTPUT_MODEL_PROJECTION");
  IElementType I_OP_OUTPUT_MODEL_PROJECTION_BODY = new IdlElementType("I_OP_OUTPUT_MODEL_PROJECTION_BODY");
  IElementType I_OP_OUTPUT_MODEL_PROJECTION_BODY_PART = new IdlElementType("I_OP_OUTPUT_MODEL_PROJECTION_BODY_PART");
  IElementType I_OP_OUTPUT_RECORD_MODEL_PROJECTION = new IdlElementType("I_OP_OUTPUT_RECORD_MODEL_PROJECTION");
  IElementType I_OP_OUTPUT_RECORD_POLY_BRANCH = new IdlElementType("I_OP_OUTPUT_RECORD_POLY_BRANCH");
  IElementType I_OP_OUTPUT_TAG_PROJECTION = new IdlElementType("I_OP_OUTPUT_TAG_PROJECTION");
  IElementType I_OP_OUTPUT_VAR_PROJECTION = new IdlElementType("I_OP_OUTPUT_VAR_PROJECTION");
  IElementType I_OP_PARAMETERS = new IdlElementType("I_OP_PARAMETERS");
  IElementType I_OP_PARAM_PROJECTION = new IdlElementType("I_OP_PARAM_PROJECTION");
  IElementType I_QID = new IdlElementType("I_QID");

  IElementType I_ANGLE_LEFT = new IdlElementType("<");
  IElementType I_ANGLE_RIGHT = new IdlElementType(">");
  IElementType I_BLOCK_COMMENT = new IdlElementType("block_comment");
  IElementType I_BOOLEAN = new IdlElementType("boolean");
  IElementType I_BRACKET_LEFT = new IdlElementType("[");
  IElementType I_BRACKET_RIGHT = new IdlElementType("]");
  IElementType I_COLON = new IdlElementType(":");
  IElementType I_COMMA = new IdlElementType(",");
  IElementType I_COMMENT = new IdlElementType("comment");
  IElementType I_CURLY_LEFT = new IdlElementType("{");
  IElementType I_CURLY_RIGHT = new IdlElementType("}");
  IElementType I_DEFAULT = new IdlElementType("default");
  IElementType I_DOT = new IdlElementType(".");
  IElementType I_EQ = new IdlElementType("=");
  IElementType I_FORBIDDEN = new IdlElementType("forbidden");
  IElementType I_ID = new IdlElementType("id");
  IElementType I_IMPORT = new IdlElementType("import");
  IElementType I_NAMESPACE = new IdlElementType("namespace");
  IElementType I_NULL = new IdlElementType("null");
  IElementType I_NUMBER = new IdlElementType("number");
  IElementType I_PARAMETERS = new IdlElementType("parameters");
  IElementType I_PAREN_LEFT = new IdlElementType("(");
  IElementType I_PAREN_RIGHT = new IdlElementType(")");
  IElementType I_REQURIED = new IdlElementType("required");
  IElementType I_SLASH = new IdlElementType("/");
  IElementType I_STAR = new IdlElementType("*");
  IElementType I_STRING = new IdlElementType("string");
  IElementType I_TILDA = new IdlElementType("~");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == I_CUSTOM_PARAM) {
        return new IdlCustomParamImpl(node);
      }
      else if (type == I_DATA_ENUM) {
        return new IdlDataEnumImpl(node);
      }
      else if (type == I_DATA_LIST) {
        return new IdlDataListImpl(node);
      }
      else if (type == I_DATA_MAP) {
        return new IdlDataMapImpl(node);
      }
      else if (type == I_DATA_MAP_ENTRY) {
        return new IdlDataMapEntryImpl(node);
      }
      else if (type == I_DATA_PRIMITIVE) {
        return new IdlDataPrimitiveImpl(node);
      }
      else if (type == I_DATA_RECORD) {
        return new IdlDataRecordImpl(node);
      }
      else if (type == I_DATA_RECORD_ENTRY) {
        return new IdlDataRecordEntryImpl(node);
      }
      else if (type == I_DATA_VALUE) {
        return new IdlDataValueImpl(node);
      }
      else if (type == I_DATA_VAR) {
        return new IdlDataVarImpl(node);
      }
      else if (type == I_DATA_VAR_ENTRY) {
        return new IdlDataVarEntryImpl(node);
      }
      else if (type == I_FQN) {
        return new IdlFqnImpl(node);
      }
      else if (type == I_FQN_SEGMENT) {
        return new IdlFqnSegmentImpl(node);
      }
      else if (type == I_FQN_TYPE_REF) {
        return new IdlFqnTypeRefImpl(node);
      }
      else if (type == I_IMPORTS) {
        return new IdlImportsImpl(node);
      }
      else if (type == I_IMPORT_STATEMENT) {
        return new IdlImportStatementImpl(node);
      }
      else if (type == I_NAMESPACE_DECL) {
        return new IdlNamespaceDeclImpl(node);
      }
      else if (type == I_OP_INPUT_MODEL_PROJECTION) {
        return new IdlOpInputModelProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_FIELD_PROJECTION) {
        return new IdlOpOutputFieldProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_FIELD_PROJECTION_BODY) {
        return new IdlOpOutputFieldProjectionBodyImpl(node);
      }
      else if (type == I_OP_OUTPUT_FIELD_PROJECTION_BODY_PART) {
        return new IdlOpOutputFieldProjectionBodyPartImpl(node);
      }
      else if (type == I_OP_OUTPUT_KEY_PROJECTION) {
        return new IdlOpOutputKeyProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_KEY_PROJECTION_PART) {
        return new IdlOpOutputKeyProjectionPartImpl(node);
      }
      else if (type == I_OP_OUTPUT_LIST_MODEL_PROJECTION) {
        return new IdlOpOutputListModelProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_LIST_POLY_BRANCH) {
        return new IdlOpOutputListPolyBranchImpl(node);
      }
      else if (type == I_OP_OUTPUT_MAP_MODEL_PROJECTION) {
        return new IdlOpOutputMapModelProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_MAP_POLY_BRANCH) {
        return new IdlOpOutputMapPolyBranchImpl(node);
      }
      else if (type == I_OP_OUTPUT_MODEL_PROJECTION) {
        return new IdlOpOutputModelProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_MODEL_PROJECTION_BODY) {
        return new IdlOpOutputModelProjectionBodyImpl(node);
      }
      else if (type == I_OP_OUTPUT_MODEL_PROJECTION_BODY_PART) {
        return new IdlOpOutputModelProjectionBodyPartImpl(node);
      }
      else if (type == I_OP_OUTPUT_RECORD_MODEL_PROJECTION) {
        return new IdlOpOutputRecordModelProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_RECORD_POLY_BRANCH) {
        return new IdlOpOutputRecordPolyBranchImpl(node);
      }
      else if (type == I_OP_OUTPUT_TAG_PROJECTION) {
        return new IdlOpOutputTagProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_VAR_PROJECTION) {
        return new IdlOpOutputVarProjectionImpl(node);
      }
      else if (type == I_OP_PARAMETERS) {
        return new IdlOpParametersImpl(node);
      }
      else if (type == I_OP_PARAM_PROJECTION) {
        return new IdlOpParamProjectionImpl(node);
      }
      else if (type == I_QID) {
        return new IdlQidImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
