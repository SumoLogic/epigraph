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
package ws.epigraph.url.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import ws.epigraph.url.parser.psi.impl.*;

public interface UrlElementTypes {

  IElementType U_ANNOTATION = new UrlElementType("U_ANNOTATION");
  IElementType U_ANON_LIST = new UrlElementType("U_ANON_LIST");
  IElementType U_ANON_MAP = new UrlElementType("U_ANON_MAP");
  IElementType U_DATA = new UrlElementType("U_DATA");
  IElementType U_DATA_ENTRY = new UrlElementType("U_DATA_ENTRY");
  IElementType U_DATA_VALUE = new UrlElementType("U_DATA_VALUE");
  IElementType U_DATUM = new UrlElementType("U_DATUM");
  IElementType U_DEFAULT_OVERRIDE = new UrlElementType("U_DEFAULT_OVERRIDE");
  IElementType U_DELETE_URL = new UrlElementType("U_DELETE_URL");
  IElementType U_ENUM_DATUM = new UrlElementType("U_ENUM_DATUM");
  IElementType U_INPUT_PROJECTION = new UrlElementType("U_INPUT_PROJECTION");
  IElementType U_LIST_DATUM = new UrlElementType("U_LIST_DATUM");
  IElementType U_MAP_DATUM = new UrlElementType("U_MAP_DATUM");
  IElementType U_MAP_DATUM_ENTRY = new UrlElementType("U_MAP_DATUM_ENTRY");
  IElementType U_NON_READ_URL = new UrlElementType("U_NON_READ_URL");
  IElementType U_NULL_DATUM = new UrlElementType("U_NULL_DATUM");
  IElementType U_OUTPUT_PROJECTION = new UrlElementType("U_OUTPUT_PROJECTION");
  IElementType U_PRIMITIVE_DATUM = new UrlElementType("U_PRIMITIVE_DATUM");
  IElementType U_QID = new UrlElementType("U_QID");
  IElementType U_QN = new UrlElementType("U_QN");
  IElementType U_QN_SEGMENT = new UrlElementType("U_QN_SEGMENT");
  IElementType U_QN_TYPE_REF = new UrlElementType("U_QN_TYPE_REF");
  IElementType U_READ_URL = new UrlElementType("U_READ_URL");
  IElementType U_RECORD_DATUM = new UrlElementType("U_RECORD_DATUM");
  IElementType U_RECORD_DATUM_ENTRY = new UrlElementType("U_RECORD_DATUM_ENTRY");
  IElementType U_REQUEST_PARAM = new UrlElementType("U_REQUEST_PARAM");
  IElementType U_REQ_ANNOTATION = new UrlElementType("U_REQ_ANNOTATION");
  IElementType U_REQ_DELETE_FIELD_PROJECTION = new UrlElementType("U_REQ_DELETE_FIELD_PROJECTION");
  IElementType U_REQ_DELETE_FIELD_PROJECTION_ENTRY = new UrlElementType("U_REQ_DELETE_FIELD_PROJECTION_ENTRY");
  IElementType U_REQ_DELETE_KEYS_PROJECTION = new UrlElementType("U_REQ_DELETE_KEYS_PROJECTION");
  IElementType U_REQ_DELETE_KEY_PROJECTION = new UrlElementType("U_REQ_DELETE_KEY_PROJECTION");
  IElementType U_REQ_DELETE_LIST_MODEL_PROJECTION = new UrlElementType("U_REQ_DELETE_LIST_MODEL_PROJECTION");
  IElementType U_REQ_DELETE_MAP_MODEL_PROJECTION = new UrlElementType("U_REQ_DELETE_MAP_MODEL_PROJECTION");
  IElementType U_REQ_DELETE_MODEL_MULTI_TAIL = new UrlElementType("U_REQ_DELETE_MODEL_MULTI_TAIL");
  IElementType U_REQ_DELETE_MODEL_MULTI_TAIL_ITEM = new UrlElementType("U_REQ_DELETE_MODEL_MULTI_TAIL_ITEM");
  IElementType U_REQ_DELETE_MODEL_POLYMORPHIC_TAIL = new UrlElementType("U_REQ_DELETE_MODEL_POLYMORPHIC_TAIL");
  IElementType U_REQ_DELETE_MODEL_PROJECTION = new UrlElementType("U_REQ_DELETE_MODEL_PROJECTION");
  IElementType U_REQ_DELETE_MODEL_SINGLE_TAIL = new UrlElementType("U_REQ_DELETE_MODEL_SINGLE_TAIL");
  IElementType U_REQ_DELETE_MULTI_TAG_PROJECTION = new UrlElementType("U_REQ_DELETE_MULTI_TAG_PROJECTION");
  IElementType U_REQ_DELETE_MULTI_TAG_PROJECTION_ITEM = new UrlElementType("U_REQ_DELETE_MULTI_TAG_PROJECTION_ITEM");
  IElementType U_REQ_DELETE_NAMED_VAR_PROJECTION = new UrlElementType("U_REQ_DELETE_NAMED_VAR_PROJECTION");
  IElementType U_REQ_DELETE_RECORD_MODEL_PROJECTION = new UrlElementType("U_REQ_DELETE_RECORD_MODEL_PROJECTION");
  IElementType U_REQ_DELETE_SINGLE_TAG_PROJECTION = new UrlElementType("U_REQ_DELETE_SINGLE_TAG_PROJECTION");
  IElementType U_REQ_DELETE_UNNAMED_OR_REF_VAR_PROJECTION = new UrlElementType("U_REQ_DELETE_UNNAMED_OR_REF_VAR_PROJECTION");
  IElementType U_REQ_DELETE_UNNAMED_VAR_PROJECTION = new UrlElementType("U_REQ_DELETE_UNNAMED_VAR_PROJECTION");
  IElementType U_REQ_DELETE_VAR_MULTI_TAIL = new UrlElementType("U_REQ_DELETE_VAR_MULTI_TAIL");
  IElementType U_REQ_DELETE_VAR_MULTI_TAIL_ITEM = new UrlElementType("U_REQ_DELETE_VAR_MULTI_TAIL_ITEM");
  IElementType U_REQ_DELETE_VAR_POLYMORPHIC_TAIL = new UrlElementType("U_REQ_DELETE_VAR_POLYMORPHIC_TAIL");
  IElementType U_REQ_DELETE_VAR_PROJECTION = new UrlElementType("U_REQ_DELETE_VAR_PROJECTION");
  IElementType U_REQ_DELETE_VAR_PROJECTION_REF = new UrlElementType("U_REQ_DELETE_VAR_PROJECTION_REF");
  IElementType U_REQ_DELETE_VAR_SINGLE_TAIL = new UrlElementType("U_REQ_DELETE_VAR_SINGLE_TAIL");
  IElementType U_REQ_FIELD_PATH = new UrlElementType("U_REQ_FIELD_PATH");
  IElementType U_REQ_FIELD_PATH_ENTRY = new UrlElementType("U_REQ_FIELD_PATH_ENTRY");
  IElementType U_REQ_MAP_MODEL_PATH = new UrlElementType("U_REQ_MAP_MODEL_PATH");
  IElementType U_REQ_MODEL_PATH = new UrlElementType("U_REQ_MODEL_PATH");
  IElementType U_REQ_OUTPUT_COMA_FIELD_PROJECTION = new UrlElementType("U_REQ_OUTPUT_COMA_FIELD_PROJECTION");
  IElementType U_REQ_OUTPUT_COMA_KEYS_PROJECTION = new UrlElementType("U_REQ_OUTPUT_COMA_KEYS_PROJECTION");
  IElementType U_REQ_OUTPUT_COMA_KEY_PROJECTION = new UrlElementType("U_REQ_OUTPUT_COMA_KEY_PROJECTION");
  IElementType U_REQ_OUTPUT_COMA_LIST_MODEL_PROJECTION = new UrlElementType("U_REQ_OUTPUT_COMA_LIST_MODEL_PROJECTION");
  IElementType U_REQ_OUTPUT_COMA_MAP_MODEL_PROJECTION = new UrlElementType("U_REQ_OUTPUT_COMA_MAP_MODEL_PROJECTION");
  IElementType U_REQ_OUTPUT_COMA_MODEL_PROJECTION = new UrlElementType("U_REQ_OUTPUT_COMA_MODEL_PROJECTION");
  IElementType U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION = new UrlElementType("U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION");
  IElementType U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION_ITEM = new UrlElementType("U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION_ITEM");
  IElementType U_REQ_OUTPUT_COMA_RECORD_MODEL_PROJECTION = new UrlElementType("U_REQ_OUTPUT_COMA_RECORD_MODEL_PROJECTION");
  IElementType U_REQ_OUTPUT_COMA_SINGLE_TAG_PROJECTION = new UrlElementType("U_REQ_OUTPUT_COMA_SINGLE_TAG_PROJECTION");
  IElementType U_REQ_OUTPUT_COMA_VAR_PROJECTION = new UrlElementType("U_REQ_OUTPUT_COMA_VAR_PROJECTION");
  IElementType U_REQ_OUTPUT_COMA_VAR_PROJECTION_REF = new UrlElementType("U_REQ_OUTPUT_COMA_VAR_PROJECTION_REF");
  IElementType U_REQ_OUTPUT_MODEL_META = new UrlElementType("U_REQ_OUTPUT_MODEL_META");
  IElementType U_REQ_OUTPUT_MODEL_MULTI_TAIL = new UrlElementType("U_REQ_OUTPUT_MODEL_MULTI_TAIL");
  IElementType U_REQ_OUTPUT_MODEL_MULTI_TAIL_ITEM = new UrlElementType("U_REQ_OUTPUT_MODEL_MULTI_TAIL_ITEM");
  IElementType U_REQ_OUTPUT_MODEL_POLYMORPHIC_TAIL = new UrlElementType("U_REQ_OUTPUT_MODEL_POLYMORPHIC_TAIL");
  IElementType U_REQ_OUTPUT_MODEL_SINGLE_TAIL = new UrlElementType("U_REQ_OUTPUT_MODEL_SINGLE_TAIL");
  IElementType U_REQ_OUTPUT_NAMED_COMA_VAR_PROJECTION = new UrlElementType("U_REQ_OUTPUT_NAMED_COMA_VAR_PROJECTION");
  IElementType U_REQ_OUTPUT_NAMED_TRUNK_VAR_PROJECTION = new UrlElementType("U_REQ_OUTPUT_NAMED_TRUNK_VAR_PROJECTION");
  IElementType U_REQ_OUTPUT_STAR_TAG_PROJECTION = new UrlElementType("U_REQ_OUTPUT_STAR_TAG_PROJECTION");
  IElementType U_REQ_OUTPUT_TRUNK_FIELD_PROJECTION = new UrlElementType("U_REQ_OUTPUT_TRUNK_FIELD_PROJECTION");
  IElementType U_REQ_OUTPUT_TRUNK_MAP_MODEL_PROJECTION = new UrlElementType("U_REQ_OUTPUT_TRUNK_MAP_MODEL_PROJECTION");
  IElementType U_REQ_OUTPUT_TRUNK_MODEL_PROJECTION = new UrlElementType("U_REQ_OUTPUT_TRUNK_MODEL_PROJECTION");
  IElementType U_REQ_OUTPUT_TRUNK_RECORD_MODEL_PROJECTION = new UrlElementType("U_REQ_OUTPUT_TRUNK_RECORD_MODEL_PROJECTION");
  IElementType U_REQ_OUTPUT_TRUNK_SINGLE_TAG_PROJECTION = new UrlElementType("U_REQ_OUTPUT_TRUNK_SINGLE_TAG_PROJECTION");
  IElementType U_REQ_OUTPUT_TRUNK_VAR_PROJECTION = new UrlElementType("U_REQ_OUTPUT_TRUNK_VAR_PROJECTION");
  IElementType U_REQ_OUTPUT_TRUNK_VAR_PROJECTION_REF = new UrlElementType("U_REQ_OUTPUT_TRUNK_VAR_PROJECTION_REF");
  IElementType U_REQ_OUTPUT_UNNAMED_COMA_VAR_PROJECTION = new UrlElementType("U_REQ_OUTPUT_UNNAMED_COMA_VAR_PROJECTION");
  IElementType U_REQ_OUTPUT_UNNAMED_OR_REF_COMA_VAR_PROJECTION = new UrlElementType("U_REQ_OUTPUT_UNNAMED_OR_REF_COMA_VAR_PROJECTION");
  IElementType U_REQ_OUTPUT_UNNAMED_OR_REF_TRUNK_VAR_PROJECTION = new UrlElementType("U_REQ_OUTPUT_UNNAMED_OR_REF_TRUNK_VAR_PROJECTION");
  IElementType U_REQ_OUTPUT_UNNAMED_TRUNK_VAR_PROJECTION = new UrlElementType("U_REQ_OUTPUT_UNNAMED_TRUNK_VAR_PROJECTION");
  IElementType U_REQ_OUTPUT_VAR_MULTI_TAIL = new UrlElementType("U_REQ_OUTPUT_VAR_MULTI_TAIL");
  IElementType U_REQ_OUTPUT_VAR_MULTI_TAIL_ITEM = new UrlElementType("U_REQ_OUTPUT_VAR_MULTI_TAIL_ITEM");
  IElementType U_REQ_OUTPUT_VAR_POLYMORPHIC_TAIL = new UrlElementType("U_REQ_OUTPUT_VAR_POLYMORPHIC_TAIL");
  IElementType U_REQ_OUTPUT_VAR_SINGLE_TAIL = new UrlElementType("U_REQ_OUTPUT_VAR_SINGLE_TAIL");
  IElementType U_REQ_PARAM = new UrlElementType("U_REQ_PARAM");
  IElementType U_REQ_RECORD_MODEL_PATH = new UrlElementType("U_REQ_RECORD_MODEL_PATH");
  IElementType U_REQ_VAR_PATH = new UrlElementType("U_REQ_VAR_PATH");
  IElementType U_TAG_NAME = new UrlElementType("U_TAG_NAME");
  IElementType U_TYPE_REF = new UrlElementType("U_TYPE_REF");
  IElementType U_URL = new UrlElementType("U_URL");
  IElementType U_VALUE_TYPE_REF = new UrlElementType("U_VALUE_TYPE_REF");
  IElementType U_VAR_TAG_REF = new UrlElementType("U_VAR_TAG_REF");

