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

  IElementType S_ANNOTATION = new EdlElementType("S_ANNOTATION");
  IElementType S_ANON_LIST = new EdlElementType("S_ANON_LIST");
  IElementType S_ANON_MAP = new EdlElementType("S_ANON_MAP");
  IElementType S_CREATE_OPERATION_BODY_PART = new EdlElementType("S_CREATE_OPERATION_BODY_PART");
  IElementType S_CREATE_OPERATION_DEF = new EdlElementType("S_CREATE_OPERATION_DEF");
  IElementType S_CUSTOM_OPERATION_BODY_PART = new EdlElementType("S_CUSTOM_OPERATION_BODY_PART");
  IElementType S_CUSTOM_OPERATION_DEF = new EdlElementType("S_CUSTOM_OPERATION_DEF");
  IElementType S_DATA = new EdlElementType("S_DATA");
  IElementType S_DATA_ENTRY = new EdlElementType("S_DATA_ENTRY");
  IElementType S_DATA_VALUE = new EdlElementType("S_DATA_VALUE");
  IElementType S_DATUM = new EdlElementType("S_DATUM");
  IElementType S_DEFAULT_OVERRIDE = new EdlElementType("S_DEFAULT_OVERRIDE");
  IElementType S_DEFS = new EdlElementType("S_DEFS");
  IElementType S_DELETE_OPERATION_BODY_PART = new EdlElementType("S_DELETE_OPERATION_BODY_PART");
  IElementType S_DELETE_OPERATION_DEF = new EdlElementType("S_DELETE_OPERATION_DEF");
  IElementType S_ENUM_DATUM = new EdlElementType("S_ENUM_DATUM");
  IElementType S_ENUM_MEMBER_DECL = new EdlElementType("S_ENUM_MEMBER_DECL");
  IElementType S_ENUM_TYPE_BODY = new EdlElementType("S_ENUM_TYPE_BODY");
  IElementType S_ENUM_TYPE_DEF = new EdlEnumTypeDefStubElementType("S_ENUM_TYPE_DEF");
  IElementType S_EXTENDS_DECL = new EdlElementType("S_EXTENDS_DECL");
  IElementType S_FIELD_DECL = new EdlElementType("S_FIELD_DECL");
  IElementType S_IMPORTS = new EdlElementType("S_IMPORTS");
  IElementType S_IMPORT_STATEMENT = new EdlElementType("S_IMPORT_STATEMENT");
  IElementType S_LIST_DATUM = new EdlElementType("S_LIST_DATUM");
  IElementType S_LIST_TYPE_BODY = new EdlElementType("S_LIST_TYPE_BODY");
  IElementType S_LIST_TYPE_DEF = new EdlListTypeDefStubElementType("S_LIST_TYPE_DEF");
  IElementType S_MAP_DATUM = new EdlElementType("S_MAP_DATUM");
  IElementType S_MAP_DATUM_ENTRY = new EdlElementType("S_MAP_DATUM_ENTRY");
  IElementType S_MAP_TYPE_BODY = new EdlElementType("S_MAP_TYPE_BODY");
  IElementType S_MAP_TYPE_DEF = new EdlMapTypeDefStubElementType("S_MAP_TYPE_DEF");
  IElementType S_META_DECL = new EdlElementType("S_META_DECL");
  IElementType S_NAMESPACE_DECL = new EdlNamespaceDeclStubElementType("S_NAMESPACE_DECL");
  IElementType S_NULL_DATUM = new EdlElementType("S_NULL_DATUM");
  IElementType S_OPERATION_DEF = new EdlElementType("S_OPERATION_DEF");
  IElementType S_OPERATION_DELETE_PROJECTION = new EdlElementType("S_OPERATION_DELETE_PROJECTION");
  IElementType S_OPERATION_INPUT_PROJECTION = new EdlElementType("S_OPERATION_INPUT_PROJECTION");
  IElementType S_OPERATION_INPUT_TYPE = new EdlElementType("S_OPERATION_INPUT_TYPE");
  IElementType S_OPERATION_METHOD = new EdlElementType("S_OPERATION_METHOD");
  IElementType S_OPERATION_NAME = new EdlElementType("S_OPERATION_NAME");
  IElementType S_OPERATION_OUTPUT_PROJECTION = new EdlElementType("S_OPERATION_OUTPUT_PROJECTION");
  IElementType S_OPERATION_OUTPUT_TYPE = new EdlElementType("S_OPERATION_OUTPUT_TYPE");
  IElementType S_OPERATION_PATH = new EdlElementType("S_OPERATION_PATH");
  IElementType S_OP_DELETE_FIELD_PROJECTION = new EdlElementType("S_OP_DELETE_FIELD_PROJECTION");
  IElementType S_OP_DELETE_FIELD_PROJECTION_BODY_PART = new EdlElementType("S_OP_DELETE_FIELD_PROJECTION_BODY_PART");
  IElementType S_OP_DELETE_FIELD_PROJECTION_ENTRY = new EdlElementType("S_OP_DELETE_FIELD_PROJECTION_ENTRY");
  IElementType S_OP_DELETE_KEY_PROJECTION = new EdlElementType("S_OP_DELETE_KEY_PROJECTION");
  IElementType S_OP_DELETE_KEY_PROJECTION_PART = new EdlElementType("S_OP_DELETE_KEY_PROJECTION_PART");
  IElementType S_OP_DELETE_LIST_MODEL_PROJECTION = new EdlElementType("S_OP_DELETE_LIST_MODEL_PROJECTION");
  IElementType S_OP_DELETE_MAP_MODEL_PROJECTION = new EdlElementType("S_OP_DELETE_MAP_MODEL_PROJECTION");
  IElementType S_OP_DELETE_MODEL_PROJECTION = new EdlElementType("S_OP_DELETE_MODEL_PROJECTION");
  IElementType S_OP_DELETE_MODEL_PROPERTY = new EdlElementType("S_OP_DELETE_MODEL_PROPERTY");
  IElementType S_OP_DELETE_MULTI_TAG_PROJECTION = new EdlElementType("S_OP_DELETE_MULTI_TAG_PROJECTION");
  IElementType S_OP_DELETE_MULTI_TAG_PROJECTION_ITEM = new EdlElementType("S_OP_DELETE_MULTI_TAG_PROJECTION_ITEM");
  IElementType S_OP_DELETE_RECORD_MODEL_PROJECTION = new EdlElementType("S_OP_DELETE_RECORD_MODEL_PROJECTION");
  IElementType S_OP_DELETE_SINGLE_TAG_PROJECTION = new EdlElementType("S_OP_DELETE_SINGLE_TAG_PROJECTION");
  IElementType S_OP_DELETE_VAR_MULTI_TAIL = new EdlElementType("S_OP_DELETE_VAR_MULTI_TAIL");
  IElementType S_OP_DELETE_VAR_MULTI_TAIL_ITEM = new EdlElementType("S_OP_DELETE_VAR_MULTI_TAIL_ITEM");
  IElementType S_OP_DELETE_VAR_POLYMORPHIC_TAIL = new EdlElementType("S_OP_DELETE_VAR_POLYMORPHIC_TAIL");
  IElementType S_OP_DELETE_VAR_PROJECTION = new EdlElementType("S_OP_DELETE_VAR_PROJECTION");
  IElementType S_OP_DELETE_VAR_SINGLE_TAIL = new EdlElementType("S_OP_DELETE_VAR_SINGLE_TAIL");
  IElementType S_OP_FIELD_PATH = new EdlElementType("S_OP_FIELD_PATH");
  IElementType S_OP_FIELD_PATH_BODY_PART = new EdlElementType("S_OP_FIELD_PATH_BODY_PART");
  IElementType S_OP_FIELD_PATH_ENTRY = new EdlElementType("S_OP_FIELD_PATH_ENTRY");
  IElementType S_OP_INPUT_DEFAULT_VALUE = new EdlElementType("S_OP_INPUT_DEFAULT_VALUE");
  IElementType S_OP_INPUT_FIELD_PROJECTION = new EdlElementType("S_OP_INPUT_FIELD_PROJECTION");
  IElementType S_OP_INPUT_FIELD_PROJECTION_BODY_PART = new EdlElementType("S_OP_INPUT_FIELD_PROJECTION_BODY_PART");
  IElementType S_OP_INPUT_FIELD_PROJECTION_ENTRY = new EdlElementType("S_OP_INPUT_FIELD_PROJECTION_ENTRY");
  IElementType S_OP_INPUT_KEY_PROJECTION = new EdlElementType("S_OP_INPUT_KEY_PROJECTION");
  IElementType S_OP_INPUT_KEY_PROJECTION_PART = new EdlElementType("S_OP_INPUT_KEY_PROJECTION_PART");
  IElementType S_OP_INPUT_LIST_MODEL_PROJECTION = new EdlElementType("S_OP_INPUT_LIST_MODEL_PROJECTION");
  IElementType S_OP_INPUT_MAP_MODEL_PROJECTION = new EdlElementType("S_OP_INPUT_MAP_MODEL_PROJECTION");
  IElementType S_OP_INPUT_MODEL_META = new EdlElementType("S_OP_INPUT_MODEL_META");
  IElementType S_OP_INPUT_MODEL_PROJECTION = new EdlElementType("S_OP_INPUT_MODEL_PROJECTION");
  IElementType S_OP_INPUT_MODEL_PROPERTY = new EdlElementType("S_OP_INPUT_MODEL_PROPERTY");
  IElementType S_OP_INPUT_MULTI_TAG_PROJECTION = new EdlElementType("S_OP_INPUT_MULTI_TAG_PROJECTION");
  IElementType S_OP_INPUT_MULTI_TAG_PROJECTION_ITEM = new EdlElementType("S_OP_INPUT_MULTI_TAG_PROJECTION_ITEM");
  IElementType S_OP_INPUT_RECORD_MODEL_PROJECTION = new EdlElementType("S_OP_INPUT_RECORD_MODEL_PROJECTION");
  IElementType S_OP_INPUT_SINGLE_TAG_PROJECTION = new EdlElementType("S_OP_INPUT_SINGLE_TAG_PROJECTION");
  IElementType S_OP_INPUT_VAR_MULTI_TAIL = new EdlElementType("S_OP_INPUT_VAR_MULTI_TAIL");
  IElementType S_OP_INPUT_VAR_MULTI_TAIL_ITEM = new EdlElementType("S_OP_INPUT_VAR_MULTI_TAIL_ITEM");
  IElementType S_OP_INPUT_VAR_POLYMORPHIC_TAIL = new EdlElementType("S_OP_INPUT_VAR_POLYMORPHIC_TAIL");
  IElementType S_OP_INPUT_VAR_PROJECTION = new EdlElementType("S_OP_INPUT_VAR_PROJECTION");
  IElementType S_OP_INPUT_VAR_SINGLE_TAIL = new EdlElementType("S_OP_INPUT_VAR_SINGLE_TAIL");
  IElementType S_OP_MAP_MODEL_PATH = new EdlElementType("S_OP_MAP_MODEL_PATH");
  IElementType S_OP_MODEL_PATH = new EdlElementType("S_OP_MODEL_PATH");
  IElementType S_OP_MODEL_PATH_PROPERTY = new EdlElementType("S_OP_MODEL_PATH_PROPERTY");
  IElementType S_OP_OUTPUT_FIELD_PROJECTION = new EdlElementType("S_OP_OUTPUT_FIELD_PROJECTION");
  IElementType S_OP_OUTPUT_FIELD_PROJECTION_BODY_PART = new EdlElementType("S_OP_OUTPUT_FIELD_PROJECTION_BODY_PART");
  IElementType S_OP_OUTPUT_FIELD_PROJECTION_ENTRY = new EdlElementType("S_OP_OUTPUT_FIELD_PROJECTION_ENTRY");
  IElementType S_OP_OUTPUT_KEY_PROJECTION = new EdlElementType("S_OP_OUTPUT_KEY_PROJECTION");
  IElementType S_OP_OUTPUT_KEY_PROJECTION_PART = new EdlElementType("S_OP_OUTPUT_KEY_PROJECTION_PART");
  IElementType S_OP_OUTPUT_LIST_MODEL_PROJECTION = new EdlElementType("S_OP_OUTPUT_LIST_MODEL_PROJECTION");
  IElementType S_OP_OUTPUT_MAP_MODEL_PROJECTION = new EdlElementType("S_OP_OUTPUT_MAP_MODEL_PROJECTION");
  IElementType S_OP_OUTPUT_MODEL_META = new EdlElementType("S_OP_OUTPUT_MODEL_META");
  IElementType S_OP_OUTPUT_MODEL_PROJECTION = new EdlElementType("S_OP_OUTPUT_MODEL_PROJECTION");
  IElementType S_OP_OUTPUT_MODEL_PROPERTY = new EdlElementType("S_OP_OUTPUT_MODEL_PROPERTY");
  IElementType S_OP_OUTPUT_MULTI_TAG_PROJECTION = new EdlElementType("S_OP_OUTPUT_MULTI_TAG_PROJECTION");
  IElementType S_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM = new EdlElementType("S_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM");
  IElementType S_OP_OUTPUT_RECORD_MODEL_PROJECTION = new EdlElementType("S_OP_OUTPUT_RECORD_MODEL_PROJECTION");
  IElementType S_OP_OUTPUT_SINGLE_TAG_PROJECTION = new EdlElementType("S_OP_OUTPUT_SINGLE_TAG_PROJECTION");
  IElementType S_OP_OUTPUT_VAR_MULTI_TAIL = new EdlElementType("S_OP_OUTPUT_VAR_MULTI_TAIL");
  IElementType S_OP_OUTPUT_VAR_MULTI_TAIL_ITEM = new EdlElementType("S_OP_OUTPUT_VAR_MULTI_TAIL_ITEM");
  IElementType S_OP_OUTPUT_VAR_POLYMORPHIC_TAIL = new EdlElementType("S_OP_OUTPUT_VAR_POLYMORPHIC_TAIL");
  IElementType S_OP_OUTPUT_VAR_PROJECTION = new EdlElementType("S_OP_OUTPUT_VAR_PROJECTION");
  IElementType S_OP_OUTPUT_VAR_SINGLE_TAIL = new EdlElementType("S_OP_OUTPUT_VAR_SINGLE_TAIL");
  IElementType S_OP_PARAM = new EdlElementType("S_OP_PARAM");
  IElementType S_OP_PATH_KEY_PROJECTION = new EdlElementType("S_OP_PATH_KEY_PROJECTION");
  IElementType S_OP_PATH_KEY_PROJECTION_BODY = new EdlElementType("S_OP_PATH_KEY_PROJECTION_BODY");
  IElementType S_OP_PATH_KEY_PROJECTION_PART = new EdlElementType("S_OP_PATH_KEY_PROJECTION_PART");
  IElementType S_OP_RECORD_MODEL_PATH = new EdlElementType("S_OP_RECORD_MODEL_PATH");
  IElementType S_OP_VAR_PATH = new EdlElementType("S_OP_VAR_PATH");
  IElementType S_PRIMITIVE_DATUM = new EdlElementType("S_PRIMITIVE_DATUM");
  IElementType S_PRIMITIVE_TYPE_BODY = new EdlElementType("S_PRIMITIVE_TYPE_BODY");
  IElementType S_PRIMITIVE_TYPE_DEF = new EdlPrimitiveTypeDefStubElementType("S_PRIMITIVE_TYPE_DEF");
  IElementType S_QID = new EdlElementType("S_QID");
  IElementType S_QN = new EdlElementType("S_QN");
  IElementType S_QN_SEGMENT = new EdlElementType("S_QN_SEGMENT");
  IElementType S_QN_TYPE_REF = new EdlElementType("S_QN_TYPE_REF");
  IElementType S_READ_OPERATION_BODY_PART = new EdlElementType("S_READ_OPERATION_BODY_PART");
  IElementType S_READ_OPERATION_DEF = new EdlElementType("S_READ_OPERATION_DEF");
  IElementType S_RECORD_DATUM = new EdlElementType("S_RECORD_DATUM");
  IElementType S_RECORD_DATUM_ENTRY = new EdlElementType("S_RECORD_DATUM_ENTRY");
  IElementType S_RECORD_TYPE_BODY = new EdlElementType("S_RECORD_TYPE_BODY");
  IElementType S_RECORD_TYPE_DEF = new EdlRecordTypeDefStubElementType("S_RECORD_TYPE_DEF");
  IElementType S_RESOURCE_DEF = new EdlElementType("S_RESOURCE_DEF");
  IElementType S_RESOURCE_NAME = new EdlElementType("S_RESOURCE_NAME");
  IElementType S_RESOURCE_TYPE = new EdlElementType("S_RESOURCE_TYPE");
  IElementType S_SUPPLEMENTS_DECL = new EdlElementType("S_SUPPLEMENTS_DECL");
  IElementType S_SUPPLEMENT_DEF = new EdlSupplementDefStubElementType("S_SUPPLEMENT_DEF");
  IElementType S_TAG_NAME = new EdlElementType("S_TAG_NAME");
  IElementType S_TYPE_DEF_WRAPPER = new EdlTypeDefWrapperStubElementType("S_TYPE_DEF_WRAPPER");
  IElementType S_TYPE_REF = new EdlElementType("S_TYPE_REF");
  IElementType S_UPDATE_OPERATION_BODY_PART = new EdlElementType("S_UPDATE_OPERATION_BODY_PART");
  IElementType S_UPDATE_OPERATION_DEF = new EdlElementType("S_UPDATE_OPERATION_DEF");
  IElementType S_VALUE_TYPE_REF = new EdlElementType("S_VALUE_TYPE_REF");
  IElementType S_VAR_TAG_DECL = new EdlElementType("S_VAR_TAG_DECL");
  IElementType S_VAR_TAG_REF = new EdlElementType("S_VAR_TAG_REF");
  IElementType S_VAR_TYPE_BODY = new EdlElementType("S_VAR_TYPE_BODY");
  IElementType S_VAR_TYPE_DEF = new EdlVarTypeDefStubElementType("S_VAR_TYPE_DEF");

  IElementType S_ABSTRACT = new EdlElementType("abstract");
  IElementType S_ANGLE_LEFT = new EdlElementType("<");
  IElementType S_ANGLE_RIGHT = new EdlElementType(">");
  IElementType S_AT = new EdlElementType("@");
  IElementType S_BANG = new EdlElementType("!");
  IElementType S_BLOCK_COMMENT = new EdlElementType("block_comment");
  IElementType S_BOOLEAN = new EdlElementType("boolean");
  IElementType S_BOOLEAN_T = new EdlElementType("boolean");
  IElementType S_BRACKET_LEFT = new EdlElementType("[");
  IElementType S_BRACKET_RIGHT = new EdlElementType("]");
  IElementType S_COLON = new EdlElementType(":");
  IElementType S_COMMA = new EdlElementType(",");
  IElementType S_COMMENT = new EdlElementType("comment");
  IElementType S_CREATE = new EdlElementType("CREATE");
  IElementType S_CURLY_LEFT = new EdlElementType("{");
  IElementType S_CURLY_RIGHT = new EdlElementType("}");
  IElementType S_CUSTOM = new EdlElementType("CUSTOM");
  IElementType S_DEFAULT = new EdlElementType("default");
  IElementType S_DELETE = new EdlElementType("DELETE");
  IElementType S_DELETE_PROJECTION = new EdlElementType("deleteProjection");
  IElementType S_DOT = new EdlElementType(".");
  IElementType S_DOUBLE_T = new EdlElementType("double");
  IElementType S_ENUM = new EdlElementType("enum");
  IElementType S_EQ = new EdlElementType("=");
  IElementType S_EXTENDS = new EdlElementType("extends");
  IElementType S_FORBIDDEN = new EdlElementType("forbidden");
  IElementType S_GET = new EdlElementType("GET");
  IElementType S_HASH = new EdlElementType("#");
  IElementType S_ID = new EdlElementType("id");
  IElementType S_IMPORT = new EdlElementType("import");
  IElementType S_INPUT_PROJECTION = new EdlElementType("inputProjection");
  IElementType S_INPUT_TYPE = new EdlElementType("inputType");
  IElementType S_INTEGER_T = new EdlElementType("integer");
  IElementType S_LIST = new EdlElementType("list");
  IElementType S_LONG_T = new EdlElementType("long");
  IElementType S_MAP = new EdlElementType("map");
  IElementType S_META = new EdlElementType("meta");
  IElementType S_METHOD = new EdlElementType("method");
  IElementType S_NAMESPACE = new EdlElementType("namespace");
  IElementType S_NODEFAULT = new EdlElementType("nodefault");
  IElementType S_NULL = new EdlElementType("null");
  IElementType S_NUMBER = new EdlElementType("number");
  IElementType S_OUTPUT_PROJECTION = new EdlElementType("outputProjection");
  IElementType S_OUTPUT_TYPE = new EdlElementType("outputType");
  IElementType S_OVERRIDE = new EdlElementType("override");
  IElementType S_PAREN_LEFT = new EdlElementType("(");
  IElementType S_PAREN_RIGHT = new EdlElementType(")");
  IElementType S_PATH = new EdlElementType("path");
  IElementType S_PLUS = new EdlElementType("+");
  IElementType S_POST = new EdlElementType("POST");
  IElementType S_PUT = new EdlElementType("PUT");
  IElementType S_READ = new EdlElementType("READ");
  IElementType S_RECORD = new EdlElementType("record");
  IElementType S_REQUIRED = new EdlElementType("required");
  IElementType S_RESOURCE = new EdlElementType("resource");
  IElementType S_SEMICOLON = new EdlElementType(";");
  IElementType S_SLASH = new EdlElementType("/");
  IElementType S_STAR = new EdlElementType("*");
  IElementType S_STRING = new EdlElementType("string");
  IElementType S_STRING_T = new EdlElementType("string");
  IElementType S_SUPPLEMENT = new EdlElementType("supplement");
  IElementType S_SUPPLEMENTS = new EdlElementType("supplements");
  IElementType S_TILDA = new EdlElementType("~");
  IElementType S_UNDERSCORE = new EdlElementType("_");
  IElementType S_UPDATE = new EdlElementType("UPDATE");
  IElementType S_VARTYPE = new EdlElementType("vartype");
  IElementType S_WITH = new EdlElementType("with");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == S_ANNOTATION) {
        return new EdlAnnotationImpl(node);
      }
      else if (type == S_ANON_LIST) {
        return new EdlAnonListImpl(node);
      }
      else if (type == S_ANON_MAP) {
        return new EdlAnonMapImpl(node);
      }
      else if (type == S_CREATE_OPERATION_BODY_PART) {
        return new EdlCreateOperationBodyPartImpl(node);
      }
      else if (type == S_CREATE_OPERATION_DEF) {
        return new EdlCreateOperationDefImpl(node);
      }
      else if (type == S_CUSTOM_OPERATION_BODY_PART) {
        return new EdlCustomOperationBodyPartImpl(node);
      }
      else if (type == S_CUSTOM_OPERATION_DEF) {
        return new EdlCustomOperationDefImpl(node);
      }
      else if (type == S_DATA) {
        return new EdlDataImpl(node);
      }
      else if (type == S_DATA_ENTRY) {
        return new EdlDataEntryImpl(node);
      }
      else if (type == S_DATA_VALUE) {
        return new EdlDataValueImpl(node);
      }
      else if (type == S_DEFAULT_OVERRIDE) {
        return new EdlDefaultOverrideImpl(node);
      }
      else if (type == S_DEFS) {
        return new EdlDefsImpl(node);
      }
      else if (type == S_DELETE_OPERATION_BODY_PART) {
        return new EdlDeleteOperationBodyPartImpl(node);
      }
      else if (type == S_DELETE_OPERATION_DEF) {
        return new EdlDeleteOperationDefImpl(node);
      }
      else if (type == S_ENUM_DATUM) {
        return new EdlEnumDatumImpl(node);
      }
      else if (type == S_ENUM_MEMBER_DECL) {
        return new EdlEnumMemberDeclImpl(node);
      }
      else if (type == S_ENUM_TYPE_BODY) {
        return new EdlEnumTypeBodyImpl(node);
      }
      else if (type == S_ENUM_TYPE_DEF) {
        return new EdlEnumTypeDefImpl(node);
      }
      else if (type == S_EXTENDS_DECL) {
        return new EdlExtendsDeclImpl(node);
      }
      else if (type == S_FIELD_DECL) {
        return new EdlFieldDeclImpl(node);
      }
      else if (type == S_IMPORTS) {
        return new EdlImportsImpl(node);
      }
      else if (type == S_IMPORT_STATEMENT) {
        return new EdlImportStatementImpl(node);
      }
      else if (type == S_LIST_DATUM) {
        return new EdlListDatumImpl(node);
      }
      else if (type == S_LIST_TYPE_BODY) {
        return new EdlListTypeBodyImpl(node);
      }
      else if (type == S_LIST_TYPE_DEF) {
        return new EdlListTypeDefImpl(node);
      }
      else if (type == S_MAP_DATUM) {
        return new EdlMapDatumImpl(node);
      }
      else if (type == S_MAP_DATUM_ENTRY) {
        return new EdlMapDatumEntryImpl(node);
      }
      else if (type == S_MAP_TYPE_BODY) {
        return new EdlMapTypeBodyImpl(node);
      }
      else if (type == S_MAP_TYPE_DEF) {
        return new EdlMapTypeDefImpl(node);
      }
      else if (type == S_META_DECL) {
        return new EdlMetaDeclImpl(node);
      }
      else if (type == S_NAMESPACE_DECL) {
        return new EdlNamespaceDeclImpl(node);
      }
      else if (type == S_NULL_DATUM) {
        return new EdlNullDatumImpl(node);
      }
      else if (type == S_OPERATION_DEF) {
        return new EdlOperationDefImpl(node);
      }
      else if (type == S_OPERATION_DELETE_PROJECTION) {
        return new EdlOperationDeleteProjectionImpl(node);
      }
      else if (type == S_OPERATION_INPUT_PROJECTION) {
        return new EdlOperationInputProjectionImpl(node);
      }
      else if (type == S_OPERATION_INPUT_TYPE) {
        return new EdlOperationInputTypeImpl(node);
      }
      else if (type == S_OPERATION_METHOD) {
        return new EdlOperationMethodImpl(node);
      }
      else if (type == S_OPERATION_NAME) {
        return new EdlOperationNameImpl(node);
      }
      else if (type == S_OPERATION_OUTPUT_PROJECTION) {
        return new EdlOperationOutputProjectionImpl(node);
      }
      else if (type == S_OPERATION_OUTPUT_TYPE) {
        return new EdlOperationOutputTypeImpl(node);
      }
      else if (type == S_OPERATION_PATH) {
        return new EdlOperationPathImpl(node);
      }
      else if (type == S_OP_DELETE_FIELD_PROJECTION) {
        return new EdlOpDeleteFieldProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_FIELD_PROJECTION_BODY_PART) {
        return new EdlOpDeleteFieldProjectionBodyPartImpl(node);
      }
      else if (type == S_OP_DELETE_FIELD_PROJECTION_ENTRY) {
        return new EdlOpDeleteFieldProjectionEntryImpl(node);
      }
      else if (type == S_OP_DELETE_KEY_PROJECTION) {
        return new EdlOpDeleteKeyProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_KEY_PROJECTION_PART) {
        return new EdlOpDeleteKeyProjectionPartImpl(node);
      }
      else if (type == S_OP_DELETE_LIST_MODEL_PROJECTION) {
        return new EdlOpDeleteListModelProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_MAP_MODEL_PROJECTION) {
        return new EdlOpDeleteMapModelProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_MODEL_PROJECTION) {
        return new EdlOpDeleteModelProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_MODEL_PROPERTY) {
        return new EdlOpDeleteModelPropertyImpl(node);
      }
      else if (type == S_OP_DELETE_MULTI_TAG_PROJECTION) {
        return new EdlOpDeleteMultiTagProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_MULTI_TAG_PROJECTION_ITEM) {
        return new EdlOpDeleteMultiTagProjectionItemImpl(node);
      }
      else if (type == S_OP_DELETE_RECORD_MODEL_PROJECTION) {
        return new EdlOpDeleteRecordModelProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_SINGLE_TAG_PROJECTION) {
        return new EdlOpDeleteSingleTagProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_VAR_MULTI_TAIL) {
        return new EdlOpDeleteVarMultiTailImpl(node);
      }
      else if (type == S_OP_DELETE_VAR_MULTI_TAIL_ITEM) {
        return new EdlOpDeleteVarMultiTailItemImpl(node);
      }
      else if (type == S_OP_DELETE_VAR_POLYMORPHIC_TAIL) {
        return new EdlOpDeleteVarPolymorphicTailImpl(node);
      }
      else if (type == S_OP_DELETE_VAR_PROJECTION) {
        return new EdlOpDeleteVarProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_VAR_SINGLE_TAIL) {
        return new EdlOpDeleteVarSingleTailImpl(node);
      }
      else if (type == S_OP_FIELD_PATH) {
        return new EdlOpFieldPathImpl(node);
      }
      else if (type == S_OP_FIELD_PATH_BODY_PART) {
        return new EdlOpFieldPathBodyPartImpl(node);
      }
      else if (type == S_OP_FIELD_PATH_ENTRY) {
        return new EdlOpFieldPathEntryImpl(node);
      }
      else if (type == S_OP_INPUT_DEFAULT_VALUE) {
        return new EdlOpInputDefaultValueImpl(node);
      }
      else if (type == S_OP_INPUT_FIELD_PROJECTION) {
        return new EdlOpInputFieldProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_FIELD_PROJECTION_BODY_PART) {
        return new EdlOpInputFieldProjectionBodyPartImpl(node);
      }
      else if (type == S_OP_INPUT_FIELD_PROJECTION_ENTRY) {
        return new EdlOpInputFieldProjectionEntryImpl(node);
      }
      else if (type == S_OP_INPUT_KEY_PROJECTION) {
        return new EdlOpInputKeyProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_KEY_PROJECTION_PART) {
        return new EdlOpInputKeyProjectionPartImpl(node);
      }
      else if (type == S_OP_INPUT_LIST_MODEL_PROJECTION) {
        return new EdlOpInputListModelProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_MAP_MODEL_PROJECTION) {
        return new EdlOpInputMapModelProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_MODEL_META) {
        return new EdlOpInputModelMetaImpl(node);
      }
      else if (type == S_OP_INPUT_MODEL_PROJECTION) {
        return new EdlOpInputModelProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_MODEL_PROPERTY) {
        return new EdlOpInputModelPropertyImpl(node);
      }
      else if (type == S_OP_INPUT_MULTI_TAG_PROJECTION) {
        return new EdlOpInputMultiTagProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_MULTI_TAG_PROJECTION_ITEM) {
        return new EdlOpInputMultiTagProjectionItemImpl(node);
      }
      else if (type == S_OP_INPUT_RECORD_MODEL_PROJECTION) {
        return new EdlOpInputRecordModelProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_SINGLE_TAG_PROJECTION) {
        return new EdlOpInputSingleTagProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_VAR_MULTI_TAIL) {
        return new EdlOpInputVarMultiTailImpl(node);
      }
      else if (type == S_OP_INPUT_VAR_MULTI_TAIL_ITEM) {
        return new EdlOpInputVarMultiTailItemImpl(node);
      }
      else if (type == S_OP_INPUT_VAR_POLYMORPHIC_TAIL) {
        return new EdlOpInputVarPolymorphicTailImpl(node);
      }
      else if (type == S_OP_INPUT_VAR_PROJECTION) {
        return new EdlOpInputVarProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_VAR_SINGLE_TAIL) {
        return new EdlOpInputVarSingleTailImpl(node);
      }
      else if (type == S_OP_MAP_MODEL_PATH) {
        return new EdlOpMapModelPathImpl(node);
      }
      else if (type == S_OP_MODEL_PATH) {
        return new EdlOpModelPathImpl(node);
      }
      else if (type == S_OP_MODEL_PATH_PROPERTY) {
        return new EdlOpModelPathPropertyImpl(node);
      }
      else if (type == S_OP_OUTPUT_FIELD_PROJECTION) {
        return new EdlOpOutputFieldProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_FIELD_PROJECTION_BODY_PART) {
        return new EdlOpOutputFieldProjectionBodyPartImpl(node);
      }
      else if (type == S_OP_OUTPUT_FIELD_PROJECTION_ENTRY) {
        return new EdlOpOutputFieldProjectionEntryImpl(node);
      }
      else if (type == S_OP_OUTPUT_KEY_PROJECTION) {
        return new EdlOpOutputKeyProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_KEY_PROJECTION_PART) {
        return new EdlOpOutputKeyProjectionPartImpl(node);
      }
      else if (type == S_OP_OUTPUT_LIST_MODEL_PROJECTION) {
        return new EdlOpOutputListModelProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_MAP_MODEL_PROJECTION) {
        return new EdlOpOutputMapModelProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_MODEL_META) {
        return new EdlOpOutputModelMetaImpl(node);
      }
      else if (type == S_OP_OUTPUT_MODEL_PROJECTION) {
        return new EdlOpOutputModelProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_MODEL_PROPERTY) {
        return new EdlOpOutputModelPropertyImpl(node);
      }
      else if (type == S_OP_OUTPUT_MULTI_TAG_PROJECTION) {
        return new EdlOpOutputMultiTagProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM) {
        return new EdlOpOutputMultiTagProjectionItemImpl(node);
      }
      else if (type == S_OP_OUTPUT_RECORD_MODEL_PROJECTION) {
        return new EdlOpOutputRecordModelProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_SINGLE_TAG_PROJECTION) {
        return new EdlOpOutputSingleTagProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_VAR_MULTI_TAIL) {
        return new EdlOpOutputVarMultiTailImpl(node);
      }
      else if (type == S_OP_OUTPUT_VAR_MULTI_TAIL_ITEM) {
        return new EdlOpOutputVarMultiTailItemImpl(node);
      }
      else if (type == S_OP_OUTPUT_VAR_POLYMORPHIC_TAIL) {
        return new EdlOpOutputVarPolymorphicTailImpl(node);
      }
      else if (type == S_OP_OUTPUT_VAR_PROJECTION) {
        return new EdlOpOutputVarProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_VAR_SINGLE_TAIL) {
        return new EdlOpOutputVarSingleTailImpl(node);
      }
      else if (type == S_OP_PARAM) {
        return new EdlOpParamImpl(node);
      }
      else if (type == S_OP_PATH_KEY_PROJECTION) {
        return new EdlOpPathKeyProjectionImpl(node);
      }
      else if (type == S_OP_PATH_KEY_PROJECTION_BODY) {
        return new EdlOpPathKeyProjectionBodyImpl(node);
      }
      else if (type == S_OP_PATH_KEY_PROJECTION_PART) {
        return new EdlOpPathKeyProjectionPartImpl(node);
      }
      else if (type == S_OP_RECORD_MODEL_PATH) {
        return new EdlOpRecordModelPathImpl(node);
      }
      else if (type == S_OP_VAR_PATH) {
        return new EdlOpVarPathImpl(node);
      }
      else if (type == S_PRIMITIVE_DATUM) {
        return new EdlPrimitiveDatumImpl(node);
      }
      else if (type == S_PRIMITIVE_TYPE_BODY) {
        return new EdlPrimitiveTypeBodyImpl(node);
      }
      else if (type == S_PRIMITIVE_TYPE_DEF) {
        return new EdlPrimitiveTypeDefImpl(node);
      }
      else if (type == S_QID) {
        return new EdlQidImpl(node);
      }
      else if (type == S_QN) {
        return new EdlQnImpl(node);
      }
      else if (type == S_QN_SEGMENT) {
        return new EdlQnSegmentImpl(node);
      }
      else if (type == S_QN_TYPE_REF) {
        return new EdlQnTypeRefImpl(node);
      }
      else if (type == S_READ_OPERATION_BODY_PART) {
        return new EdlReadOperationBodyPartImpl(node);
      }
      else if (type == S_READ_OPERATION_DEF) {
        return new EdlReadOperationDefImpl(node);
      }
      else if (type == S_RECORD_DATUM) {
        return new EdlRecordDatumImpl(node);
      }
      else if (type == S_RECORD_DATUM_ENTRY) {
        return new EdlRecordDatumEntryImpl(node);
      }
      else if (type == S_RECORD_TYPE_BODY) {
        return new EdlRecordTypeBodyImpl(node);
      }
      else if (type == S_RECORD_TYPE_DEF) {
        return new EdlRecordTypeDefImpl(node);
      }
      else if (type == S_RESOURCE_DEF) {
        return new EdlResourceDefImpl(node);
      }
      else if (type == S_RESOURCE_NAME) {
        return new EdlResourceNameImpl(node);
      }
      else if (type == S_RESOURCE_TYPE) {
        return new EdlResourceTypeImpl(node);
      }
      else if (type == S_SUPPLEMENTS_DECL) {
        return new EdlSupplementsDeclImpl(node);
      }
      else if (type == S_SUPPLEMENT_DEF) {
        return new EdlSupplementDefImpl(node);
      }
      else if (type == S_TAG_NAME) {
        return new EdlTagNameImpl(node);
      }
      else if (type == S_TYPE_DEF_WRAPPER) {
        return new EdlTypeDefWrapperImpl(node);
      }
      else if (type == S_UPDATE_OPERATION_BODY_PART) {
        return new EdlUpdateOperationBodyPartImpl(node);
      }
      else if (type == S_UPDATE_OPERATION_DEF) {
        return new EdlUpdateOperationDefImpl(node);
      }
      else if (type == S_VALUE_TYPE_REF) {
        return new EdlValueTypeRefImpl(node);
      }
      else if (type == S_VAR_TAG_DECL) {
        return new EdlVarTagDeclImpl(node);
      }
      else if (type == S_VAR_TAG_REF) {
        return new EdlVarTagRefImpl(node);
      }
      else if (type == S_VAR_TYPE_BODY) {
        return new EdlVarTypeBodyImpl(node);
      }
      else if (type == S_VAR_TYPE_DEF) {
        return new EdlVarTypeDefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
