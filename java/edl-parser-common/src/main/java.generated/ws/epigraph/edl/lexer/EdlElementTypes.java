// This is a generated file. Not intended for manual editing.
package ws.epigraph.edl.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import ws.epigraph.edl.parser.psi.stubs.EdlEnumTypeDefStubElementType;
import ws.epigraph.edl.parser.psi.stubs.EdlListTypeDefStubElementType;
import ws.epigraph.edl.parser.psi.stubs.EdlMapTypeDefStubElementType;
import ws.epigraph.edl.parser.psi.stubs.EdlNamespaceDeclStubElementType;
import ws.epigraph.edl.parser.psi.stubs.EdlPrimitiveTypeDefStubElementType;
import ws.epigraph.edl.parser.psi.stubs.EdlRecordTypeDefStubElementType;
import ws.epigraph.edl.parser.psi.stubs.EdlSupplementDefStubElementType;
import ws.epigraph.edl.parser.psi.stubs.EdlTypeDefWrapperStubElementType;
import ws.epigraph.edl.parser.psi.stubs.EdlVarTypeDefStubElementType;
import ws.epigraph.edl.parser.psi.impl.*;

public interface EdlElementTypes {

  IElementType E_ANNOTATION = new EdlElementType("E_ANNOTATION");
  IElementType E_ANON_LIST = new EdlElementType("E_ANON_LIST");
  IElementType E_ANON_MAP = new EdlElementType("E_ANON_MAP");
  IElementType E_CREATE_OPERATION_BODY_PART = new EdlElementType("E_CREATE_OPERATION_BODY_PART");
  IElementType E_CREATE_OPERATION_DEF = new EdlElementType("E_CREATE_OPERATION_DEF");
  IElementType E_CUSTOM_OPERATION_BODY_PART = new EdlElementType("E_CUSTOM_OPERATION_BODY_PART");
  IElementType E_CUSTOM_OPERATION_DEF = new EdlElementType("E_CUSTOM_OPERATION_DEF");
  IElementType E_DATA = new EdlElementType("E_DATA");
  IElementType E_DATA_ENTRY = new EdlElementType("E_DATA_ENTRY");
  IElementType E_DATA_VALUE = new EdlElementType("E_DATA_VALUE");
  IElementType E_DATUM = new EdlElementType("E_DATUM");
  IElementType E_DEFAULT_OVERRIDE = new EdlElementType("E_DEFAULT_OVERRIDE");
  IElementType E_DEFS = new EdlElementType("E_DEFS");
  IElementType E_DELETE_OPERATION_BODY_PART = new EdlElementType("E_DELETE_OPERATION_BODY_PART");
  IElementType E_DELETE_OPERATION_DEF = new EdlElementType("E_DELETE_OPERATION_DEF");
  IElementType E_ENUM_DATUM = new EdlElementType("E_ENUM_DATUM");
  IElementType E_ENUM_MEMBER_DECL = new EdlElementType("E_ENUM_MEMBER_DECL");
  IElementType E_ENUM_TYPE_BODY = new EdlElementType("E_ENUM_TYPE_BODY");
  IElementType E_ENUM_TYPE_DEF = new EdlEnumTypeDefStubElementType("E_ENUM_TYPE_DEF");
  IElementType E_EXTENDS_DECL = new EdlElementType("E_EXTENDS_DECL");
  IElementType E_FIELD_DECL = new EdlElementType("E_FIELD_DECL");
  IElementType E_IMPORTS = new EdlElementType("E_IMPORTS");
  IElementType E_IMPORT_STATEMENT = new EdlElementType("E_IMPORT_STATEMENT");
  IElementType E_LIST_DATUM = new EdlElementType("E_LIST_DATUM");
  IElementType E_LIST_TYPE_BODY = new EdlElementType("E_LIST_TYPE_BODY");
  IElementType E_LIST_TYPE_DEF = new EdlListTypeDefStubElementType("E_LIST_TYPE_DEF");
  IElementType E_MAP_DATUM = new EdlElementType("E_MAP_DATUM");
  IElementType E_MAP_DATUM_ENTRY = new EdlElementType("E_MAP_DATUM_ENTRY");
  IElementType E_MAP_TYPE_BODY = new EdlElementType("E_MAP_TYPE_BODY");
  IElementType E_MAP_TYPE_DEF = new EdlMapTypeDefStubElementType("E_MAP_TYPE_DEF");
  IElementType E_META_DECL = new EdlElementType("E_META_DECL");
  IElementType E_NAMESPACE_DECL = new EdlNamespaceDeclStubElementType("E_NAMESPACE_DECL");
  IElementType E_NULL_DATUM = new EdlElementType("E_NULL_DATUM");
  IElementType E_OPERATION_DEF = new EdlElementType("E_OPERATION_DEF");
  IElementType E_OPERATION_DELETE_PROJECTION = new EdlElementType("E_OPERATION_DELETE_PROJECTION");
  IElementType E_OPERATION_INPUT_PROJECTION = new EdlElementType("E_OPERATION_INPUT_PROJECTION");
  IElementType E_OPERATION_INPUT_TYPE = new EdlElementType("E_OPERATION_INPUT_TYPE");
  IElementType E_OPERATION_METHOD = new EdlElementType("E_OPERATION_METHOD");
  IElementType E_OPERATION_NAME = new EdlElementType("E_OPERATION_NAME");
  IElementType E_OPERATION_OUTPUT_PROJECTION = new EdlElementType("E_OPERATION_OUTPUT_PROJECTION");
  IElementType E_OPERATION_OUTPUT_TYPE = new EdlElementType("E_OPERATION_OUTPUT_TYPE");
  IElementType E_OPERATION_PATH = new EdlElementType("E_OPERATION_PATH");
  IElementType E_OP_DELETE_FIELD_PROJECTION = new EdlElementType("E_OP_DELETE_FIELD_PROJECTION");
  IElementType E_OP_DELETE_FIELD_PROJECTION_BODY_PART = new EdlElementType("E_OP_DELETE_FIELD_PROJECTION_BODY_PART");
  IElementType E_OP_DELETE_FIELD_PROJECTION_ENTRY = new EdlElementType("E_OP_DELETE_FIELD_PROJECTION_ENTRY");
  IElementType E_OP_DELETE_KEY_PROJECTION = new EdlElementType("E_OP_DELETE_KEY_PROJECTION");
  IElementType E_OP_DELETE_KEY_PROJECTION_PART = new EdlElementType("E_OP_DELETE_KEY_PROJECTION_PART");
  IElementType E_OP_DELETE_LIST_MODEL_PROJECTION = new EdlElementType("E_OP_DELETE_LIST_MODEL_PROJECTION");
  IElementType E_OP_DELETE_MAP_MODEL_PROJECTION = new EdlElementType("E_OP_DELETE_MAP_MODEL_PROJECTION");
  IElementType E_OP_DELETE_MODEL_PROJECTION = new EdlElementType("E_OP_DELETE_MODEL_PROJECTION");
  IElementType E_OP_DELETE_MODEL_PROPERTY = new EdlElementType("E_OP_DELETE_MODEL_PROPERTY");
  IElementType E_OP_DELETE_MULTI_TAG_PROJECTION = new EdlElementType("E_OP_DELETE_MULTI_TAG_PROJECTION");
  IElementType E_OP_DELETE_MULTI_TAG_PROJECTION_ITEM = new EdlElementType("E_OP_DELETE_MULTI_TAG_PROJECTION_ITEM");
  IElementType E_OP_DELETE_RECORD_MODEL_PROJECTION = new EdlElementType("E_OP_DELETE_RECORD_MODEL_PROJECTION");
  IElementType E_OP_DELETE_SINGLE_TAG_PROJECTION = new EdlElementType("E_OP_DELETE_SINGLE_TAG_PROJECTION");
  IElementType E_OP_DELETE_VAR_MULTI_TAIL = new EdlElementType("E_OP_DELETE_VAR_MULTI_TAIL");
  IElementType E_OP_DELETE_VAR_MULTI_TAIL_ITEM = new EdlElementType("E_OP_DELETE_VAR_MULTI_TAIL_ITEM");
  IElementType E_OP_DELETE_VAR_POLYMORPHIC_TAIL = new EdlElementType("E_OP_DELETE_VAR_POLYMORPHIC_TAIL");
  IElementType E_OP_DELETE_VAR_PROJECTION = new EdlElementType("E_OP_DELETE_VAR_PROJECTION");
  IElementType E_OP_DELETE_VAR_SINGLE_TAIL = new EdlElementType("E_OP_DELETE_VAR_SINGLE_TAIL");
  IElementType E_OP_FIELD_PATH = new EdlElementType("E_OP_FIELD_PATH");
  IElementType E_OP_FIELD_PATH_BODY_PART = new EdlElementType("E_OP_FIELD_PATH_BODY_PART");
  IElementType E_OP_FIELD_PATH_ENTRY = new EdlElementType("E_OP_FIELD_PATH_ENTRY");
  IElementType E_OP_INPUT_DEFAULT_VALUE = new EdlElementType("E_OP_INPUT_DEFAULT_VALUE");
  IElementType E_OP_INPUT_FIELD_PROJECTION = new EdlElementType("E_OP_INPUT_FIELD_PROJECTION");
  IElementType E_OP_INPUT_FIELD_PROJECTION_BODY_PART = new EdlElementType("E_OP_INPUT_FIELD_PROJECTION_BODY_PART");
  IElementType E_OP_INPUT_FIELD_PROJECTION_ENTRY = new EdlElementType("E_OP_INPUT_FIELD_PROJECTION_ENTRY");
  IElementType E_OP_INPUT_KEY_PROJECTION = new EdlElementType("E_OP_INPUT_KEY_PROJECTION");
  IElementType E_OP_INPUT_KEY_PROJECTION_PART = new EdlElementType("E_OP_INPUT_KEY_PROJECTION_PART");
  IElementType E_OP_INPUT_LIST_MODEL_PROJECTION = new EdlElementType("E_OP_INPUT_LIST_MODEL_PROJECTION");
  IElementType E_OP_INPUT_MAP_MODEL_PROJECTION = new EdlElementType("E_OP_INPUT_MAP_MODEL_PROJECTION");
  IElementType E_OP_INPUT_MODEL_META = new EdlElementType("E_OP_INPUT_MODEL_META");
  IElementType E_OP_INPUT_MODEL_PROJECTION = new EdlElementType("E_OP_INPUT_MODEL_PROJECTION");
  IElementType E_OP_INPUT_MODEL_PROPERTY = new EdlElementType("E_OP_INPUT_MODEL_PROPERTY");
  IElementType E_OP_INPUT_MULTI_TAG_PROJECTION = new EdlElementType("E_OP_INPUT_MULTI_TAG_PROJECTION");
  IElementType E_OP_INPUT_MULTI_TAG_PROJECTION_ITEM = new EdlElementType("E_OP_INPUT_MULTI_TAG_PROJECTION_ITEM");
  IElementType E_OP_INPUT_RECORD_MODEL_PROJECTION = new EdlElementType("E_OP_INPUT_RECORD_MODEL_PROJECTION");
  IElementType E_OP_INPUT_SINGLE_TAG_PROJECTION = new EdlElementType("E_OP_INPUT_SINGLE_TAG_PROJECTION");
  IElementType E_OP_INPUT_VAR_MULTI_TAIL = new EdlElementType("E_OP_INPUT_VAR_MULTI_TAIL");
  IElementType E_OP_INPUT_VAR_MULTI_TAIL_ITEM = new EdlElementType("E_OP_INPUT_VAR_MULTI_TAIL_ITEM");
  IElementType E_OP_INPUT_VAR_POLYMORPHIC_TAIL = new EdlElementType("E_OP_INPUT_VAR_POLYMORPHIC_TAIL");
  IElementType E_OP_INPUT_VAR_PROJECTION = new EdlElementType("E_OP_INPUT_VAR_PROJECTION");
  IElementType E_OP_INPUT_VAR_SINGLE_TAIL = new EdlElementType("E_OP_INPUT_VAR_SINGLE_TAIL");
  IElementType E_OP_MAP_MODEL_PATH = new EdlElementType("E_OP_MAP_MODEL_PATH");
  IElementType E_OP_MODEL_PATH = new EdlElementType("E_OP_MODEL_PATH");
  IElementType E_OP_MODEL_PATH_PROPERTY = new EdlElementType("E_OP_MODEL_PATH_PROPERTY");
  IElementType E_OP_OUTPUT_FIELD_PROJECTION = new EdlElementType("E_OP_OUTPUT_FIELD_PROJECTION");
  IElementType E_OP_OUTPUT_FIELD_PROJECTION_BODY_PART = new EdlElementType("E_OP_OUTPUT_FIELD_PROJECTION_BODY_PART");
  IElementType E_OP_OUTPUT_FIELD_PROJECTION_ENTRY = new EdlElementType("E_OP_OUTPUT_FIELD_PROJECTION_ENTRY");
  IElementType E_OP_OUTPUT_KEY_PROJECTION = new EdlElementType("E_OP_OUTPUT_KEY_PROJECTION");
  IElementType E_OP_OUTPUT_KEY_PROJECTION_PART = new EdlElementType("E_OP_OUTPUT_KEY_PROJECTION_PART");
  IElementType E_OP_OUTPUT_LIST_MODEL_PROJECTION = new EdlElementType("E_OP_OUTPUT_LIST_MODEL_PROJECTION");
  IElementType E_OP_OUTPUT_MAP_MODEL_PROJECTION = new EdlElementType("E_OP_OUTPUT_MAP_MODEL_PROJECTION");
  IElementType E_OP_OUTPUT_MODEL_META = new EdlElementType("E_OP_OUTPUT_MODEL_META");
  IElementType E_OP_OUTPUT_MODEL_PROJECTION = new EdlElementType("E_OP_OUTPUT_MODEL_PROJECTION");
  IElementType E_OP_OUTPUT_MODEL_PROPERTY = new EdlElementType("E_OP_OUTPUT_MODEL_PROPERTY");
  IElementType E_OP_OUTPUT_MULTI_TAG_PROJECTION = new EdlElementType("E_OP_OUTPUT_MULTI_TAG_PROJECTION");
  IElementType E_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM = new EdlElementType("E_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM");
  IElementType E_OP_OUTPUT_RECORD_MODEL_PROJECTION = new EdlElementType("E_OP_OUTPUT_RECORD_MODEL_PROJECTION");
  IElementType E_OP_OUTPUT_SINGLE_TAG_PROJECTION = new EdlElementType("E_OP_OUTPUT_SINGLE_TAG_PROJECTION");
  IElementType E_OP_OUTPUT_VAR_MULTI_TAIL = new EdlElementType("E_OP_OUTPUT_VAR_MULTI_TAIL");
  IElementType E_OP_OUTPUT_VAR_MULTI_TAIL_ITEM = new EdlElementType("E_OP_OUTPUT_VAR_MULTI_TAIL_ITEM");
  IElementType E_OP_OUTPUT_VAR_POLYMORPHIC_TAIL = new EdlElementType("E_OP_OUTPUT_VAR_POLYMORPHIC_TAIL");
  IElementType E_OP_OUTPUT_VAR_PROJECTION = new EdlElementType("E_OP_OUTPUT_VAR_PROJECTION");
  IElementType E_OP_OUTPUT_VAR_SINGLE_TAIL = new EdlElementType("E_OP_OUTPUT_VAR_SINGLE_TAIL");
  IElementType E_OP_PARAM = new EdlElementType("E_OP_PARAM");
  IElementType E_OP_PATH_KEY_PROJECTION = new EdlElementType("E_OP_PATH_KEY_PROJECTION");
  IElementType E_OP_PATH_KEY_PROJECTION_BODY = new EdlElementType("E_OP_PATH_KEY_PROJECTION_BODY");
  IElementType E_OP_PATH_KEY_PROJECTION_PART = new EdlElementType("E_OP_PATH_KEY_PROJECTION_PART");
  IElementType E_OP_RECORD_MODEL_PATH = new EdlElementType("E_OP_RECORD_MODEL_PATH");
  IElementType E_OP_VAR_PATH = new EdlElementType("E_OP_VAR_PATH");
  IElementType E_PRIMITIVE_DATUM = new EdlElementType("E_PRIMITIVE_DATUM");
  IElementType E_PRIMITIVE_TYPE_BODY = new EdlElementType("E_PRIMITIVE_TYPE_BODY");
  IElementType E_PRIMITIVE_TYPE_DEF = new EdlPrimitiveTypeDefStubElementType("E_PRIMITIVE_TYPE_DEF");
  IElementType E_QID = new EdlElementType("E_QID");
  IElementType E_QN = new EdlElementType("E_QN");
  IElementType E_QN_SEGMENT = new EdlElementType("E_QN_SEGMENT");
  IElementType E_QN_TYPE_REF = new EdlElementType("E_QN_TYPE_REF");
  IElementType E_READ_OPERATION_BODY_PART = new EdlElementType("E_READ_OPERATION_BODY_PART");
  IElementType E_READ_OPERATION_DEF = new EdlElementType("E_READ_OPERATION_DEF");
  IElementType E_RECORD_DATUM = new EdlElementType("E_RECORD_DATUM");
  IElementType E_RECORD_DATUM_ENTRY = new EdlElementType("E_RECORD_DATUM_ENTRY");
  IElementType E_RECORD_TYPE_BODY = new EdlElementType("E_RECORD_TYPE_BODY");
  IElementType E_RECORD_TYPE_DEF = new EdlRecordTypeDefStubElementType("E_RECORD_TYPE_DEF");
  IElementType E_RESOURCE_DEF = new EdlElementType("E_RESOURCE_DEF");
  IElementType E_RESOURCE_NAME = new EdlElementType("E_RESOURCE_NAME");
  IElementType E_RESOURCE_TYPE = new EdlElementType("E_RESOURCE_TYPE");
  IElementType E_SUPPLEMENTS_DECL = new EdlElementType("E_SUPPLEMENTS_DECL");
  IElementType E_SUPPLEMENT_DEF = new EdlSupplementDefStubElementType("E_SUPPLEMENT_DEF");
  IElementType E_TAG_NAME = new EdlElementType("E_TAG_NAME");
  IElementType E_TYPE_DEF_WRAPPER = new EdlTypeDefWrapperStubElementType("E_TYPE_DEF_WRAPPER");
  IElementType E_TYPE_REF = new EdlElementType("E_TYPE_REF");
  IElementType E_UPDATE_OPERATION_BODY_PART = new EdlElementType("E_UPDATE_OPERATION_BODY_PART");
  IElementType E_UPDATE_OPERATION_DEF = new EdlElementType("E_UPDATE_OPERATION_DEF");
  IElementType E_VALUE_TYPE_REF = new EdlElementType("E_VALUE_TYPE_REF");
  IElementType E_VAR_TAG_DECL = new EdlElementType("E_VAR_TAG_DECL");
  IElementType E_VAR_TAG_REF = new EdlElementType("E_VAR_TAG_REF");
  IElementType E_VAR_TYPE_BODY = new EdlElementType("E_VAR_TYPE_BODY");
  IElementType E_VAR_TYPE_DEF = new EdlVarTypeDefStubElementType("E_VAR_TYPE_DEF");