  IElementType U_AMP = new UrlElementType("&");
  IElementType U_ANGLE_LEFT = new UrlElementType("<");
  IElementType U_ANGLE_RIGHT = new UrlElementType(">");
  IElementType U_AT = new UrlElementType("@");
  IElementType U_BANG = new UrlElementType("!");
  IElementType U_BLOCK_COMMENT = new UrlElementType("block_comment");
  IElementType U_BOOLEAN = new UrlElementType("boolean");
  IElementType U_BRACKET_LEFT = new UrlElementType("[");
  IElementType U_BRACKET_RIGHT = new UrlElementType("]");
  IElementType U_COLON = new UrlElementType(":");
  IElementType U_COMMA = new UrlElementType(",");
  IElementType U_CURLY_LEFT = new UrlElementType("{");
  IElementType U_CURLY_RIGHT = new UrlElementType("}");
  IElementType U_DEFAULT = new UrlElementType("default");
  IElementType U_DOLLAR = new UrlElementType("$");
  IElementType U_DOT = new UrlElementType(".");
  IElementType U_EQ = new UrlElementType("=");
  IElementType U_HASH = new UrlElementType("#");
  IElementType U_ID = new UrlElementType("id");
  IElementType U_LIST = new UrlElementType("list");
  IElementType U_MAP = new UrlElementType("map");
  IElementType U_NULL = new UrlElementType("null");
  IElementType U_NUMBER = new UrlElementType("number");
  IElementType U_PARAM_NAME = new UrlElementType("PARAM_NAME");
  IElementType U_PAREN_LEFT = new UrlElementType("(");
  IElementType U_PAREN_RIGHT = new UrlElementType(")");
  IElementType U_PLUS = new UrlElementType("+");
  IElementType U_QMARK = new UrlElementType("?");
  IElementType U_SEMICOLON = new UrlElementType(";");
  IElementType U_SLASH = new UrlElementType("/");
  IElementType U_STAR = new UrlElementType("*");
  IElementType U_STRING = new UrlElementType("string");
  IElementType U_TILDA = new UrlElementType("~");
  IElementType U_UNDERSCORE = new UrlElementType("_");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == U_ANNOTATION) {
        return new UrlAnnotationImpl(node);
      }
      else if (type == U_ANON_LIST) {
        return new UrlAnonListImpl(node);
      }
      else if (type == U_ANON_MAP) {
        return new UrlAnonMapImpl(node);
      }
      else if (type == U_DATA) {
        return new UrlDataImpl(node);
      }
      else if (type == U_DATA_ENTRY) {
        return new UrlDataEntryImpl(node);
      }
      else if (type == U_DATA_VALUE) {
        return new UrlDataValueImpl(node);
      }
      else if (type == U_DEFAULT_OVERRIDE) {
        return new UrlDefaultOverrideImpl(node);
      }
      else if (type == U_DELETE_URL) {
        return new UrlDeleteUrlImpl(node);
      }
      else if (type == U_ENUM_DATUM) {
        return new UrlEnumDatumImpl(node);
      }
      else if (type == U_INPUT_PROJECTION) {
        return new UrlInputProjectionImpl(node);
      }
      else if (type == U_LIST_DATUM) {
        return new UrlListDatumImpl(node);
      }
      else if (type == U_MAP_DATUM) {
        return new UrlMapDatumImpl(node);
      }
      else if (type == U_MAP_DATUM_ENTRY) {
        return new UrlMapDatumEntryImpl(node);
      }
      else if (type == U_NON_READ_URL) {
        return new UrlNonReadUrlImpl(node);
      }
      else if (type == U_NULL_DATUM) {
        return new UrlNullDatumImpl(node);
      }
      else if (type == U_OUTPUT_PROJECTION) {
        return new UrlOutputProjectionImpl(node);
      }
      else if (type == U_PRIMITIVE_DATUM) {
        return new UrlPrimitiveDatumImpl(node);
      }
      else if (type == U_QID) {
        return new UrlQidImpl(node);
      }
      else if (type == U_QN) {
        return new UrlQnImpl(node);
      }
      else if (type == U_QN_SEGMENT) {
        return new UrlQnSegmentImpl(node);
      }
      else if (type == U_QN_TYPE_REF) {
        return new UrlQnTypeRefImpl(node);
      }
      else if (type == U_READ_URL) {
        return new UrlReadUrlImpl(node);
      }
      else if (type == U_RECORD_DATUM) {
        return new UrlRecordDatumImpl(node);
      }
      else if (type == U_RECORD_DATUM_ENTRY) {
        return new UrlRecordDatumEntryImpl(node);
      }
      else if (type == U_REQUEST_PARAM) {
        return new UrlRequestParamImpl(node);
      }
      else if (type == U_REQ_ANNOTATION) {
        return new UrlReqAnnotationImpl(node);
      }
      else if (type == U_REQ_DELETE_FIELD_PROJECTION) {
        return new UrlReqDeleteFieldProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_FIELD_PROJECTION_ENTRY) {
        return new UrlReqDeleteFieldProjectionEntryImpl(node);
      }
      else if (type == U_REQ_DELETE_KEYS_PROJECTION) {
        return new UrlReqDeleteKeysProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_KEY_PROJECTION) {
        return new UrlReqDeleteKeyProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_LIST_MODEL_PROJECTION) {
        return new UrlReqDeleteListModelProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_MAP_MODEL_PROJECTION) {
        return new UrlReqDeleteMapModelProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_MODEL_MULTI_TAIL) {
        return new UrlReqDeleteModelMultiTailImpl(node);
      }
      else if (type == U_REQ_DELETE_MODEL_MULTI_TAIL_ITEM) {
        return new UrlReqDeleteModelMultiTailItemImpl(node);
      }
      else if (type == U_REQ_DELETE_MODEL_POLYMORPHIC_TAIL) {
        return new UrlReqDeleteModelPolymorphicTailImpl(node);
      }
      else if (type == U_REQ_DELETE_MODEL_PROJECTION) {
        return new UrlReqDeleteModelProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_MODEL_SINGLE_TAIL) {
        return new UrlReqDeleteModelSingleTailImpl(node);
      }
      else if (type == U_REQ_DELETE_MULTI_TAG_PROJECTION) {
        return new UrlReqDeleteMultiTagProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_MULTI_TAG_PROJECTION_ITEM) {
        return new UrlReqDeleteMultiTagProjectionItemImpl(node);
      }
      else if (type == U_REQ_DELETE_NAMED_VAR_PROJECTION) {
        return new UrlReqDeleteNamedVarProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_RECORD_MODEL_PROJECTION) {
        return new UrlReqDeleteRecordModelProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_SINGLE_TAG_PROJECTION) {
        return new UrlReqDeleteSingleTagProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_UNNAMED_OR_REF_VAR_PROJECTION) {
        return new UrlReqDeleteUnnamedOrRefVarProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_UNNAMED_VAR_PROJECTION) {
        return new UrlReqDeleteUnnamedVarProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_VAR_MULTI_TAIL) {
        return new UrlReqDeleteVarMultiTailImpl(node);
      }
      else if (type == U_REQ_DELETE_VAR_MULTI_TAIL_ITEM) {
        return new UrlReqDeleteVarMultiTailItemImpl(node);
      }
      else if (type == U_REQ_DELETE_VAR_POLYMORPHIC_TAIL) {
        return new UrlReqDeleteVarPolymorphicTailImpl(node);
      }
      else if (type == U_REQ_DELETE_VAR_PROJECTION) {
        return new UrlReqDeleteVarProjectionImpl(node);
      }
      else if (type == U_REQ_DELETE_VAR_PROJECTION_REF) {
        return new UrlReqDeleteVarProjectionRefImpl(node);
      }
      else if (type == U_REQ_DELETE_VAR_SINGLE_TAIL) {
        return new UrlReqDeleteVarSingleTailImpl(node);
      }
      else if (type == U_REQ_FIELD_PATH) {
        return new UrlReqFieldPathImpl(node);
      }
      else if (type == U_REQ_FIELD_PATH_ENTRY) {
        return new UrlReqFieldPathEntryImpl(node);
      }
      else if (type == U_REQ_MAP_MODEL_PATH) {
        return new UrlReqMapModelPathImpl(node);
      }
      else if (type == U_REQ_MODEL_PATH) {
        return new UrlReqModelPathImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_FIELD_PROJECTION) {
        return new UrlReqOutputComaFieldProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_KEYS_PROJECTION) {
        return new UrlReqOutputComaKeysProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_KEY_PROJECTION) {
        return new UrlReqOutputComaKeyProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_LIST_MODEL_PROJECTION) {
        return new UrlReqOutputComaListModelProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_MAP_MODEL_PROJECTION) {
        return new UrlReqOutputComaMapModelProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_MODEL_PROJECTION) {
        return new UrlReqOutputComaModelProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION) {
        return new UrlReqOutputComaMultiTagProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION_ITEM) {
        return new UrlReqOutputComaMultiTagProjectionItemImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_RECORD_MODEL_PROJECTION) {
        return new UrlReqOutputComaRecordModelProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_SINGLE_TAG_PROJECTION) {
        return new UrlReqOutputComaSingleTagProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_VAR_PROJECTION) {
        return new UrlReqOutputComaVarProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_COMA_VAR_PROJECTION_REF) {
        return new UrlReqOutputComaVarProjectionRefImpl(node);
      }
      else if (type == U_REQ_OUTPUT_MODEL_META) {
        return new UrlReqOutputModelMetaImpl(node);
      }
      else if (type == U_REQ_OUTPUT_MODEL_MULTI_TAIL) {
        return new UrlReqOutputModelMultiTailImpl(node);
      }
      else if (type == U_REQ_OUTPUT_MODEL_MULTI_TAIL_ITEM) {
        return new UrlReqOutputModelMultiTailItemImpl(node);
      }
      else if (type == U_REQ_OUTPUT_MODEL_POLYMORPHIC_TAIL) {
        return new UrlReqOutputModelPolymorphicTailImpl(node);
      }
      else if (type == U_REQ_OUTPUT_MODEL_SINGLE_TAIL) {
        return new UrlReqOutputModelSingleTailImpl(node);
      }
      else if (type == U_REQ_OUTPUT_NAMED_COMA_VAR_PROJECTION) {
        return new UrlReqOutputNamedComaVarProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_NAMED_TRUNK_VAR_PROJECTION) {
        return new UrlReqOutputNamedTrunkVarProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_STAR_TAG_PROJECTION) {
        return new UrlReqOutputStarTagProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_TRUNK_FIELD_PROJECTION) {
        return new UrlReqOutputTrunkFieldProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_TRUNK_MAP_MODEL_PROJECTION) {
        return new UrlReqOutputTrunkMapModelProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_TRUNK_MODEL_PROJECTION) {
        return new UrlReqOutputTrunkModelProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_TRUNK_RECORD_MODEL_PROJECTION) {
        return new UrlReqOutputTrunkRecordModelProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_TRUNK_SINGLE_TAG_PROJECTION) {
        return new UrlReqOutputTrunkSingleTagProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_TRUNK_VAR_PROJECTION) {
        return new UrlReqOutputTrunkVarProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_TRUNK_VAR_PROJECTION_REF) {
        return new UrlReqOutputTrunkVarProjectionRefImpl(node);
      }
      else if (type == U_REQ_OUTPUT_UNNAMED_COMA_VAR_PROJECTION) {
        return new UrlReqOutputUnnamedComaVarProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_UNNAMED_OR_REF_COMA_VAR_PROJECTION) {
        return new UrlReqOutputUnnamedOrRefComaVarProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_UNNAMED_OR_REF_TRUNK_VAR_PROJECTION) {
        return new UrlReqOutputUnnamedOrRefTrunkVarProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_UNNAMED_TRUNK_VAR_PROJECTION) {
        return new UrlReqOutputUnnamedTrunkVarProjectionImpl(node);
      }
      else if (type == U_REQ_OUTPUT_VAR_MULTI_TAIL) {
        return new UrlReqOutputVarMultiTailImpl(node);
      }
      else if (type == U_REQ_OUTPUT_VAR_MULTI_TAIL_ITEM) {
        return new UrlReqOutputVarMultiTailItemImpl(node);
      }
      else if (type == U_REQ_OUTPUT_VAR_POLYMORPHIC_TAIL) {
        return new UrlReqOutputVarPolymorphicTailImpl(node);
      }
      else if (type == U_REQ_OUTPUT_VAR_SINGLE_TAIL) {
        return new UrlReqOutputVarSingleTailImpl(node);
      }
      else if (type == U_REQ_PARAM) {
        return new UrlReqParamImpl(node);
      }
      else if (type == U_REQ_RECORD_MODEL_PATH) {
        return new UrlReqRecordModelPathImpl(node);
      }
      else if (type == U_REQ_VAR_PATH) {
        return new UrlReqVarPathImpl(node);
      }
      else if (type == U_TAG_NAME) {
        return new UrlTagNameImpl(node);
      }
      else if (type == U_VALUE_TYPE_REF) {
        return new UrlValueTypeRefImpl(node);
      }
      else if (type == U_VAR_TAG_REF) {
        return new UrlVarTagRefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
