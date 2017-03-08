/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import ws.epigraph.schema.parser.psi.stubs.SchemaEnumTypeDefStubElementType;
import ws.epigraph.schema.parser.psi.stubs.SchemaListTypeDefStubElementType;
import ws.epigraph.schema.parser.psi.stubs.SchemaMapTypeDefStubElementType;
import ws.epigraph.schema.parser.psi.stubs.SchemaNamespaceDeclStubElementType;
import ws.epigraph.schema.parser.psi.stubs.SchemaPrimitiveTypeDefStubElementType;
import ws.epigraph.schema.parser.psi.stubs.SchemaRecordTypeDefStubElementType;
import ws.epigraph.schema.parser.psi.stubs.SchemaSupplementDefStubElementType;
import ws.epigraph.schema.parser.psi.stubs.SchemaTypeDefWrapperStubElementType;
import ws.epigraph.schema.parser.psi.stubs.SchemaVarTypeDefStubElementType;
import ws.epigraph.schema.parser.psi.impl.*;

public interface SchemaElementTypes {

  IElementType S_ANNOTATION = new SchemaElementType("S_ANNOTATION");
  IElementType S_ANON_LIST = new SchemaElementType("S_ANON_LIST");
  IElementType S_ANON_MAP = new SchemaElementType("S_ANON_MAP");
  IElementType S_CREATE_OPERATION_BODY_PART = new SchemaElementType("S_CREATE_OPERATION_BODY_PART");
  IElementType S_CREATE_OPERATION_DEF = new SchemaElementType("S_CREATE_OPERATION_DEF");
  IElementType S_CUSTOM_OPERATION_BODY_PART = new SchemaElementType("S_CUSTOM_OPERATION_BODY_PART");
  IElementType S_CUSTOM_OPERATION_DEF = new SchemaElementType("S_CUSTOM_OPERATION_DEF");
  IElementType S_DATA = new SchemaElementType("S_DATA");
  IElementType S_DATA_ENTRY = new SchemaElementType("S_DATA_ENTRY");
  IElementType S_DATA_VALUE = new SchemaElementType("S_DATA_VALUE");
  IElementType S_DATUM = new SchemaElementType("S_DATUM");
  IElementType S_DEFS = new SchemaElementType("S_DEFS");
  IElementType S_DELETE_OPERATION_BODY_PART = new SchemaElementType("S_DELETE_OPERATION_BODY_PART");
  IElementType S_DELETE_OPERATION_DEF = new SchemaElementType("S_DELETE_OPERATION_DEF");
  IElementType S_DELETE_PROJECTION_DEF = new SchemaElementType("S_DELETE_PROJECTION_DEF");
  IElementType S_ENUM_DATUM = new SchemaElementType("S_ENUM_DATUM");
  IElementType S_ENUM_MEMBER_DECL = new SchemaElementType("S_ENUM_MEMBER_DECL");
  IElementType S_ENUM_TYPE_BODY = new SchemaElementType("S_ENUM_TYPE_BODY");
  IElementType S_ENUM_TYPE_DEF = new SchemaEnumTypeDefStubElementType("S_ENUM_TYPE_DEF");
  IElementType S_EXTENDS_DECL = new SchemaElementType("S_EXTENDS_DECL");
  IElementType S_FIELD_DECL = new SchemaElementType("S_FIELD_DECL");
  IElementType S_IMPORTS = new SchemaElementType("S_IMPORTS");
  IElementType S_IMPORT_STATEMENT = new SchemaElementType("S_IMPORT_STATEMENT");
  IElementType S_INPUT_PROJECTION_DEF = new SchemaElementType("S_INPUT_PROJECTION_DEF");
  IElementType S_LIST_DATUM = new SchemaElementType("S_LIST_DATUM");
  IElementType S_LIST_TYPE_BODY = new SchemaElementType("S_LIST_TYPE_BODY");
  IElementType S_LIST_TYPE_DEF = new SchemaListTypeDefStubElementType("S_LIST_TYPE_DEF");
  IElementType S_MAP_DATUM = new SchemaElementType("S_MAP_DATUM");
  IElementType S_MAP_DATUM_ENTRY = new SchemaElementType("S_MAP_DATUM_ENTRY");
  IElementType S_MAP_TYPE_BODY = new SchemaElementType("S_MAP_TYPE_BODY");
  IElementType S_MAP_TYPE_DEF = new SchemaMapTypeDefStubElementType("S_MAP_TYPE_DEF");
  IElementType S_META_DECL = new SchemaElementType("S_META_DECL");
  IElementType S_NAMESPACE_DECL = new SchemaNamespaceDeclStubElementType("S_NAMESPACE_DECL");
  IElementType S_NULL_DATUM = new SchemaElementType("S_NULL_DATUM");
  IElementType S_OPERATION_DEF = new SchemaElementType("S_OPERATION_DEF");
  IElementType S_OPERATION_DELETE_PROJECTION = new SchemaElementType("S_OPERATION_DELETE_PROJECTION");
  IElementType S_OPERATION_INPUT_PROJECTION = new SchemaElementType("S_OPERATION_INPUT_PROJECTION");
  IElementType S_OPERATION_INPUT_TYPE = new SchemaElementType("S_OPERATION_INPUT_TYPE");
  IElementType S_OPERATION_METHOD = new SchemaElementType("S_OPERATION_METHOD");
  IElementType S_OPERATION_NAME = new SchemaElementType("S_OPERATION_NAME");
  IElementType S_OPERATION_OUTPUT_PROJECTION = new SchemaElementType("S_OPERATION_OUTPUT_PROJECTION");
  IElementType S_OPERATION_OUTPUT_TYPE = new SchemaElementType("S_OPERATION_OUTPUT_TYPE");
  IElementType S_OPERATION_PATH = new SchemaElementType("S_OPERATION_PATH");
  IElementType S_OP_DELETE_FIELD_PROJECTION = new SchemaElementType("S_OP_DELETE_FIELD_PROJECTION");
  IElementType S_OP_DELETE_FIELD_PROJECTION_ENTRY = new SchemaElementType("S_OP_DELETE_FIELD_PROJECTION_ENTRY");
  IElementType S_OP_DELETE_KEY_PROJECTION = new SchemaElementType("S_OP_DELETE_KEY_PROJECTION");
  IElementType S_OP_DELETE_KEY_PROJECTION_PART = new SchemaElementType("S_OP_DELETE_KEY_PROJECTION_PART");
  IElementType S_OP_DELETE_LIST_MODEL_PROJECTION = new SchemaElementType("S_OP_DELETE_LIST_MODEL_PROJECTION");
  IElementType S_OP_DELETE_MAP_MODEL_PROJECTION = new SchemaElementType("S_OP_DELETE_MAP_MODEL_PROJECTION");
  IElementType S_OP_DELETE_MODEL_MULTI_TAIL = new SchemaElementType("S_OP_DELETE_MODEL_MULTI_TAIL");
  IElementType S_OP_DELETE_MODEL_MULTI_TAIL_ITEM = new SchemaElementType("S_OP_DELETE_MODEL_MULTI_TAIL_ITEM");
  IElementType S_OP_DELETE_MODEL_POLYMORPHIC_TAIL = new SchemaElementType("S_OP_DELETE_MODEL_POLYMORPHIC_TAIL");
  IElementType S_OP_DELETE_MODEL_PROJECTION = new SchemaElementType("S_OP_DELETE_MODEL_PROJECTION");
  IElementType S_OP_DELETE_MODEL_PROPERTY = new SchemaElementType("S_OP_DELETE_MODEL_PROPERTY");
  IElementType S_OP_DELETE_MODEL_SINGLE_TAIL = new SchemaElementType("S_OP_DELETE_MODEL_SINGLE_TAIL");
  IElementType S_OP_DELETE_MULTI_TAG_PROJECTION = new SchemaElementType("S_OP_DELETE_MULTI_TAG_PROJECTION");
  IElementType S_OP_DELETE_MULTI_TAG_PROJECTION_ITEM = new SchemaElementType("S_OP_DELETE_MULTI_TAG_PROJECTION_ITEM");
  IElementType S_OP_DELETE_NAMED_VAR_PROJECTION = new SchemaElementType("S_OP_DELETE_NAMED_VAR_PROJECTION");
  IElementType S_OP_DELETE_RECORD_MODEL_PROJECTION = new SchemaElementType("S_OP_DELETE_RECORD_MODEL_PROJECTION");
  IElementType S_OP_DELETE_SINGLE_TAG_PROJECTION = new SchemaElementType("S_OP_DELETE_SINGLE_TAG_PROJECTION");
  IElementType S_OP_DELETE_UNNAMED_OR_REF_VAR_PROJECTION = new SchemaElementType("S_OP_DELETE_UNNAMED_OR_REF_VAR_PROJECTION");
  IElementType S_OP_DELETE_UNNAMED_VAR_PROJECTION = new SchemaElementType("S_OP_DELETE_UNNAMED_VAR_PROJECTION");
  IElementType S_OP_DELETE_VAR_MULTI_TAIL = new SchemaElementType("S_OP_DELETE_VAR_MULTI_TAIL");
  IElementType S_OP_DELETE_VAR_MULTI_TAIL_ITEM = new SchemaElementType("S_OP_DELETE_VAR_MULTI_TAIL_ITEM");
  IElementType S_OP_DELETE_VAR_POLYMORPHIC_TAIL = new SchemaElementType("S_OP_DELETE_VAR_POLYMORPHIC_TAIL");
  IElementType S_OP_DELETE_VAR_PROJECTION = new SchemaElementType("S_OP_DELETE_VAR_PROJECTION");
  IElementType S_OP_DELETE_VAR_PROJECTION_REF = new SchemaElementType("S_OP_DELETE_VAR_PROJECTION_REF");
  IElementType S_OP_DELETE_VAR_SINGLE_TAIL = new SchemaElementType("S_OP_DELETE_VAR_SINGLE_TAIL");
  IElementType S_OP_FIELD_PATH = new SchemaElementType("S_OP_FIELD_PATH");
  IElementType S_OP_FIELD_PATH_ENTRY = new SchemaElementType("S_OP_FIELD_PATH_ENTRY");
  IElementType S_OP_INPUT_DEFAULT_VALUE = new SchemaElementType("S_OP_INPUT_DEFAULT_VALUE");
  IElementType S_OP_INPUT_FIELD_PROJECTION = new SchemaElementType("S_OP_INPUT_FIELD_PROJECTION");
  IElementType S_OP_INPUT_FIELD_PROJECTION_ENTRY = new SchemaElementType("S_OP_INPUT_FIELD_PROJECTION_ENTRY");
  IElementType S_OP_INPUT_KEY_PROJECTION = new SchemaElementType("S_OP_INPUT_KEY_PROJECTION");
  IElementType S_OP_INPUT_KEY_PROJECTION_PART = new SchemaElementType("S_OP_INPUT_KEY_PROJECTION_PART");
  IElementType S_OP_INPUT_LIST_MODEL_PROJECTION = new SchemaElementType("S_OP_INPUT_LIST_MODEL_PROJECTION");
  IElementType S_OP_INPUT_MAP_MODEL_PROJECTION = new SchemaElementType("S_OP_INPUT_MAP_MODEL_PROJECTION");
  IElementType S_OP_INPUT_MODEL_META = new SchemaElementType("S_OP_INPUT_MODEL_META");
  IElementType S_OP_INPUT_MODEL_MULTI_TAIL = new SchemaElementType("S_OP_INPUT_MODEL_MULTI_TAIL");
  IElementType S_OP_INPUT_MODEL_MULTI_TAIL_ITEM = new SchemaElementType("S_OP_INPUT_MODEL_MULTI_TAIL_ITEM");
  IElementType S_OP_INPUT_MODEL_POLYMORPHIC_TAIL = new SchemaElementType("S_OP_INPUT_MODEL_POLYMORPHIC_TAIL");
  IElementType S_OP_INPUT_MODEL_PROJECTION = new SchemaElementType("S_OP_INPUT_MODEL_PROJECTION");
  IElementType S_OP_INPUT_MODEL_PROPERTY = new SchemaElementType("S_OP_INPUT_MODEL_PROPERTY");
  IElementType S_OP_INPUT_MODEL_SINGLE_TAIL = new SchemaElementType("S_OP_INPUT_MODEL_SINGLE_TAIL");
  IElementType S_OP_INPUT_MULTI_TAG_PROJECTION = new SchemaElementType("S_OP_INPUT_MULTI_TAG_PROJECTION");
  IElementType S_OP_INPUT_MULTI_TAG_PROJECTION_ITEM = new SchemaElementType("S_OP_INPUT_MULTI_TAG_PROJECTION_ITEM");
  IElementType S_OP_INPUT_NAMED_VAR_PROJECTION = new SchemaElementType("S_OP_INPUT_NAMED_VAR_PROJECTION");
  IElementType S_OP_INPUT_RECORD_MODEL_PROJECTION = new SchemaElementType("S_OP_INPUT_RECORD_MODEL_PROJECTION");
  IElementType S_OP_INPUT_SINGLE_TAG_PROJECTION = new SchemaElementType("S_OP_INPUT_SINGLE_TAG_PROJECTION");
  IElementType S_OP_INPUT_UNNAMED_OR_REF_VAR_PROJECTION = new SchemaElementType("S_OP_INPUT_UNNAMED_OR_REF_VAR_PROJECTION");
  IElementType S_OP_INPUT_UNNAMED_VAR_PROJECTION = new SchemaElementType("S_OP_INPUT_UNNAMED_VAR_PROJECTION");
  IElementType S_OP_INPUT_VAR_MULTI_TAIL = new SchemaElementType("S_OP_INPUT_VAR_MULTI_TAIL");
  IElementType S_OP_INPUT_VAR_MULTI_TAIL_ITEM = new SchemaElementType("S_OP_INPUT_VAR_MULTI_TAIL_ITEM");
  IElementType S_OP_INPUT_VAR_POLYMORPHIC_TAIL = new SchemaElementType("S_OP_INPUT_VAR_POLYMORPHIC_TAIL");
  IElementType S_OP_INPUT_VAR_PROJECTION = new SchemaElementType("S_OP_INPUT_VAR_PROJECTION");
  IElementType S_OP_INPUT_VAR_PROJECTION_REF = new SchemaElementType("S_OP_INPUT_VAR_PROJECTION_REF");
  IElementType S_OP_INPUT_VAR_SINGLE_TAIL = new SchemaElementType("S_OP_INPUT_VAR_SINGLE_TAIL");
  IElementType S_OP_MAP_MODEL_PATH = new SchemaElementType("S_OP_MAP_MODEL_PATH");
  IElementType S_OP_MODEL_PATH = new SchemaElementType("S_OP_MODEL_PATH");
  IElementType S_OP_MODEL_PATH_PROPERTY = new SchemaElementType("S_OP_MODEL_PATH_PROPERTY");
  IElementType S_OP_OUTPUT_FIELD_PROJECTION = new SchemaElementType("S_OP_OUTPUT_FIELD_PROJECTION");
  IElementType S_OP_OUTPUT_FIELD_PROJECTION_ENTRY = new SchemaElementType("S_OP_OUTPUT_FIELD_PROJECTION_ENTRY");
  IElementType S_OP_OUTPUT_KEY_PROJECTION = new SchemaElementType("S_OP_OUTPUT_KEY_PROJECTION");
  IElementType S_OP_OUTPUT_KEY_PROJECTION_PART = new SchemaElementType("S_OP_OUTPUT_KEY_PROJECTION_PART");
  IElementType S_OP_OUTPUT_LIST_MODEL_PROJECTION = new SchemaElementType("S_OP_OUTPUT_LIST_MODEL_PROJECTION");
  IElementType S_OP_OUTPUT_MAP_MODEL_PROJECTION = new SchemaElementType("S_OP_OUTPUT_MAP_MODEL_PROJECTION");
  IElementType S_OP_OUTPUT_MODEL_META = new SchemaElementType("S_OP_OUTPUT_MODEL_META");
  IElementType S_OP_OUTPUT_MODEL_MULTI_TAIL = new SchemaElementType("S_OP_OUTPUT_MODEL_MULTI_TAIL");
  IElementType S_OP_OUTPUT_MODEL_MULTI_TAIL_ITEM = new SchemaElementType("S_OP_OUTPUT_MODEL_MULTI_TAIL_ITEM");
  IElementType S_OP_OUTPUT_MODEL_POLYMORPHIC_TAIL = new SchemaElementType("S_OP_OUTPUT_MODEL_POLYMORPHIC_TAIL");
  IElementType S_OP_OUTPUT_MODEL_PROJECTION = new SchemaElementType("S_OP_OUTPUT_MODEL_PROJECTION");
  IElementType S_OP_OUTPUT_MODEL_PROPERTY = new SchemaElementType("S_OP_OUTPUT_MODEL_PROPERTY");
  IElementType S_OP_OUTPUT_MODEL_SINGLE_TAIL = new SchemaElementType("S_OP_OUTPUT_MODEL_SINGLE_TAIL");
  IElementType S_OP_OUTPUT_MULTI_TAG_PROJECTION = new SchemaElementType("S_OP_OUTPUT_MULTI_TAG_PROJECTION");
  IElementType S_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM = new SchemaElementType("S_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM");
  IElementType S_OP_OUTPUT_NAMED_VAR_PROJECTION = new SchemaElementType("S_OP_OUTPUT_NAMED_VAR_PROJECTION");
  IElementType S_OP_OUTPUT_RECORD_MODEL_PROJECTION = new SchemaElementType("S_OP_OUTPUT_RECORD_MODEL_PROJECTION");
  IElementType S_OP_OUTPUT_SINGLE_TAG_PROJECTION = new SchemaElementType("S_OP_OUTPUT_SINGLE_TAG_PROJECTION");
  IElementType S_OP_OUTPUT_UNNAMED_OR_REF_VAR_PROJECTION = new SchemaElementType("S_OP_OUTPUT_UNNAMED_OR_REF_VAR_PROJECTION");
  IElementType S_OP_OUTPUT_UNNAMED_VAR_PROJECTION = new SchemaElementType("S_OP_OUTPUT_UNNAMED_VAR_PROJECTION");
  IElementType S_OP_OUTPUT_VAR_MULTI_TAIL = new SchemaElementType("S_OP_OUTPUT_VAR_MULTI_TAIL");
  IElementType S_OP_OUTPUT_VAR_MULTI_TAIL_ITEM = new SchemaElementType("S_OP_OUTPUT_VAR_MULTI_TAIL_ITEM");
  IElementType S_OP_OUTPUT_VAR_POLYMORPHIC_TAIL = new SchemaElementType("S_OP_OUTPUT_VAR_POLYMORPHIC_TAIL");
  IElementType S_OP_OUTPUT_VAR_PROJECTION = new SchemaElementType("S_OP_OUTPUT_VAR_PROJECTION");
  IElementType S_OP_OUTPUT_VAR_PROJECTION_REF = new SchemaElementType("S_OP_OUTPUT_VAR_PROJECTION_REF");
  IElementType S_OP_OUTPUT_VAR_SINGLE_TAIL = new SchemaElementType("S_OP_OUTPUT_VAR_SINGLE_TAIL");
  IElementType S_OP_PARAM = new SchemaElementType("S_OP_PARAM");
  IElementType S_OP_PATH_KEY_PROJECTION = new SchemaElementType("S_OP_PATH_KEY_PROJECTION");
  IElementType S_OP_PATH_KEY_PROJECTION_BODY = new SchemaElementType("S_OP_PATH_KEY_PROJECTION_BODY");
  IElementType S_OP_PATH_KEY_PROJECTION_PART = new SchemaElementType("S_OP_PATH_KEY_PROJECTION_PART");
  IElementType S_OP_RECORD_MODEL_PATH = new SchemaElementType("S_OP_RECORD_MODEL_PATH");
  IElementType S_OP_VAR_PATH = new SchemaElementType("S_OP_VAR_PATH");
  IElementType S_OUTPUT_PROJECTION_DEF = new SchemaElementType("S_OUTPUT_PROJECTION_DEF");
  IElementType S_PRIMITIVE_DATUM = new SchemaElementType("S_PRIMITIVE_DATUM");
  IElementType S_PRIMITIVE_TYPE_BODY = new SchemaElementType("S_PRIMITIVE_TYPE_BODY");
  IElementType S_PRIMITIVE_TYPE_DEF = new SchemaPrimitiveTypeDefStubElementType("S_PRIMITIVE_TYPE_DEF");
  IElementType S_PROJECTION_DEF = new SchemaElementType("S_PROJECTION_DEF");
  IElementType S_QID = new SchemaElementType("S_QID");
  IElementType S_QN = new SchemaElementType("S_QN");
  IElementType S_QN_SEGMENT = new SchemaElementType("S_QN_SEGMENT");
  IElementType S_QN_TYPE_REF = new SchemaElementType("S_QN_TYPE_REF");
  IElementType S_READ_OPERATION_BODY_PART = new SchemaElementType("S_READ_OPERATION_BODY_PART");
  IElementType S_READ_OPERATION_DEF = new SchemaElementType("S_READ_OPERATION_DEF");
  IElementType S_RECORD_DATUM = new SchemaElementType("S_RECORD_DATUM");
  IElementType S_RECORD_DATUM_ENTRY = new SchemaElementType("S_RECORD_DATUM_ENTRY");
  IElementType S_RECORD_TYPE_BODY = new SchemaElementType("S_RECORD_TYPE_BODY");
  IElementType S_RECORD_TYPE_DEF = new SchemaRecordTypeDefStubElementType("S_RECORD_TYPE_DEF");
  IElementType S_RESOURCE_DEF = new SchemaElementType("S_RESOURCE_DEF");
  IElementType S_RESOURCE_NAME = new SchemaElementType("S_RESOURCE_NAME");
  IElementType S_RESOURCE_TYPE = new SchemaElementType("S_RESOURCE_TYPE");
  IElementType S_RETRO_DECL = new SchemaElementType("S_RETRO_DECL");
  IElementType S_SUPPLEMENTS_DECL = new SchemaElementType("S_SUPPLEMENTS_DECL");
  IElementType S_SUPPLEMENT_DEF = new SchemaSupplementDefStubElementType("S_SUPPLEMENT_DEF");
  IElementType S_TAG_NAME = new SchemaElementType("S_TAG_NAME");
  IElementType S_TYPE_DEF_WRAPPER = new SchemaTypeDefWrapperStubElementType("S_TYPE_DEF_WRAPPER");
  IElementType S_TYPE_REF = new SchemaElementType("S_TYPE_REF");
  IElementType S_UPDATE_OPERATION_BODY_PART = new SchemaElementType("S_UPDATE_OPERATION_BODY_PART");
  IElementType S_UPDATE_OPERATION_DEF = new SchemaElementType("S_UPDATE_OPERATION_DEF");
  IElementType S_VALUE_TYPE_REF = new SchemaElementType("S_VALUE_TYPE_REF");
  IElementType S_VAR_TAG_DECL = new SchemaElementType("S_VAR_TAG_DECL");
  IElementType S_VAR_TAG_REF = new SchemaElementType("S_VAR_TAG_REF");
  IElementType S_VAR_TYPE_BODY = new SchemaElementType("S_VAR_TYPE_BODY");
  IElementType S_VAR_TYPE_DEF = new SchemaVarTypeDefStubElementType("S_VAR_TYPE_DEF");