  IElementType E_ABSTRACT = new EdlElementType("abstract");
  IElementType E_ANGLE_LEFT = new EdlElementType("<");
  IElementType E_ANGLE_RIGHT = new EdlElementType(">");
  IElementType E_AT = new EdlElementType("@");
  IElementType E_BANG = new EdlElementType("!");
  IElementType E_BLOCK_COMMENT = new EdlElementType("block_comment");
  IElementType E_BOOLEAN = new EdlElementType("boolean");
  IElementType E_BOOLEAN_T = new EdlElementType("boolean");
  IElementType E_BRACKET_LEFT = new EdlElementType("[");
  IElementType E_BRACKET_RIGHT = new EdlElementType("]");
  IElementType E_COLON = new EdlElementType(":");
  IElementType E_COMMA = new EdlElementType(",");
  IElementType E_COMMENT = new EdlElementType("comment");
  IElementType E_CREATE = new EdlElementType("CREATE");
  IElementType E_CURLY_LEFT = new EdlElementType("{");
  IElementType E_CURLY_RIGHT = new EdlElementType("}");
  IElementType E_CUSTOM = new EdlElementType("CUSTOM");
  IElementType E_DEFAULT = new EdlElementType("default");
  IElementType E_DELETE = new EdlElementType("DELETE");
  IElementType E_DELETE_PROJECTION = new EdlElementType("deleteProjection");
  IElementType E_DOT = new EdlElementType(".");
  IElementType E_DOUBLE_T = new EdlElementType("double");
  IElementType E_ENUM = new EdlElementType("enum");
  IElementType E_EQ = new EdlElementType("=");
  IElementType E_EXTENDS = new EdlElementType("extends");
  IElementType E_FORBIDDEN = new EdlElementType("forbidden");
  IElementType E_GET = new EdlElementType("GET");
  IElementType E_HASH = new EdlElementType("#");
  IElementType E_ID = new EdlElementType("id");
  IElementType E_IMPORT = new EdlElementType("import");
  IElementType E_INPUT_PROJECTION = new EdlElementType("inputProjection");
  IElementType E_INPUT_TYPE = new EdlElementType("inputType");
  IElementType E_INTEGER_T = new EdlElementType("integer");
  IElementType E_LIST = new EdlElementType("list");
  IElementType E_LONG_T = new EdlElementType("long");
  IElementType E_MAP = new EdlElementType("map");
  IElementType E_META = new EdlElementType("meta");
  IElementType E_METHOD = new EdlElementType("method");
  IElementType E_NAMESPACE = new EdlElementType("namespace");
  IElementType E_NODEFAULT = new EdlElementType("nodefault");
  IElementType E_NULL = new EdlElementType("null");
  IElementType E_NUMBER = new EdlElementType("number");
  IElementType E_OUTPUT_PROJECTION = new EdlElementType("outputProjection");
  IElementType E_OUTPUT_TYPE = new EdlElementType("outputType");
  IElementType E_OVERRIDE = new EdlElementType("override");
  IElementType E_PAREN_LEFT = new EdlElementType("(");
  IElementType E_PAREN_RIGHT = new EdlElementType(")");
  IElementType E_PATH = new EdlElementType("path");
  IElementType E_PLUS = new EdlElementType("+");
  IElementType E_POST = new EdlElementType("POST");
  IElementType E_PUT = new EdlElementType("PUT");
  IElementType E_READ = new EdlElementType("READ");
  IElementType E_RECORD = new EdlElementType("record");
  IElementType E_REQUIRED = new EdlElementType("required");
  IElementType E_RESOURCE = new EdlElementType("resource");
  IElementType E_SEMICOLON = new EdlElementType(";");
  IElementType E_SLASH = new EdlElementType("/");
  IElementType E_STAR = new EdlElementType("*");
  IElementType E_STRING = new EdlElementType("string");
  IElementType E_STRING_T = new EdlElementType("string");
  IElementType E_SUPPLEMENT = new EdlElementType("supplement");
  IElementType E_SUPPLEMENTS = new EdlElementType("supplements");
  IElementType E_TILDA = new EdlElementType("~");
  IElementType E_UNDERSCORE = new EdlElementType("_");
  IElementType E_UPDATE = new EdlElementType("UPDATE");
  IElementType E_VARTYPE = new EdlElementType("vartype");
  IElementType E_WITH = new EdlElementType("with");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == E_ANNOTATION) {
        return new EdlAnnotationImpl(node);
      }
      else if (type == E_ANON_LIST) {
        return new EdlAnonListImpl(node);
      }
      else if (type == E_ANON_MAP) {
        return new EdlAnonMapImpl(node);
      }
      else if (type == E_CREATE_OPERATION_BODY_PART) {
        return new EdlCreateOperationBodyPartImpl(node);
      }
      else if (type == E_CREATE_OPERATION_DEF) {
        return new EdlCreateOperationDefImpl(node);
      }
      else if (type == E_CUSTOM_OPERATION_BODY_PART) {
        return new EdlCustomOperationBodyPartImpl(node);
      }
      else if (type == E_CUSTOM_OPERATION_DEF) {
        return new EdlCustomOperationDefImpl(node);
      }
      else if (type == E_DATA) {
        return new EdlDataImpl(node);
      }
      else if (type == E_DATA_ENTRY) {
        return new EdlDataEntryImpl(node);
      }
      else if (type == E_DATA_VALUE) {
        return new EdlDataValueImpl(node);
      }
      else if (type == E_DEFAULT_OVERRIDE) {
        return new EdlDefaultOverrideImpl(node);
      }
      else if (type == E_DEFS) {
        return new EdlDefsImpl(node);
      }
      else if (type == E_DELETE_OPERATION_BODY_PART) {
        return new EdlDeleteOperationBodyPartImpl(node);
      }
      else if (type == E_DELETE_OPERATION_DEF) {
        return new EdlDeleteOperationDefImpl(node);
      }
      else if (type == E_ENUM_DATUM) {
        return new EdlEnumDatumImpl(node);
      }
      else if (type == E_ENUM_MEMBER_DECL) {
        return new EdlEnumMemberDeclImpl(node);
      }
      else if (type == E_ENUM_TYPE_BODY) {
        return new EdlEnumTypeBodyImpl(node);
      }
      else if (type == E_ENUM_TYPE_DEF) {
        return new EdlEnumTypeDefImpl(node);
      }
      else if (type == E_EXTENDS_DECL) {
        return new EdlExtendsDeclImpl(node);
      }
      else if (type == E_FIELD_DECL) {
        return new EdlFieldDeclImpl(node);
      }
      else if (type == E_IMPORTS) {
        return new EdlImportsImpl(node);
      }
      else if (type == E_IMPORT_STATEMENT) {
        return new EdlImportStatementImpl(node);
      }
      else if (type == E_LIST_DATUM) {
        return new EdlListDatumImpl(node);
      }
      else if (type == E_LIST_TYPE_BODY) {
        return new EdlListTypeBodyImpl(node);
      }
      else if (type == E_LIST_TYPE_DEF) {
        return new EdlListTypeDefImpl(node);
      }
      else if (type == E_MAP_DATUM) {
        return new EdlMapDatumImpl(node);
      }
      else if (type == E_MAP_DATUM_ENTRY) {
        return new EdlMapDatumEntryImpl(node);
      }
      else if (type == E_MAP_TYPE_BODY) {
        return new EdlMapTypeBodyImpl(node);
      }
      else if (type == E_MAP_TYPE_DEF) {
        return new EdlMapTypeDefImpl(node);
      }
      else if (type == E_META_DECL) {
        return new EdlMetaDeclImpl(node);
      }
      else if (type == E_NAMESPACE_DECL) {
        return new EdlNamespaceDeclImpl(node);
      }
      else if (type == E_NULL_DATUM) {
        return new EdlNullDatumImpl(node);
      }
      else if (type == E_OPERATION_DEF) {
        return new EdlOperationDefImpl(node);
      }
      else if (type == E_OPERATION_DELETE_PROJECTION) {
        return new EdlOperationDeleteProjectionImpl(node);
      }
      else if (type == E_OPERATION_INPUT_PROJECTION) {
        return new EdlOperationInputProjectionImpl(node);
      }
      else if (type == E_OPERATION_INPUT_TYPE) {
        return new EdlOperationInputTypeImpl(node);
      }
      else if (type == E_OPERATION_METHOD) {
        return new EdlOperationMethodImpl(node);
      }
      else if (type == E_OPERATION_NAME) {
        return new EdlOperationNameImpl(node);
      }
      else if (type == E_OPERATION_OUTPUT_PROJECTION) {
        return new EdlOperationOutputProjectionImpl(node);
      }
      else if (type == E_OPERATION_OUTPUT_TYPE) {
        return new EdlOperationOutputTypeImpl(node);
      }
      else if (type == E_OPERATION_PATH) {
        return new EdlOperationPathImpl(node);
      }
      else if (type == E_OP_DELETE_FIELD_PROJECTION) {
        return new EdlOpDeleteFieldProjectionImpl(node);
      }
      else if (type == E_OP_DELETE_FIELD_PROJECTION_BODY_PART) {
        return new EdlOpDeleteFieldProjectionBodyPartImpl(node);
      }
      else if (type == E_OP_DELETE_FIELD_PROJECTION_ENTRY) {
        return new EdlOpDeleteFieldProjectionEntryImpl(node);
      }
      else if (type == E_OP_DELETE_KEY_PROJECTION) {
        return new EdlOpDeleteKeyProjectionImpl(node);
      }
      else if (type == E_OP_DELETE_KEY_PROJECTION_PART) {
        return new EdlOpDeleteKeyProjectionPartImpl(node);
      }
      else if (type == E_OP_DELETE_LIST_MODEL_PROJECTION) {
        return new EdlOpDeleteListModelProjectionImpl(node);
      }
      else if (type == E_OP_DELETE_MAP_MODEL_PROJECTION) {
        return new EdlOpDeleteMapModelProjectionImpl(node);
      }
      else if (type == E_OP_DELETE_MODEL_PROJECTION) {
        return new EdlOpDeleteModelProjectionImpl(node);
      }
      else if (type == E_OP_DELETE_MODEL_PROPERTY) {
        return new EdlOpDeleteModelPropertyImpl(node);
      }
      else if (type == E_OP_DELETE_MULTI_TAG_PROJECTION) {
        return new EdlOpDeleteMultiTagProjectionImpl(node);
      }
      else if (type == E_OP_DELETE_MULTI_TAG_PROJECTION_ITEM) {
        return new EdlOpDeleteMultiTagProjectionItemImpl(node);
      }
      else if (type == E_OP_DELETE_RECORD_MODEL_PROJECTION) {
        return new EdlOpDeleteRecordModelProjectionImpl(node);
      }
      else if (type == E_OP_DELETE_SINGLE_TAG_PROJECTION) {
        return new EdlOpDeleteSingleTagProjectionImpl(node);
      }
      else if (type == E_OP_DELETE_VAR_MULTI_TAIL) {
        return new EdlOpDeleteVarMultiTailImpl(node);
      }
      else if (type == E_OP_DELETE_VAR_MULTI_TAIL_ITEM) {
        return new EdlOpDeleteVarMultiTailItemImpl(node);
      }
      else if (type == E_OP_DELETE_VAR_POLYMORPHIC_TAIL) {
        return new EdlOpDeleteVarPolymorphicTailImpl(node);
      }
      else if (type == E_OP_DELETE_VAR_PROJECTION) {
        return new EdlOpDeleteVarProjectionImpl(node);
      }
      else if (type == E_OP_DELETE_VAR_SINGLE_TAIL) {
        return new EdlOpDeleteVarSingleTailImpl(node);
      }
      else if (type == E_OP_FIELD_PATH) {
        return new EdlOpFieldPathImpl(node);
      }
      else if (type == E_OP_FIELD_PATH_BODY_PART) {
        return new EdlOpFieldPathBodyPartImpl(node);
      }
      else if (type == E_OP_FIELD_PATH_ENTRY) {
        return new EdlOpFieldPathEntryImpl(node);
      }
      else if (type == E_OP_INPUT_DEFAULT_VALUE) {
        return new EdlOpInputDefaultValueImpl(node);
      }
      else if (type == E_OP_INPUT_FIELD_PROJECTION) {
        return new EdlOpInputFieldProjectionImpl(node);
      }
      else if (type == E_OP_INPUT_FIELD_PROJECTION_BODY_PART) {
        return new EdlOpInputFieldProjectionBodyPartImpl(node);
      }
      else if (type == E_OP_INPUT_FIELD_PROJECTION_ENTRY) {
        return new EdlOpInputFieldProjectionEntryImpl(node);
      }
      else if (type == E_OP_INPUT_KEY_PROJECTION) {
        return new EdlOpInputKeyProjectionImpl(node);
      }
      else if (type == E_OP_INPUT_KEY_PROJECTION_PART) {
        return new EdlOpInputKeyProjectionPartImpl(node);
      }
      else if (type == E_OP_INPUT_LIST_MODEL_PROJECTION) {
        return new EdlOpInputListModelProjectionImpl(node);
      }
      else if (type == E_OP_INPUT_MAP_MODEL_PROJECTION) {
        return new EdlOpInputMapModelProjectionImpl(node);
      }
      else if (type == E_OP_INPUT_MODEL_META) {
        return new EdlOpInputModelMetaImpl(node);
      }
      else if (type == E_OP_INPUT_MODEL_PROJECTION) {
        return new EdlOpInputModelProjectionImpl(node);
      }
      else if (type == E_OP_INPUT_MODEL_PROPERTY) {
        return new EdlOpInputModelPropertyImpl(node);
      }
      else if (type == E_OP_INPUT_MULTI_TAG_PROJECTION) {
        return new EdlOpInputMultiTagProjectionImpl(node);
      }
      else if (type == E_OP_INPUT_MULTI_TAG_PROJECTION_ITEM) {
        return new EdlOpInputMultiTagProjectionItemImpl(node);
      }
      else if (type == E_OP_INPUT_RECORD_MODEL_PROJECTION) {
        return new EdlOpInputRecordModelProjectionImpl(node);
      }
      else if (type == E_OP_INPUT_SINGLE_TAG_PROJECTION) {
        return new EdlOpInputSingleTagProjectionImpl(node);
      }
      else if (type == E_OP_INPUT_VAR_MULTI_TAIL) {
        return new EdlOpInputVarMultiTailImpl(node);
      }
      else if (type == E_OP_INPUT_VAR_MULTI_TAIL_ITEM) {
        return new EdlOpInputVarMultiTailItemImpl(node);
      }
      else if (type == E_OP_INPUT_VAR_POLYMORPHIC_TAIL) {
        return new EdlOpInputVarPolymorphicTailImpl(node);
      }
      else if (type == E_OP_INPUT_VAR_PROJECTION) {
        return new EdlOpInputVarProjectionImpl(node);
      }
      else if (type == E_OP_INPUT_VAR_SINGLE_TAIL) {
        return new EdlOpInputVarSingleTailImpl(node);
      }
      else if (type == E_OP_MAP_MODEL_PATH) {
        return new EdlOpMapModelPathImpl(node);
      }
      else if (type == E_OP_MODEL_PATH) {
        return new EdlOpModelPathImpl(node);
      }
      else if (type == E_OP_MODEL_PATH_PROPERTY) {
        return new EdlOpModelPathPropertyImpl(node);
      }
      else if (type == E_OP_OUTPUT_FIELD_PROJECTION) {
        return new EdlOpOutputFieldProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_FIELD_PROJECTION_BODY_PART) {
        return new EdlOpOutputFieldProjectionBodyPartImpl(node);
      }
      else if (type == E_OP_OUTPUT_FIELD_PROJECTION_ENTRY) {
        return new EdlOpOutputFieldProjectionEntryImpl(node);
      }
      else if (type == E_OP_OUTPUT_KEY_PROJECTION) {
        return new EdlOpOutputKeyProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_KEY_PROJECTION_PART) {
        return new EdlOpOutputKeyProjectionPartImpl(node);
      }
      else if (type == E_OP_OUTPUT_LIST_MODEL_PROJECTION) {
        return new EdlOpOutputListModelProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_MAP_MODEL_PROJECTION) {
        return new EdlOpOutputMapModelProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_MODEL_META) {
        return new EdlOpOutputModelMetaImpl(node);
      }
      else if (type == E_OP_OUTPUT_MODEL_PROJECTION) {
        return new EdlOpOutputModelProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_MODEL_PROPERTY) {
        return new EdlOpOutputModelPropertyImpl(node);
      }
      else if (type == E_OP_OUTPUT_MULTI_TAG_PROJECTION) {
        return new EdlOpOutputMultiTagProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM) {
        return new EdlOpOutputMultiTagProjectionItemImpl(node);
      }
      else if (type == E_OP_OUTPUT_RECORD_MODEL_PROJECTION) {
        return new EdlOpOutputRecordModelProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_SINGLE_TAG_PROJECTION) {
        return new EdlOpOutputSingleTagProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_VAR_MULTI_TAIL) {
        return new EdlOpOutputVarMultiTailImpl(node);
      }
      else if (type == E_OP_OUTPUT_VAR_MULTI_TAIL_ITEM) {
        return new EdlOpOutputVarMultiTailItemImpl(node);
      }
      else if (type == E_OP_OUTPUT_VAR_POLYMORPHIC_TAIL) {
        return new EdlOpOutputVarPolymorphicTailImpl(node);
      }
      else if (type == E_OP_OUTPUT_VAR_PROJECTION) {
        return new EdlOpOutputVarProjectionImpl(node);
      }
      else if (type == E_OP_OUTPUT_VAR_SINGLE_TAIL) {
        return new EdlOpOutputVarSingleTailImpl(node);
      }
      else if (type == E_OP_PARAM) {
        return new EdlOpParamImpl(node);
      }
      else if (type == E_OP_PATH_KEY_PROJECTION) {
        return new EdlOpPathKeyProjectionImpl(node);
      }
      else if (type == E_OP_PATH_KEY_PROJECTION_BODY) {
        return new EdlOpPathKeyProjectionBodyImpl(node);
      }
      else if (type == E_OP_PATH_KEY_PROJECTION_PART) {
        return new EdlOpPathKeyProjectionPartImpl(node);
      }
      else if (type == E_OP_RECORD_MODEL_PATH) {
        return new EdlOpRecordModelPathImpl(node);
      }
      else if (type == E_OP_VAR_PATH) {
        return new EdlOpVarPathImpl(node);
      }
      else if (type == E_PRIMITIVE_DATUM) {
        return new EdlPrimitiveDatumImpl(node);
      }
      else if (type == E_PRIMITIVE_TYPE_BODY) {
        return new EdlPrimitiveTypeBodyImpl(node);
      }
      else if (type == E_PRIMITIVE_TYPE_DEF) {
        return new EdlPrimitiveTypeDefImpl(node);
      }
      else if (type == E_QID) {
        return new EdlQidImpl(node);
      }
      else if (type == E_QN) {
        return new EdlQnImpl(node);
      }
      else if (type == E_QN_SEGMENT) {
        return new EdlQnSegmentImpl(node);
      }
      else if (type == E_QN_TYPE_REF) {
        return new EdlQnTypeRefImpl(node);
      }
      else if (type == E_READ_OPERATION_BODY_PART) {
        return new EdlReadOperationBodyPartImpl(node);
      }
      else if (type == E_READ_OPERATION_DEF) {
        return new EdlReadOperationDefImpl(node);
      }
      else if (type == E_RECORD_DATUM) {
        return new EdlRecordDatumImpl(node);
      }
      else if (type == E_RECORD_DATUM_ENTRY) {
        return new EdlRecordDatumEntryImpl(node);
      }
      else if (type == E_RECORD_TYPE_BODY) {
        return new EdlRecordTypeBodyImpl(node);
      }
      else if (type == E_RECORD_TYPE_DEF) {
        return new EdlRecordTypeDefImpl(node);
      }
      else if (type == E_RESOURCE_DEF) {
        return new EdlResourceDefImpl(node);
      }
      else if (type == E_RESOURCE_NAME) {
        return new EdlResourceNameImpl(node);
      }
      else if (type == E_RESOURCE_TYPE) {
        return new EdlResourceTypeImpl(node);
      }
      else if (type == E_SUPPLEMENTS_DECL) {
        return new EdlSupplementsDeclImpl(node);
      }
      else if (type == E_SUPPLEMENT_DEF) {
        return new EdlSupplementDefImpl(node);
      }
      else if (type == E_TAG_NAME) {
        return new EdlTagNameImpl(node);
      }
      else if (type == E_TYPE_DEF_WRAPPER) {
        return new EdlTypeDefWrapperImpl(node);
      }
      else if (type == E_UPDATE_OPERATION_BODY_PART) {
        return new EdlUpdateOperationBodyPartImpl(node);
      }
      else if (type == E_UPDATE_OPERATION_DEF) {
        return new EdlUpdateOperationDefImpl(node);
      }
      else if (type == E_VALUE_TYPE_REF) {
        return new EdlValueTypeRefImpl(node);
      }
      else if (type == E_VAR_TAG_DECL) {
        return new EdlVarTagDeclImpl(node);
      }
      else if (type == E_VAR_TAG_REF) {
        return new EdlVarTagRefImpl(node);
      }
      else if (type == E_VAR_TYPE_BODY) {
        return new EdlVarTypeBodyImpl(node);
      }
      else if (type == E_VAR_TYPE_DEF) {
        return new EdlVarTypeDefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
