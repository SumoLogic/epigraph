// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.epigraph.idl.parser.psi.impl.*;

public interface IdlElementTypes {

  IElementType I_ANNOTATION = new IdlElementType("I_ANNOTATION");
  IElementType I_DATA = new IdlElementType("I_DATA");
  IElementType I_DATA_ENTRY = new IdlElementType("I_DATA_ENTRY");
  IElementType I_DATA_VALUE = new IdlElementType("I_DATA_VALUE");
  IElementType I_DATUM = new IdlElementType("I_DATUM");
  IElementType I_ENUM_DATUM = new IdlElementType("I_ENUM_DATUM");
  IElementType I_FQN = new IdlElementType("I_FQN");
  IElementType I_FQN_SEGMENT = new IdlElementType("I_FQN_SEGMENT");
  IElementType I_FQN_TYPE_REF = new IdlElementType("I_FQN_TYPE_REF");
  IElementType I_IMPORTS = new IdlElementType("I_IMPORTS");
  IElementType I_IMPORT_STATEMENT = new IdlElementType("I_IMPORT_STATEMENT");
  IElementType I_LIST_DATUM = new IdlElementType("I_LIST_DATUM");
  IElementType I_MAP_DATUM = new IdlElementType("I_MAP_DATUM");
  IElementType I_MAP_DATUM_ENTRY = new IdlElementType("I_MAP_DATUM_ENTRY");
  IElementType I_NAMESPACE_DECL = new IdlElementType("I_NAMESPACE_DECL");
  IElementType I_NULL_DATUM = new IdlElementType("I_NULL_DATUM");
  IElementType I_OP_INPUT_COMA_FIELD_PROJECTION = new IdlElementType("I_OP_INPUT_COMA_FIELD_PROJECTION");
  IElementType I_OP_INPUT_COMA_KEY_PROJECTION = new IdlElementType("I_OP_INPUT_COMA_KEY_PROJECTION");
  IElementType I_OP_INPUT_COMA_LIST_MODEL_PROJECTION = new IdlElementType("I_OP_INPUT_COMA_LIST_MODEL_PROJECTION");
  IElementType I_OP_INPUT_COMA_MAP_MODEL_PROJECTION = new IdlElementType("I_OP_INPUT_COMA_MAP_MODEL_PROJECTION");
  IElementType I_OP_INPUT_COMA_MODEL_PROJECTION = new IdlElementType("I_OP_INPUT_COMA_MODEL_PROJECTION");
  IElementType I_OP_INPUT_COMA_MULTI_TAG_PROJECTION = new IdlElementType("I_OP_INPUT_COMA_MULTI_TAG_PROJECTION");
  IElementType I_OP_INPUT_COMA_MULTI_TAG_PROJECTION_ITEM = new IdlElementType("I_OP_INPUT_COMA_MULTI_TAG_PROJECTION_ITEM");
  IElementType I_OP_INPUT_COMA_RECORD_MODEL_PROJECTION = new IdlElementType("I_OP_INPUT_COMA_RECORD_MODEL_PROJECTION");
  IElementType I_OP_INPUT_COMA_SINGLE_TAG_PROJECTION = new IdlElementType("I_OP_INPUT_COMA_SINGLE_TAG_PROJECTION");
  IElementType I_OP_INPUT_COMA_VAR_PROJECTION = new IdlElementType("I_OP_INPUT_COMA_VAR_PROJECTION");
  IElementType I_OP_INPUT_DEFAULT_VALUE = new IdlElementType("I_OP_INPUT_DEFAULT_VALUE");
  IElementType I_OP_INPUT_FIELD_PROJECTION_BODY_PART = new IdlElementType("I_OP_INPUT_FIELD_PROJECTION_BODY_PART");
  IElementType I_OP_INPUT_MODEL_META = new IdlElementType("I_OP_INPUT_MODEL_META");
  IElementType I_OP_INPUT_MODEL_PROPERTY = new IdlElementType("I_OP_INPUT_MODEL_PROPERTY");
  IElementType I_OP_INPUT_TRUNK_FIELD_PROJECTION = new IdlElementType("I_OP_INPUT_TRUNK_FIELD_PROJECTION");
  IElementType I_OP_INPUT_TRUNK_MODEL_PROJECTION = new IdlElementType("I_OP_INPUT_TRUNK_MODEL_PROJECTION");
  IElementType I_OP_INPUT_TRUNK_RECORD_MODEL_PROJECTION = new IdlElementType("I_OP_INPUT_TRUNK_RECORD_MODEL_PROJECTION");
  IElementType I_OP_INPUT_TRUNK_SINGLE_TAG_PROJECTION = new IdlElementType("I_OP_INPUT_TRUNK_SINGLE_TAG_PROJECTION");
  IElementType I_OP_INPUT_TRUNK_VAR_PROJECTION = new IdlElementType("I_OP_INPUT_TRUNK_VAR_PROJECTION");
  IElementType I_OP_INPUT_VAR_MULTI_TAIL = new IdlElementType("I_OP_INPUT_VAR_MULTI_TAIL");
  IElementType I_OP_INPUT_VAR_MULTI_TAIL_ITEM = new IdlElementType("I_OP_INPUT_VAR_MULTI_TAIL_ITEM");
  IElementType I_OP_INPUT_VAR_POLYMORPHIC_TAIL = new IdlElementType("I_OP_INPUT_VAR_POLYMORPHIC_TAIL");
  IElementType I_OP_INPUT_VAR_SINGLE_TAIL = new IdlElementType("I_OP_INPUT_VAR_SINGLE_TAIL");
  IElementType I_OP_OUTPUT_FIELD_PROJECTION = new IdlElementType("I_OP_OUTPUT_FIELD_PROJECTION");
  IElementType I_OP_OUTPUT_FIELD_PROJECTION_BODY_PART = new IdlElementType("I_OP_OUTPUT_FIELD_PROJECTION_BODY_PART");
  IElementType I_OP_OUTPUT_KEY_PROJECTION = new IdlElementType("I_OP_OUTPUT_KEY_PROJECTION");
  IElementType I_OP_OUTPUT_KEY_PROJECTION_PART = new IdlElementType("I_OP_OUTPUT_KEY_PROJECTION_PART");
  IElementType I_OP_OUTPUT_LIST_MODEL_PROJECTION = new IdlElementType("I_OP_OUTPUT_LIST_MODEL_PROJECTION");
  IElementType I_OP_OUTPUT_MAP_MODEL_PROJECTION = new IdlElementType("I_OP_OUTPUT_MAP_MODEL_PROJECTION");
  IElementType I_OP_OUTPUT_MODEL_META = new IdlElementType("I_OP_OUTPUT_MODEL_META");
  IElementType I_OP_OUTPUT_MODEL_PROJECTION = new IdlElementType("I_OP_OUTPUT_MODEL_PROJECTION");
  IElementType I_OP_OUTPUT_MODEL_PROPERTY = new IdlElementType("I_OP_OUTPUT_MODEL_PROPERTY");
  IElementType I_OP_OUTPUT_MULTI_TAG_PROJECTION = new IdlElementType("I_OP_OUTPUT_MULTI_TAG_PROJECTION");
  IElementType I_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM = new IdlElementType("I_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM");
  IElementType I_OP_OUTPUT_RECORD_MODEL_PROJECTION = new IdlElementType("I_OP_OUTPUT_RECORD_MODEL_PROJECTION");
  IElementType I_OP_OUTPUT_SINGLE_TAG_PROJECTION = new IdlElementType("I_OP_OUTPUT_SINGLE_TAG_PROJECTION");
  IElementType I_OP_OUTPUT_VAR_MULTI_TAIL = new IdlElementType("I_OP_OUTPUT_VAR_MULTI_TAIL");
  IElementType I_OP_OUTPUT_VAR_MULTI_TAIL_ITEM = new IdlElementType("I_OP_OUTPUT_VAR_MULTI_TAIL_ITEM");
  IElementType I_OP_OUTPUT_VAR_POLYMORPHIC_TAIL = new IdlElementType("I_OP_OUTPUT_VAR_POLYMORPHIC_TAIL");
  IElementType I_OP_OUTPUT_VAR_PROJECTION = new IdlElementType("I_OP_OUTPUT_VAR_PROJECTION");
  IElementType I_OP_OUTPUT_VAR_SINGLE_TAIL = new IdlElementType("I_OP_OUTPUT_VAR_SINGLE_TAIL");
  IElementType I_OP_PARAM = new IdlElementType("I_OP_PARAM");
  IElementType I_PRIMITIVE_DATUM = new IdlElementType("I_PRIMITIVE_DATUM");
  IElementType I_QID = new IdlElementType("I_QID");
  IElementType I_RECORD_DATUM = new IdlElementType("I_RECORD_DATUM");
  IElementType I_RECORD_DATUM_ENTRY = new IdlElementType("I_RECORD_DATUM_ENTRY");
  IElementType I_REQ_ANNOTATION = new IdlElementType("I_REQ_ANNOTATION");
  IElementType I_REQ_OUTPUT_COMA_FIELD_PROJECTION = new IdlElementType("I_REQ_OUTPUT_COMA_FIELD_PROJECTION");
  IElementType I_REQ_OUTPUT_COMA_KEYS_PROJECTION = new IdlElementType("I_REQ_OUTPUT_COMA_KEYS_PROJECTION");
  IElementType I_REQ_OUTPUT_COMA_KEY_PROJECTION = new IdlElementType("I_REQ_OUTPUT_COMA_KEY_PROJECTION");
  IElementType I_REQ_OUTPUT_COMA_LIST_MODEL_PROJECTION = new IdlElementType("I_REQ_OUTPUT_COMA_LIST_MODEL_PROJECTION");
  IElementType I_REQ_OUTPUT_COMA_MAP_MODEL_PROJECTION = new IdlElementType("I_REQ_OUTPUT_COMA_MAP_MODEL_PROJECTION");
  IElementType I_REQ_OUTPUT_COMA_MODEL_PROJECTION = new IdlElementType("I_REQ_OUTPUT_COMA_MODEL_PROJECTION");
  IElementType I_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION = new IdlElementType("I_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION");
  IElementType I_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION_ITEM = new IdlElementType("I_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION_ITEM");
  IElementType I_REQ_OUTPUT_COMA_RECORD_MODEL_PROJECTION = new IdlElementType("I_REQ_OUTPUT_COMA_RECORD_MODEL_PROJECTION");
  IElementType I_REQ_OUTPUT_COMA_SINGLE_TAG_PROJECTION = new IdlElementType("I_REQ_OUTPUT_COMA_SINGLE_TAG_PROJECTION");
  IElementType I_REQ_OUTPUT_COMA_VAR_PROJECTION = new IdlElementType("I_REQ_OUTPUT_COMA_VAR_PROJECTION");
  IElementType I_REQ_OUTPUT_MODEL_META = new IdlElementType("I_REQ_OUTPUT_MODEL_META");
  IElementType I_REQ_OUTPUT_TRUNK_MAP_MODEL_PROJECTION = new IdlElementType("I_REQ_OUTPUT_TRUNK_MAP_MODEL_PROJECTION");
  IElementType I_REQ_OUTPUT_TRUNK_MODEL_PROJECTION = new IdlElementType("I_REQ_OUTPUT_TRUNK_MODEL_PROJECTION");
  IElementType I_REQ_OUTPUT_TRUNK_RECORD_MODEL_PROJECTION = new IdlElementType("I_REQ_OUTPUT_TRUNK_RECORD_MODEL_PROJECTION");
  IElementType I_REQ_OUTPUT_TRUNK_SINGLE_TAG_PROJECTION = new IdlElementType("I_REQ_OUTPUT_TRUNK_SINGLE_TAG_PROJECTION");
  IElementType I_REQ_OUTPUT_TRUNK_VAR_PROJECTION = new IdlElementType("I_REQ_OUTPUT_TRUNK_VAR_PROJECTION");
  IElementType I_REQ_OUTPUT_VAR_MULTI_TAIL = new IdlElementType("I_REQ_OUTPUT_VAR_MULTI_TAIL");
  IElementType I_REQ_OUTPUT_VAR_MULTI_TAIL_ITEM = new IdlElementType("I_REQ_OUTPUT_VAR_MULTI_TAIL_ITEM");
  IElementType I_REQ_OUTPUT_VAR_POLYMORPHIC_TAIL = new IdlElementType("I_REQ_OUTPUT_VAR_POLYMORPHIC_TAIL");
  IElementType I_REQ_OUTPUT_VAR_SINGLE_TAIL = new IdlElementType("I_REQ_OUTPUT_VAR_SINGLE_TAIL");
  IElementType I_REQ_PARAM = new IdlElementType("I_REQ_PARAM");
  IElementType I_TAG_NAME = new IdlElementType("I_TAG_NAME");