  IElementType S_ABSTRACT = new SchemaElementType("abstract");
  IElementType S_ANGLE_LEFT = new SchemaElementType("<");
  IElementType S_ANGLE_RIGHT = new SchemaElementType(">");
  IElementType S_AT = new SchemaElementType("@");
  IElementType S_BANG = new SchemaElementType("!");
  IElementType S_BLOCK_COMMENT = new SchemaElementType("block_comment");
  IElementType S_BOOLEAN = new SchemaElementType("boolean");
  IElementType S_BOOLEAN_T = new SchemaElementType("boolean");
  IElementType S_BRACKET_LEFT = new SchemaElementType("[");
  IElementType S_BRACKET_RIGHT = new SchemaElementType("]");
  IElementType S_COLON = new SchemaElementType(":");
  IElementType S_COMMA = new SchemaElementType(",");
  IElementType S_COMMENT = new SchemaElementType("comment");
  IElementType S_CURLY_LEFT = new SchemaElementType("{");
  IElementType S_CURLY_RIGHT = new SchemaElementType("}");
  IElementType S_DEFAULT = new SchemaElementType("default");
  IElementType S_DELETE = new SchemaElementType("DELETE");
  IElementType S_DELETE_PROJECTION = new SchemaElementType("deleteProjection");
  IElementType S_DOLLAR = new SchemaElementType("$");
  IElementType S_DOT = new SchemaElementType(".");
  IElementType S_DOUBLE_T = new SchemaElementType("double");
  IElementType S_ENUM = new SchemaElementType("enum");
  IElementType S_EQ = new SchemaElementType("=");
  IElementType S_EXTENDS = new SchemaElementType("extends");
  IElementType S_FORBIDDEN = new SchemaElementType("forbidden");
  IElementType S_GET = new SchemaElementType("GET");
  IElementType S_HASH = new SchemaElementType("#");
  IElementType S_ID = new SchemaElementType("id");
  IElementType S_IMPORT = new SchemaElementType("import");
  IElementType S_INPUT_PROJECTION = new SchemaElementType("inputProjection");
  IElementType S_INPUT_TYPE = new SchemaElementType("inputType");
  IElementType S_INTEGER_T = new SchemaElementType("integer");
  IElementType S_LIST = new SchemaElementType("list");
  IElementType S_LONG_T = new SchemaElementType("long");
  IElementType S_MAP = new SchemaElementType("map");
  IElementType S_META = new SchemaElementType("meta");
  IElementType S_METHOD = new SchemaElementType("method");
  IElementType S_NAMESPACE = new SchemaElementType("namespace");
  IElementType S_NODEFAULT = new SchemaElementType("nodefault");
  IElementType S_NULL = new SchemaElementType("null");
  IElementType S_NUMBER = new SchemaElementType("number");
  IElementType S_OP_CREATE = new SchemaElementType("create");
  IElementType S_OP_CUSTOM = new SchemaElementType("custom");
  IElementType S_OP_DELETE = new SchemaElementType("delete");
  IElementType S_OP_READ = new SchemaElementType("read");
  IElementType S_OP_UPDATE = new SchemaElementType("update");
  IElementType S_OUTPUT_PROJECTION = new SchemaElementType("outputProjection");
  IElementType S_OUTPUT_TYPE = new SchemaElementType("outputType");
  IElementType S_OVERRIDE = new SchemaElementType("override");
  IElementType S_PAREN_LEFT = new SchemaElementType("(");
  IElementType S_PAREN_RIGHT = new SchemaElementType(")");
  IElementType S_PATH = new SchemaElementType("path");
  IElementType S_PLUS = new SchemaElementType("+");
  IElementType S_POST = new SchemaElementType("POST");
  IElementType S_PUT = new SchemaElementType("PUT");
  IElementType S_RECORD = new SchemaElementType("record");
  IElementType S_REQUIRED = new SchemaElementType("required");
  IElementType S_RESOURCE = new SchemaElementType("resource");
  IElementType S_RETRO = new SchemaElementType("retro");
  IElementType S_SEMICOLON = new SchemaElementType(";");
  IElementType S_SLASH = new SchemaElementType("/");
  IElementType S_STAR = new SchemaElementType("*");
  IElementType S_STRING = new SchemaElementType("string");
  IElementType S_STRING_T = new SchemaElementType("string");
  IElementType S_SUPPLEMENT = new SchemaElementType("supplement");
  IElementType S_SUPPLEMENTS = new SchemaElementType("supplements");
  IElementType S_TILDA = new SchemaElementType("~");
  IElementType S_UNDERSCORE = new SchemaElementType("_");
  IElementType S_VARTYPE = new SchemaElementType("vartype");
  IElementType S_WITH = new SchemaElementType("with");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == S_ANNOTATION) {
        return new SchemaAnnotationImpl(node);
      }
      else if (type == S_ANON_LIST) {
        return new SchemaAnonListImpl(node);
      }
      else if (type == S_ANON_MAP) {
        return new SchemaAnonMapImpl(node);
      }
      else if (type == S_CREATE_OPERATION_BODY_PART) {
        return new SchemaCreateOperationBodyPartImpl(node);
      }
      else if (type == S_CREATE_OPERATION_DEF) {
        return new SchemaCreateOperationDefImpl(node);
      }
      else if (type == S_CUSTOM_OPERATION_BODY_PART) {
        return new SchemaCustomOperationBodyPartImpl(node);
      }
      else if (type == S_CUSTOM_OPERATION_DEF) {
        return new SchemaCustomOperationDefImpl(node);
      }
      else if (type == S_DATA) {
        return new SchemaDataImpl(node);
      }
      else if (type == S_DATA_ENTRY) {
        return new SchemaDataEntryImpl(node);
      }
      else if (type == S_DATA_VALUE) {
        return new SchemaDataValueImpl(node);
      }
      else if (type == S_DEFS) {
        return new SchemaDefsImpl(node);
      }
      else if (type == S_DELETE_OPERATION_BODY_PART) {
        return new SchemaDeleteOperationBodyPartImpl(node);
      }
      else if (type == S_DELETE_OPERATION_DEF) {
        return new SchemaDeleteOperationDefImpl(node);
      }
      else if (type == S_DELETE_PROJECTION_DEF) {
        return new SchemaDeleteProjectionDefImpl(node);
      }
      else if (type == S_ENUM_DATUM) {
        return new SchemaEnumDatumImpl(node);
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
      else if (type == S_IMPORTS) {
        return new SchemaImportsImpl(node);
      }
      else if (type == S_IMPORT_STATEMENT) {
        return new SchemaImportStatementImpl(node);
      }
      else if (type == S_INPUT_PROJECTION_DEF) {
        return new SchemaInputProjectionDefImpl(node);
      }
      else if (type == S_LIST_DATUM) {
        return new SchemaListDatumImpl(node);
      }
      else if (type == S_LIST_TYPE_BODY) {
        return new SchemaListTypeBodyImpl(node);
      }
      else if (type == S_LIST_TYPE_DEF) {
        return new SchemaListTypeDefImpl(node);
      }
      else if (type == S_MAP_DATUM) {
        return new SchemaMapDatumImpl(node);
      }
      else if (type == S_MAP_DATUM_ENTRY) {
        return new SchemaMapDatumEntryImpl(node);
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
      else if (type == S_NULL_DATUM) {
        return new SchemaNullDatumImpl(node);
      }
      else if (type == S_OPERATION_DEF) {
        return new SchemaOperationDefImpl(node);
      }
      else if (type == S_OPERATION_DELETE_PROJECTION) {
        return new SchemaOperationDeleteProjectionImpl(node);
      }
      else if (type == S_OPERATION_INPUT_PROJECTION) {
        return new SchemaOperationInputProjectionImpl(node);
      }
      else if (type == S_OPERATION_INPUT_TYPE) {
        return new SchemaOperationInputTypeImpl(node);
      }
      else if (type == S_OPERATION_METHOD) {
        return new SchemaOperationMethodImpl(node);
      }
      else if (type == S_OPERATION_NAME) {
        return new SchemaOperationNameImpl(node);
      }
      else if (type == S_OPERATION_OUTPUT_PROJECTION) {
        return new SchemaOperationOutputProjectionImpl(node);
      }
      else if (type == S_OPERATION_OUTPUT_TYPE) {
        return new SchemaOperationOutputTypeImpl(node);
      }
      else if (type == S_OPERATION_PATH) {
        return new SchemaOperationPathImpl(node);
      }
      else if (type == S_OP_DELETE_FIELD_PROJECTION) {
        return new SchemaOpDeleteFieldProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_FIELD_PROJECTION_ENTRY) {
        return new SchemaOpDeleteFieldProjectionEntryImpl(node);
      }
      else if (type == S_OP_DELETE_KEY_PROJECTION) {
        return new SchemaOpDeleteKeyProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_KEY_PROJECTION_PART) {
        return new SchemaOpDeleteKeyProjectionPartImpl(node);
      }
      else if (type == S_OP_DELETE_LIST_MODEL_PROJECTION) {
        return new SchemaOpDeleteListModelProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_MAP_MODEL_PROJECTION) {
        return new SchemaOpDeleteMapModelProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_MODEL_MULTI_TAIL) {
        return new SchemaOpDeleteModelMultiTailImpl(node);
      }
      else if (type == S_OP_DELETE_MODEL_MULTI_TAIL_ITEM) {
        return new SchemaOpDeleteModelMultiTailItemImpl(node);
      }
      else if (type == S_OP_DELETE_MODEL_POLYMORPHIC_TAIL) {
        return new SchemaOpDeleteModelPolymorphicTailImpl(node);
      }
      else if (type == S_OP_DELETE_MODEL_PROJECTION) {
        return new SchemaOpDeleteModelProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_MODEL_PROPERTY) {
        return new SchemaOpDeleteModelPropertyImpl(node);
      }
      else if (type == S_OP_DELETE_MODEL_SINGLE_TAIL) {
        return new SchemaOpDeleteModelSingleTailImpl(node);
      }
      else if (type == S_OP_DELETE_MULTI_TAG_PROJECTION) {
        return new SchemaOpDeleteMultiTagProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_MULTI_TAG_PROJECTION_ITEM) {
        return new SchemaOpDeleteMultiTagProjectionItemImpl(node);
      }
      else if (type == S_OP_DELETE_NAMED_VAR_PROJECTION) {
        return new SchemaOpDeleteNamedVarProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_RECORD_MODEL_PROJECTION) {
        return new SchemaOpDeleteRecordModelProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_SINGLE_TAG_PROJECTION) {
        return new SchemaOpDeleteSingleTagProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_UNNAMED_OR_REF_VAR_PROJECTION) {
        return new SchemaOpDeleteUnnamedOrRefVarProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_UNNAMED_VAR_PROJECTION) {
        return new SchemaOpDeleteUnnamedVarProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_VAR_MULTI_TAIL) {
        return new SchemaOpDeleteVarMultiTailImpl(node);
      }
      else if (type == S_OP_DELETE_VAR_MULTI_TAIL_ITEM) {
        return new SchemaOpDeleteVarMultiTailItemImpl(node);
      }
      else if (type == S_OP_DELETE_VAR_POLYMORPHIC_TAIL) {
        return new SchemaOpDeleteVarPolymorphicTailImpl(node);
      }
      else if (type == S_OP_DELETE_VAR_PROJECTION) {
        return new SchemaOpDeleteVarProjectionImpl(node);
      }
      else if (type == S_OP_DELETE_VAR_PROJECTION_REF) {
        return new SchemaOpDeleteVarProjectionRefImpl(node);
      }
      else if (type == S_OP_DELETE_VAR_SINGLE_TAIL) {
        return new SchemaOpDeleteVarSingleTailImpl(node);
      }
      else if (type == S_OP_FIELD_PATH) {
        return new SchemaOpFieldPathImpl(node);
      }
      else if (type == S_OP_FIELD_PATH_ENTRY) {
        return new SchemaOpFieldPathEntryImpl(node);
      }
      else if (type == S_OP_INPUT_DEFAULT_VALUE) {
        return new SchemaOpInputDefaultValueImpl(node);
      }
      else if (type == S_OP_INPUT_FIELD_PROJECTION) {
        return new SchemaOpInputFieldProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_FIELD_PROJECTION_ENTRY) {
        return new SchemaOpInputFieldProjectionEntryImpl(node);
      }
      else if (type == S_OP_INPUT_KEY_PROJECTION) {
        return new SchemaOpInputKeyProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_KEY_PROJECTION_PART) {
        return new SchemaOpInputKeyProjectionPartImpl(node);
      }
      else if (type == S_OP_INPUT_LIST_MODEL_PROJECTION) {
        return new SchemaOpInputListModelProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_MAP_MODEL_PROJECTION) {
        return new SchemaOpInputMapModelProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_MODEL_META) {
        return new SchemaOpInputModelMetaImpl(node);
      }
      else if (type == S_OP_INPUT_MODEL_MULTI_TAIL) {
        return new SchemaOpInputModelMultiTailImpl(node);
      }
      else if (type == S_OP_INPUT_MODEL_MULTI_TAIL_ITEM) {
        return new SchemaOpInputModelMultiTailItemImpl(node);
      }
      else if (type == S_OP_INPUT_MODEL_POLYMORPHIC_TAIL) {
        return new SchemaOpInputModelPolymorphicTailImpl(node);
      }
      else if (type == S_OP_INPUT_MODEL_PROJECTION) {
        return new SchemaOpInputModelProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_MODEL_PROPERTY) {
        return new SchemaOpInputModelPropertyImpl(node);
      }
      else if (type == S_OP_INPUT_MODEL_SINGLE_TAIL) {
        return new SchemaOpInputModelSingleTailImpl(node);
      }
      else if (type == S_OP_INPUT_MULTI_TAG_PROJECTION) {
        return new SchemaOpInputMultiTagProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_MULTI_TAG_PROJECTION_ITEM) {
        return new SchemaOpInputMultiTagProjectionItemImpl(node);
      }
      else if (type == S_OP_INPUT_NAMED_VAR_PROJECTION) {
        return new SchemaOpInputNamedVarProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_RECORD_MODEL_PROJECTION) {
        return new SchemaOpInputRecordModelProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_SINGLE_TAG_PROJECTION) {
        return new SchemaOpInputSingleTagProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_UNNAMED_OR_REF_VAR_PROJECTION) {
        return new SchemaOpInputUnnamedOrRefVarProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_UNNAMED_VAR_PROJECTION) {
        return new SchemaOpInputUnnamedVarProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_VAR_MULTI_TAIL) {
        return new SchemaOpInputVarMultiTailImpl(node);
      }
      else if (type == S_OP_INPUT_VAR_MULTI_TAIL_ITEM) {
        return new SchemaOpInputVarMultiTailItemImpl(node);
      }
      else if (type == S_OP_INPUT_VAR_POLYMORPHIC_TAIL) {
        return new SchemaOpInputVarPolymorphicTailImpl(node);
      }
      else if (type == S_OP_INPUT_VAR_PROJECTION) {
        return new SchemaOpInputVarProjectionImpl(node);
      }
      else if (type == S_OP_INPUT_VAR_PROJECTION_REF) {
        return new SchemaOpInputVarProjectionRefImpl(node);
      }
      else if (type == S_OP_INPUT_VAR_SINGLE_TAIL) {
        return new SchemaOpInputVarSingleTailImpl(node);
      }
      else if (type == S_OP_MAP_MODEL_PATH) {
        return new SchemaOpMapModelPathImpl(node);
      }
      else if (type == S_OP_MODEL_PATH) {
        return new SchemaOpModelPathImpl(node);
      }
      else if (type == S_OP_MODEL_PATH_PROPERTY) {
        return new SchemaOpModelPathPropertyImpl(node);
      }
      else if (type == S_OP_OUTPUT_FIELD_PROJECTION) {
        return new SchemaOpOutputFieldProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_FIELD_PROJECTION_ENTRY) {
        return new SchemaOpOutputFieldProjectionEntryImpl(node);
      }
      else if (type == S_OP_OUTPUT_KEY_PROJECTION) {
        return new SchemaOpOutputKeyProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_KEY_PROJECTION_PART) {
        return new SchemaOpOutputKeyProjectionPartImpl(node);
      }
      else if (type == S_OP_OUTPUT_LIST_MODEL_PROJECTION) {
        return new SchemaOpOutputListModelProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_MAP_MODEL_PROJECTION) {
        return new SchemaOpOutputMapModelProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_MODEL_META) {
        return new SchemaOpOutputModelMetaImpl(node);
      }
      else if (type == S_OP_OUTPUT_MODEL_MULTI_TAIL) {
        return new SchemaOpOutputModelMultiTailImpl(node);
      }
      else if (type == S_OP_OUTPUT_MODEL_MULTI_TAIL_ITEM) {
        return new SchemaOpOutputModelMultiTailItemImpl(node);
      }
      else if (type == S_OP_OUTPUT_MODEL_POLYMORPHIC_TAIL) {
        return new SchemaOpOutputModelPolymorphicTailImpl(node);
      }
      else if (type == S_OP_OUTPUT_MODEL_PROJECTION) {
        return new SchemaOpOutputModelProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_MODEL_PROPERTY) {
        return new SchemaOpOutputModelPropertyImpl(node);
      }
      else if (type == S_OP_OUTPUT_MODEL_SINGLE_TAIL) {
        return new SchemaOpOutputModelSingleTailImpl(node);
      }
      else if (type == S_OP_OUTPUT_MULTI_TAG_PROJECTION) {
        return new SchemaOpOutputMultiTagProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM) {
        return new SchemaOpOutputMultiTagProjectionItemImpl(node);
      }
      else if (type == S_OP_OUTPUT_NAMED_VAR_PROJECTION) {
        return new SchemaOpOutputNamedVarProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_RECORD_MODEL_PROJECTION) {
        return new SchemaOpOutputRecordModelProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_SINGLE_TAG_PROJECTION) {
        return new SchemaOpOutputSingleTagProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_UNNAMED_OR_REF_VAR_PROJECTION) {
        return new SchemaOpOutputUnnamedOrRefVarProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_UNNAMED_VAR_PROJECTION) {
        return new SchemaOpOutputUnnamedVarProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_VAR_MULTI_TAIL) {
        return new SchemaOpOutputVarMultiTailImpl(node);
      }
      else if (type == S_OP_OUTPUT_VAR_MULTI_TAIL_ITEM) {
        return new SchemaOpOutputVarMultiTailItemImpl(node);
      }
      else if (type == S_OP_OUTPUT_VAR_POLYMORPHIC_TAIL) {
        return new SchemaOpOutputVarPolymorphicTailImpl(node);
      }
      else if (type == S_OP_OUTPUT_VAR_PROJECTION) {
        return new SchemaOpOutputVarProjectionImpl(node);
      }
      else if (type == S_OP_OUTPUT_VAR_PROJECTION_REF) {
        return new SchemaOpOutputVarProjectionRefImpl(node);
      }
      else if (type == S_OP_OUTPUT_VAR_SINGLE_TAIL) {
        return new SchemaOpOutputVarSingleTailImpl(node);
      }
      else if (type == S_OP_PARAM) {
        return new SchemaOpParamImpl(node);
      }
      else if (type == S_OP_PATH_KEY_PROJECTION) {
        return new SchemaOpPathKeyProjectionImpl(node);
      }
      else if (type == S_OP_PATH_KEY_PROJECTION_BODY) {
        return new SchemaOpPathKeyProjectionBodyImpl(node);
      }
      else if (type == S_OP_PATH_KEY_PROJECTION_PART) {
        return new SchemaOpPathKeyProjectionPartImpl(node);
      }
      else if (type == S_OP_RECORD_MODEL_PATH) {
        return new SchemaOpRecordModelPathImpl(node);
      }
      else if (type == S_OP_VAR_PATH) {
        return new SchemaOpVarPathImpl(node);
      }
      else if (type == S_OUTPUT_PROJECTION_DEF) {
        return new SchemaOutputProjectionDefImpl(node);
      }
      else if (type == S_PRIMITIVE_DATUM) {
        return new SchemaPrimitiveDatumImpl(node);
      }
      else if (type == S_PRIMITIVE_TYPE_BODY) {
        return new SchemaPrimitiveTypeBodyImpl(node);
      }
      else if (type == S_PRIMITIVE_TYPE_DEF) {
        return new SchemaPrimitiveTypeDefImpl(node);
      }
      else if (type == S_PROJECTION_DEF) {
        return new SchemaProjectionDefImpl(node);
      }
      else if (type == S_QID) {
        return new SchemaQidImpl(node);
      }
      else if (type == S_QN) {
        return new SchemaQnImpl(node);
      }
      else if (type == S_QN_SEGMENT) {
        return new SchemaQnSegmentImpl(node);
      }
      else if (type == S_QN_TYPE_REF) {
        return new SchemaQnTypeRefImpl(node);
      }
      else if (type == S_READ_OPERATION_BODY_PART) {
        return new SchemaReadOperationBodyPartImpl(node);
      }
      else if (type == S_READ_OPERATION_DEF) {
        return new SchemaReadOperationDefImpl(node);
      }
      else if (type == S_RECORD_DATUM) {
        return new SchemaRecordDatumImpl(node);
      }
      else if (type == S_RECORD_DATUM_ENTRY) {
        return new SchemaRecordDatumEntryImpl(node);
      }
      else if (type == S_RECORD_TYPE_BODY) {
        return new SchemaRecordTypeBodyImpl(node);
      }
      else if (type == S_RECORD_TYPE_DEF) {
        return new SchemaRecordTypeDefImpl(node);
      }
      else if (type == S_RESOURCE_DEF) {
        return new SchemaResourceDefImpl(node);
      }
      else if (type == S_RESOURCE_NAME) {
        return new SchemaResourceNameImpl(node);
      }
      else if (type == S_RESOURCE_TYPE) {
        return new SchemaResourceTypeImpl(node);
      }
      else if (type == S_RETRO_DECL) {
        return new SchemaRetroDeclImpl(node);
      }
      else if (type == S_SUPPLEMENTS_DECL) {
        return new SchemaSupplementsDeclImpl(node);
      }
      else if (type == S_SUPPLEMENT_DEF) {
        return new SchemaSupplementDefImpl(node);
      }
      else if (type == S_TAG_NAME) {
        return new SchemaTagNameImpl(node);
      }
      else if (type == S_TYPE_DEF_WRAPPER) {
        return new SchemaTypeDefWrapperImpl(node);
      }
      else if (type == S_UPDATE_OPERATION_BODY_PART) {
        return new SchemaUpdateOperationBodyPartImpl(node);
      }
      else if (type == S_UPDATE_OPERATION_DEF) {
        return new SchemaUpdateOperationDefImpl(node);
      }
      else if (type == S_VALUE_TYPE_REF) {
        return new SchemaValueTypeRefImpl(node);
      }
      else if (type == S_VAR_TAG_DECL) {
        return new SchemaVarTagDeclImpl(node);
      }
      else if (type == S_VAR_TAG_REF) {
        return new SchemaVarTagRefImpl(node);
      }
      else if (type == S_VAR_TYPE_BODY) {
        return new SchemaVarTypeBodyImpl(node);
      }
      else if (type == S_VAR_TYPE_DEF) {
        return new SchemaVarTypeDefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