  IElementType I_ANGLE_LEFT = new IdlElementType("<");
  IElementType I_ANGLE_RIGHT = new IdlElementType(">");
  IElementType I_AT = new IdlElementType("@");
  IElementType I_BANG = new IdlElementType("!");
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
  IElementType I_HASH = new IdlElementType("#");
  IElementType I_ID = new IdlElementType("id");
  IElementType I_IMPORT = new IdlElementType("import");
  IElementType I_META = new IdlElementType("meta");
  IElementType I_NAMESPACE = new IdlElementType("namespace");
  IElementType I_NULL = new IdlElementType("null");
  IElementType I_NUMBER = new IdlElementType("number");
  IElementType I_PARAMETERS = new IdlElementType("parameters");
  IElementType I_PAREN_LEFT = new IdlElementType("(");
  IElementType I_PAREN_RIGHT = new IdlElementType(")");
  IElementType I_PLUS = new IdlElementType("+");
  IElementType I_REQURIED = new IdlElementType("required");
  IElementType I_SEMICOLON = new IdlElementType(";");
  IElementType I_SLASH = new IdlElementType("/");
  IElementType I_STAR = new IdlElementType("*");
  IElementType I_STRING = new IdlElementType("string");
  IElementType I_TILDA = new IdlElementType("~");
  IElementType I_UNDERSCORE = new IdlElementType("_");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == I_ANNOTATION) {
        return new IdlAnnotationImpl(node);
      }
      else if (type == I_DATA) {
        return new IdlDataImpl(node);
      }
      else if (type == I_DATA_ENTRY) {
        return new IdlDataEntryImpl(node);
      }
      else if (type == I_DATA_VALUE) {
        return new IdlDataValueImpl(node);
      }
      else if (type == I_DATUM) {
        return new IdlDatumImpl(node);
      }
      else if (type == I_ENUM_DATUM) {
        return new IdlEnumDatumImpl(node);
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
      else if (type == I_LIST_DATUM) {
        return new IdlListDatumImpl(node);
      }
      else if (type == I_MAP_DATUM) {
        return new IdlMapDatumImpl(node);
      }
      else if (type == I_MAP_DATUM_ENTRY) {
        return new IdlMapDatumEntryImpl(node);
      }
      else if (type == I_NAMESPACE_DECL) {
        return new IdlNamespaceDeclImpl(node);
      }
      else if (type == I_NULL_DATUM) {
        return new IdlNullDatumImpl(node);
      }
      else if (type == I_OP_INPUT_COMA_FIELD_PROJECTION) {
        return new IdlOpInputComaFieldProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_COMA_KEY_PROJECTION) {
        return new IdlOpInputComaKeyProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_COMA_LIST_MODEL_PROJECTION) {
        return new IdlOpInputComaListModelProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_COMA_MAP_MODEL_PROJECTION) {
        return new IdlOpInputComaMapModelProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_COMA_MODEL_PROJECTION) {
        return new IdlOpInputComaModelProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_COMA_MULTI_TAG_PROJECTION) {
        return new IdlOpInputComaMultiTagProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_COMA_MULTI_TAG_PROJECTION_ITEM) {
        return new IdlOpInputComaMultiTagProjectionItemImpl(node);
      }
      else if (type == I_OP_INPUT_COMA_RECORD_MODEL_PROJECTION) {
        return new IdlOpInputComaRecordModelProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_COMA_SINGLE_TAG_PROJECTION) {
        return new IdlOpInputComaSingleTagProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_COMA_VAR_PROJECTION) {
        return new IdlOpInputComaVarProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_DEFAULT_VALUE) {
        return new IdlOpInputDefaultValueImpl(node);
      }
      else if (type == I_OP_INPUT_FIELD_PROJECTION_BODY_PART) {
        return new IdlOpInputFieldProjectionBodyPartImpl(node);
      }
      else if (type == I_OP_INPUT_MODEL_META) {
        return new IdlOpInputModelMetaImpl(node);
      }
      else if (type == I_OP_INPUT_MODEL_PROPERTY) {
        return new IdlOpInputModelPropertyImpl(node);
      }
      else if (type == I_OP_INPUT_TRUNK_FIELD_PROJECTION) {
        return new IdlOpInputTrunkFieldProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_TRUNK_MODEL_PROJECTION) {
        return new IdlOpInputTrunkModelProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_TRUNK_RECORD_MODEL_PROJECTION) {
        return new IdlOpInputTrunkRecordModelProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_TRUNK_SINGLE_TAG_PROJECTION) {
        return new IdlOpInputTrunkSingleTagProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_TRUNK_VAR_PROJECTION) {
        return new IdlOpInputTrunkVarProjectionImpl(node);
      }
      else if (type == I_OP_INPUT_VAR_MULTI_TAIL) {
        return new IdlOpInputVarMultiTailImpl(node);
      }
      else if (type == I_OP_INPUT_VAR_MULTI_TAIL_ITEM) {
        return new IdlOpInputVarMultiTailItemImpl(node);
      }
      else if (type == I_OP_INPUT_VAR_POLYMORPHIC_TAIL) {
        return new IdlOpInputVarPolymorphicTailImpl(node);
      }
      else if (type == I_OP_INPUT_VAR_SINGLE_TAIL) {
        return new IdlOpInputVarSingleTailImpl(node);
      }
      else if (type == I_OP_OUTPUT_FIELD_PROJECTION) {
        return new IdlOpOutputFieldProjectionImpl(node);
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
      else if (type == I_OP_OUTPUT_MAP_MODEL_PROJECTION) {
        return new IdlOpOutputMapModelProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_MODEL_META) {
        return new IdlOpOutputModelMetaImpl(node);
      }
      else if (type == I_OP_OUTPUT_MODEL_PROJECTION) {
        return new IdlOpOutputModelProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_MODEL_PROPERTY) {
        return new IdlOpOutputModelPropertyImpl(node);
      }
      else if (type == I_OP_OUTPUT_MULTI_TAG_PROJECTION) {
        return new IdlOpOutputMultiTagProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM) {
        return new IdlOpOutputMultiTagProjectionItemImpl(node);
      }
      else if (type == I_OP_OUTPUT_RECORD_MODEL_PROJECTION) {
        return new IdlOpOutputRecordModelProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_SINGLE_TAG_PROJECTION) {
        return new IdlOpOutputSingleTagProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_VAR_MULTI_TAIL) {
        return new IdlOpOutputVarMultiTailImpl(node);
      }
      else if (type == I_OP_OUTPUT_VAR_MULTI_TAIL_ITEM) {
        return new IdlOpOutputVarMultiTailItemImpl(node);
      }
      else if (type == I_OP_OUTPUT_VAR_POLYMORPHIC_TAIL) {
        return new IdlOpOutputVarPolymorphicTailImpl(node);
      }
      else if (type == I_OP_OUTPUT_VAR_PROJECTION) {
        return new IdlOpOutputVarProjectionImpl(node);
      }
      else if (type == I_OP_OUTPUT_VAR_SINGLE_TAIL) {
        return new IdlOpOutputVarSingleTailImpl(node);
      }
      else if (type == I_OP_PARAM) {
        return new IdlOpParamImpl(node);
      }
      else if (type == I_PRIMITIVE_DATUM) {
        return new IdlPrimitiveDatumImpl(node);
      }
      else if (type == I_QID) {
        return new IdlQidImpl(node);
      }
      else if (type == I_RECORD_DATUM) {
        return new IdlRecordDatumImpl(node);
      }
      else if (type == I_RECORD_DATUM_ENTRY) {
        return new IdlRecordDatumEntryImpl(node);
      }
      else if (type == I_REQ_ANNOTATION) {
        return new IdlReqAnnotationImpl(node);
      }
      else if (type == I_REQ_OUTPUT_COMA_FIELD_PROJECTION) {
        return new IdlReqOutputComaFieldProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_COMA_KEYS_PROJECTION) {
        return new IdlReqOutputComaKeysProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_COMA_KEY_PROJECTION) {
        return new IdlReqOutputComaKeyProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_COMA_LIST_MODEL_PROJECTION) {
        return new IdlReqOutputComaListModelProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_COMA_MAP_MODEL_PROJECTION) {
        return new IdlReqOutputComaMapModelProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_COMA_MODEL_PROJECTION) {
        return new IdlReqOutputComaModelProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION) {
        return new IdlReqOutputComaMultiTagProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION_ITEM) {
        return new IdlReqOutputComaMultiTagProjectionItemImpl(node);
      }
      else if (type == I_REQ_OUTPUT_COMA_RECORD_MODEL_PROJECTION) {
        return new IdlReqOutputComaRecordModelProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_COMA_SINGLE_TAG_PROJECTION) {
        return new IdlReqOutputComaSingleTagProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_COMA_VAR_PROJECTION) {
        return new IdlReqOutputComaVarProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_MODEL_META) {
        return new IdlReqOutputModelMetaImpl(node);
      }
      else if (type == I_REQ_OUTPUT_TRUNK_MAP_MODEL_PROJECTION) {
        return new IdlReqOutputTrunkMapModelProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_TRUNK_MODEL_PROJECTION) {
        return new IdlReqOutputTrunkModelProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_TRUNK_RECORD_MODEL_PROJECTION) {
        return new IdlReqOutputTrunkRecordModelProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_TRUNK_SINGLE_TAG_PROJECTION) {
        return new IdlReqOutputTrunkSingleTagProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_TRUNK_VAR_PROJECTION) {
        return new IdlReqOutputTrunkVarProjectionImpl(node);
      }
      else if (type == I_REQ_OUTPUT_VAR_MULTI_TAIL) {
        return new IdlReqOutputVarMultiTailImpl(node);
      }
      else if (type == I_REQ_OUTPUT_VAR_MULTI_TAIL_ITEM) {
        return new IdlReqOutputVarMultiTailItemImpl(node);
      }
      else if (type == I_REQ_OUTPUT_VAR_POLYMORPHIC_TAIL) {
        return new IdlReqOutputVarPolymorphicTailImpl(node);
      }
      else if (type == I_REQ_OUTPUT_VAR_SINGLE_TAIL) {
        return new IdlReqOutputVarSingleTailImpl(node);
      }
      else if (type == I_REQ_PARAM) {
        return new IdlReqParamImpl(node);
      }
      else if (type == I_TAG_NAME) {
        return new IdlTagNameImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
