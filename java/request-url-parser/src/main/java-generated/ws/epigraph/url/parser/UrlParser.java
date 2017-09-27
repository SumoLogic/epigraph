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
package ws.epigraph.url.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static ws.epigraph.url.lexer.UrlElementTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class UrlParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == U_ANNOTATION) {
      r = annotation(b, 0);
    }
    else if (t == U_ANON_LIST) {
      r = anonList(b, 0);
    }
    else if (t == U_ANON_MAP) {
      r = anonMap(b, 0);
    }
    else if (t == U_DATA) {
      r = data(b, 0);
    }
    else if (t == U_DATA_ENTRY) {
      r = dataEntry(b, 0);
    }
    else if (t == U_DATA_VALUE) {
      r = dataValue(b, 0);
    }
    else if (t == U_DATUM) {
      r = datum(b, 0);
    }
    else if (t == U_DEFAULT_OVERRIDE) {
      r = defaultOverride(b, 0);
    }
    else if (t == U_ENUM_DATUM) {
      r = enumDatum(b, 0);
    }
    else if (t == U_INPUT_PROJECTION) {
      r = inputProjection(b, 0);
    }
    else if (t == U_LIST_DATUM) {
      r = listDatum(b, 0);
    }
    else if (t == U_MAP_DATUM) {
      r = mapDatum(b, 0);
    }
    else if (t == U_MAP_DATUM_ENTRY) {
      r = mapDatumEntry(b, 0);
    }
    else if (t == U_NON_READ_URL) {
      r = nonReadUrl(b, 0);
    }
    else if (t == U_NULL_DATUM) {
      r = nullDatum(b, 0);
    }
    else if (t == U_OUTPUT_PROJECTION) {
      r = outputProjection(b, 0);
    }
    else if (t == U_PRIMITIVE_DATUM) {
      r = primitiveDatum(b, 0);
    }
    else if (t == U_QID) {
      r = qid(b, 0);
    }
    else if (t == U_QN) {
      r = qn(b, 0);
    }
    else if (t == U_QN_SEGMENT) {
      r = qnSegment(b, 0);
    }
    else if (t == U_QN_TYPE_REF) {
      r = qnTypeRef(b, 0);
    }
    else if (t == U_READ_URL) {
      r = readUrl(b, 0);
    }
    else if (t == U_RECORD_DATUM) {
      r = recordDatum(b, 0);
    }
    else if (t == U_RECORD_DATUM_ENTRY) {
      r = recordDatumEntry(b, 0);
    }
    else if (t == U_REQ_ANNOTATION) {
      r = reqAnnotation(b, 0);
    }
    else if (t == U_REQ_COMA_ENTITY_PROJECTION) {
      r = reqComaEntityProjection(b, 0);
    }
    else if (t == U_REQ_COMA_ENTITY_PROJECTION_REF) {
      r = reqComaEntityProjectionRef(b, 0);
    }
    else if (t == U_REQ_COMA_FIELD_PROJECTION) {
      r = reqComaFieldProjection(b, 0);
    }
    else if (t == U_REQ_COMA_KEY_PROJECTION) {
      r = reqComaKeyProjection(b, 0);
    }
    else if (t == U_REQ_COMA_KEYS_PROJECTION) {
      r = reqComaKeysProjection(b, 0);
    }
    else if (t == U_REQ_COMA_LIST_MODEL_PROJECTION) {
      r = reqComaListModelProjection(b, 0);
    }
    else if (t == U_REQ_COMA_MAP_MODEL_PROJECTION) {
      r = reqComaMapModelProjection(b, 0);
    }
    else if (t == U_REQ_COMA_MODEL_PROJECTION) {
      r = reqComaModelProjection(b, 0);
    }
    else if (t == U_REQ_COMA_MULTI_TAG_PROJECTION) {
      r = reqComaMultiTagProjection(b, 0);
    }
    else if (t == U_REQ_COMA_MULTI_TAG_PROJECTION_ITEM) {
      r = reqComaMultiTagProjectionItem(b, 0);
    }
    else if (t == U_REQ_COMA_RECORD_MODEL_PROJECTION) {
      r = reqComaRecordModelProjection(b, 0);
    }
    else if (t == U_REQ_COMA_SINGLE_TAG_PROJECTION) {
      r = reqComaSingleTagProjection(b, 0);
    }
    else if (t == U_REQ_ENTITY_MULTI_TAIL) {
      r = reqEntityMultiTail(b, 0);
    }
    else if (t == U_REQ_ENTITY_MULTI_TAIL_ITEM) {
      r = reqEntityMultiTailItem(b, 0);
    }
    else if (t == U_REQ_ENTITY_POLYMORPHIC_TAIL) {
      r = reqEntityPolymorphicTail(b, 0);
    }
    else if (t == U_REQ_ENTITY_SINGLE_TAIL) {
      r = reqEntitySingleTail(b, 0);
    }
    else if (t == U_REQ_FIELD_PATH) {
      r = reqFieldPath(b, 0);
    }
    else if (t == U_REQ_FIELD_PATH_ENTRY) {
      r = reqFieldPathEntry(b, 0);
    }
    else if (t == U_REQ_MAP_MODEL_PATH) {
      r = reqMapModelPath(b, 0);
    }
    else if (t == U_REQ_MODEL_META) {
      r = reqModelMeta(b, 0);
    }
    else if (t == U_REQ_MODEL_MULTI_TAIL) {
      r = reqModelMultiTail(b, 0);
    }
    else if (t == U_REQ_MODEL_MULTI_TAIL_ITEM) {
      r = reqModelMultiTailItem(b, 0);
    }
    else if (t == U_REQ_MODEL_PATH) {
      r = reqModelPath(b, 0);
    }
    else if (t == U_REQ_MODEL_POLYMORPHIC_TAIL) {
      r = reqModelPolymorphicTail(b, 0);
    }
    else if (t == U_REQ_MODEL_SINGLE_TAIL) {
      r = reqModelSingleTail(b, 0);
    }
    else if (t == U_REQ_NAMED_COMA_ENTITY_PROJECTION) {
      r = reqNamedComaEntityProjection(b, 0);
    }
    else if (t == U_REQ_NAMED_TRUNK_ENTITY_PROJECTION) {
      r = reqNamedTrunkEntityProjection(b, 0);
    }
    else if (t == U_REQ_PARAM) {
      r = reqParam(b, 0);
    }
    else if (t == U_REQ_RECORD_MODEL_PATH) {
      r = reqRecordModelPath(b, 0);
    }
    else if (t == U_REQ_STAR_TAG_PROJECTION) {
      r = reqStarTagProjection(b, 0);
    }
    else if (t == U_REQ_TRUNK_ENTITY_PROJECTION) {
      r = reqTrunkEntityProjection(b, 0);
    }
    else if (t == U_REQ_TRUNK_ENTITY_PROJECTION_REF) {
      r = reqTrunkEntityProjectionRef(b, 0);
    }
    else if (t == U_REQ_TRUNK_FIELD_PROJECTION) {
      r = reqTrunkFieldProjection(b, 0);
    }
    else if (t == U_REQ_TRUNK_MAP_MODEL_PROJECTION) {
      r = reqTrunkMapModelProjection(b, 0);
    }
    else if (t == U_REQ_TRUNK_MODEL_PROJECTION) {
      r = reqTrunkModelProjection(b, 0);
    }
    else if (t == U_REQ_TRUNK_RECORD_MODEL_PROJECTION) {
      r = reqTrunkRecordModelProjection(b, 0);
    }
    else if (t == U_REQ_TRUNK_SINGLE_TAG_PROJECTION) {
      r = reqTrunkSingleTagProjection(b, 0);
    }
    else if (t == U_REQ_UNNAMED_COMA_ENTITY_PROJECTION) {
      r = reqUnnamedComaEntityProjection(b, 0);
    }
    else if (t == U_REQ_UNNAMED_OR_REF_COMA_ENTITY_PROJECTION) {
      r = reqUnnamedOrRefComaEntityProjection(b, 0);
    }
    else if (t == U_REQ_UNNAMED_OR_REF_TRUNK_ENTITY_PROJECTION) {
      r = reqUnnamedOrRefTrunkEntityProjection(b, 0);
    }
    else if (t == U_REQ_UNNAMED_TRUNK_ENTITY_PROJECTION) {
      r = reqUnnamedTrunkEntityProjection(b, 0);
    }
    else if (t == U_REQ_VAR_PATH) {
      r = reqVarPath(b, 0);
    }
    else if (t == U_REQUEST_PARAM) {
      r = requestParam(b, 0);
    }
    else if (t == U_TAG_NAME) {
      r = tagName(b, 0);
    }
    else if (t == U_TYPE_REF) {
      r = typeRef(b, 0);
    }
    else if (t == U_URL) {
      r = url(b, 0);
    }
    else if (t == U_VALUE_TYPE_REF) {
      r = valueTypeRef(b, 0);
    }
    else if (t == U_VAR_TAG_REF) {
      r = varTagRef(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(U_REQ_COMA_MODEL_PROJECTION, U_REQ_TRUNK_MODEL_PROJECTION),
    create_token_set_(U_NON_READ_URL, U_READ_URL, U_URL),
    create_token_set_(U_ANON_LIST, U_ANON_MAP, U_QN_TYPE_REF, U_TYPE_REF),
    create_token_set_(U_DATUM, U_ENUM_DATUM, U_LIST_DATUM, U_MAP_DATUM,
      U_NULL_DATUM, U_PRIMITIVE_DATUM, U_RECORD_DATUM),
  };

  /* ********************************************************** */
  // qid '=' dataValue
  public static boolean annotation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotation")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_ANNOTATION, "<custom annotation>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, U_EQ);
    p = r; // pin = 2
    r = r && dataValue(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'list' '[' valueTypeRef ']'
  public static boolean anonList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonList")) return false;
    if (!nextTokenIs(b, U_LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_ANON_LIST, null);
    r = consumeTokens(b, 1, U_LIST, U_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, valueTypeRef(b, l + 1));
    r = p && consumeToken(b, U_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'map' '[' typeRef ',' valueTypeRef ']'
  public static boolean anonMap(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonMap")) return false;
    if (!nextTokenIs(b, U_MAP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_ANON_MAP, null);
    r = consumeTokens(b, 1, U_MAP, U_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, typeRef(b, l + 1));
    r = p && report_error_(b, consumeToken(b, U_COMMA)) && r;
    r = p && report_error_(b, valueTypeRef(b, l + 1)) && r;
    r = p && consumeToken(b, U_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // dataTypeSpec? '<' dataEntry* '>'
  public static boolean data(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "data")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_DATA, "<data>");
    r = data_0(b, l + 1);
    r = r && consumeToken(b, U_ANGLE_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, data_2(b, l + 1));
    r = p && consumeToken(b, U_ANGLE_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // dataTypeSpec?
  private static boolean data_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "data_0")) return false;
    dataTypeSpec(b, l + 1);
    return true;
  }

  // dataEntry*
  private static boolean data_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "data_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!dataEntry(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "data_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // qid ':' datum ','?
  public static boolean dataEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataEntry")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_DATA_ENTRY, "<data entry>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, U_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, datum(b, l + 1));
    r = p && dataEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean dataEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataEntry_3")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeRef
  static boolean dataTypeSpec(PsiBuilder b, int l) {
    return typeRef(b, l + 1);
  }

  /* ********************************************************** */
  // data | datum
  public static boolean dataValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_DATA_VALUE, "<data value>");
    r = data(b, l + 1);
    if (!r) r = datum(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ! ( '#' | qid | primitiveDatum | '}' | ')' | '>' | ']' | ',' | '?' )
  static boolean dataValueRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !dataValueRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '#' | qid | primitiveDatum | '}' | ')' | '>' | ']' | ',' | '?'
  private static boolean dataValueRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_HASH);
    if (!r) r = qid(b, l + 1);
    if (!r) r = primitiveDatum(b, l + 1);
    if (!r) r = consumeToken(b, U_CURLY_RIGHT);
    if (!r) r = consumeToken(b, U_PAREN_RIGHT);
    if (!r) r = consumeToken(b, U_ANGLE_RIGHT);
    if (!r) r = consumeToken(b, U_BRACKET_RIGHT);
    if (!r) r = consumeToken(b, U_COMMA);
    if (!r) r = consumeToken(b, U_QMARK);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // recordDatum | mapDatum | listDatum | primitiveDatum | enumDatum | nullDatum
  public static boolean datum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "datum")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, U_DATUM, "<datum>");
    r = recordDatum(b, l + 1);
    if (!r) r = mapDatum(b, l + 1);
    if (!r) r = listDatum(b, l + 1);
    if (!r) r = primitiveDatum(b, l + 1);
    if (!r) r = enumDatum(b, l + 1);
    if (!r) r = nullDatum(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'default' varTagRef
  public static boolean defaultOverride(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defaultOverride")) return false;
    if (!nextTokenIs(b, U_DEFAULT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_DEFAULT_OVERRIDE, null);
    r = consumeToken(b, U_DEFAULT);
    p = r; // pin = 1
    r = r && varTagRef(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '#' qid
  public static boolean enumDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumDatum")) return false;
    if (!nextTokenIs(b, U_HASH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_ENUM_DATUM, null);
    r = consumeToken(b, U_HASH);
    p = r; // pin = 1
    r = r && qid(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '<' '+'? reqTrunkFieldProjection
  public static boolean inputProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputProjection")) return false;
    if (!nextTokenIs(b, U_ANGLE_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_INPUT_PROJECTION, "<input projection>");
    r = consumeToken(b, U_ANGLE_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, inputProjection_1(b, l + 1));
    r = p && reqTrunkFieldProjection(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean inputProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputProjection_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // dataTypeSpec? '[' (dataValue ','?)* ']'
  public static boolean listDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listDatum")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_LIST_DATUM, "<list datum>");
    r = listDatum_0(b, l + 1);
    r = r && consumeToken(b, U_BRACKET_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, listDatum_2(b, l + 1));
    r = p && consumeToken(b, U_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // dataTypeSpec?
  private static boolean listDatum_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listDatum_0")) return false;
    dataTypeSpec(b, l + 1);
    return true;
  }

  // (dataValue ','?)*
  private static boolean listDatum_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listDatum_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!listDatum_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "listDatum_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // dataValue ','?
  private static boolean listDatum_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listDatum_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dataValue(b, l + 1);
    r = r && listDatum_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean listDatum_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listDatum_2_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // dataTypeSpec? '(' mapDatumEntry* ')'
  public static boolean mapDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapDatum")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_MAP_DATUM, "<map datum>");
    r = mapDatum_0(b, l + 1);
    r = r && consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, mapDatum_2(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // dataTypeSpec?
  private static boolean mapDatum_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapDatum_0")) return false;
    dataTypeSpec(b, l + 1);
    return true;
  }

  // mapDatumEntry*
  private static boolean mapDatum_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapDatum_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!mapDatumEntry(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "mapDatum_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // datum ':' dataValue ','?
  public static boolean mapDatumEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapDatumEntry")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_MAP_DATUM_ENTRY, "<map datum entry>");
    r = datum(b, l + 1);
    r = r && consumeToken(b, U_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataValue(b, l + 1));
    r = p && mapDatumEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean mapDatumEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapDatumEntry_3")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '/' qid reqFieldPath inputProjection? outputProjection? requestParams
  public static boolean nonReadUrl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonReadUrl")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && qid(b, l + 1);
    r = r && reqFieldPath(b, l + 1);
    r = r && nonReadUrl_3(b, l + 1);
    r = r && nonReadUrl_4(b, l + 1);
    r = r && requestParams(b, l + 1);
    exit_section_(b, m, U_NON_READ_URL, r);
    return r;
  }

  // inputProjection?
  private static boolean nonReadUrl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonReadUrl_3")) return false;
    inputProjection(b, l + 1);
    return true;
  }

  // outputProjection?
  private static boolean nonReadUrl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonReadUrl_4")) return false;
    outputProjection(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (dataTypeSpec '@')? 'null'
  public static boolean nullDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nullDatum")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_NULL_DATUM, "<null datum>");
    r = nullDatum_0(b, l + 1);
    r = r && consumeToken(b, U_NULL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (dataTypeSpec '@')?
  private static boolean nullDatum_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nullDatum_0")) return false;
    nullDatum_0_0(b, l + 1);
    return true;
  }

  // dataTypeSpec '@'
  private static boolean nullDatum_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nullDatum_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dataTypeSpec(b, l + 1);
    r = r && consumeToken(b, U_AT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '>' '+'? reqTrunkFieldProjection
  public static boolean outputProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "outputProjection")) return false;
    if (!nextTokenIs(b, U_ANGLE_RIGHT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_OUTPUT_PROJECTION, "<output projection>");
    r = consumeToken(b, U_ANGLE_RIGHT);
    p = r; // pin = 1
    r = r && report_error_(b, outputProjection_1(b, l + 1));
    r = p && reqTrunkFieldProjection(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean outputProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "outputProjection_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // (dataTypeSpec '@')? (string | number | boolean)
  public static boolean primitiveDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveDatum")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_PRIMITIVE_DATUM, "<primitive datum>");
    r = primitiveDatum_0(b, l + 1);
    r = r && primitiveDatum_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (dataTypeSpec '@')?
  private static boolean primitiveDatum_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveDatum_0")) return false;
    primitiveDatum_0_0(b, l + 1);
    return true;
  }

  // dataTypeSpec '@'
  private static boolean primitiveDatum_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveDatum_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dataTypeSpec(b, l + 1);
    r = r && consumeToken(b, U_AT);
    exit_section_(b, m, null, r);
    return r;
  }

  // string | number | boolean
  private static boolean primitiveDatum_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveDatum_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_STRING);
    if (!r) r = consumeToken(b, U_NUMBER);
    if (!r) r = consumeToken(b, U_BOOLEAN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // id
  public static boolean qid(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qid")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_ID);
    exit_section_(b, m, U_QID, r);
    return r;
  }

  /* ********************************************************** */
  // qnSegment ('.' qnSegment)*
  public static boolean qn(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qn")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qnSegment(b, l + 1);
    r = r && qn_1(b, l + 1);
    exit_section_(b, m, U_QN, r);
    return r;
  }

  // ('.' qnSegment)*
  private static boolean qn_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qn_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!qn_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "qn_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // '.' qnSegment
  private static boolean qn_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qn_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_DOT);
    r = r && qnSegment(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qid
  public static boolean qnSegment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qnSegment")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, U_QN_SEGMENT, r);
    return r;
  }

  /* ********************************************************** */
  // qn
  public static boolean qnTypeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qnTypeRef")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qn(b, l + 1);
    exit_section_(b, m, U_QN_TYPE_REF, r);
    return r;
  }

  /* ********************************************************** */
  // '/' qid reqTrunkFieldProjection requestParams
  public static boolean readUrl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readUrl")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && qid(b, l + 1);
    r = r && reqTrunkFieldProjection(b, l + 1);
    r = r && requestParams(b, l + 1);
    exit_section_(b, m, U_READ_URL, r);
    return r;
  }

  /* ********************************************************** */
  // dataTypeSpec? '{' recordDatumEntry* '}'
  public static boolean recordDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordDatum")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_RECORD_DATUM, "<record datum>");
    r = recordDatum_0(b, l + 1);
    r = r && consumeToken(b, U_CURLY_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, recordDatum_2(b, l + 1));
    r = p && consumeToken(b, U_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // dataTypeSpec?
  private static boolean recordDatum_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordDatum_0")) return false;
    dataTypeSpec(b, l + 1);
    return true;
  }

  // recordDatumEntry*
  private static boolean recordDatum_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordDatum_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!recordDatumEntry(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "recordDatum_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // qid ':' dataValue ','?
  public static boolean recordDatumEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordDatumEntry")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_RECORD_DATUM_ENTRY, "<record datum entry>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, U_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataValue(b, l + 1));
    r = p && recordDatumEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean recordDatumEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordDatumEntry_3")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '!' annotation
  public static boolean reqAnnotation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqAnnotation")) return false;
    if (!nextTokenIs(b, U_BANG)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_BANG);
    r = r && annotation(b, l + 1);
    exit_section_(b, m, U_REQ_ANNOTATION, r);
    return r;
  }

  /* ********************************************************** */
  // '(' reqComaEntityProjection ')'
  static boolean reqBracedComaEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqBracedComaEntityProjection")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_PAREN_LEFT);
    r = r && reqComaEntityProjection(b, l + 1);
    r = r && consumeToken(b, U_PAREN_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // reqNamedComaEntityProjection | reqUnnamedOrRefComaEntityProjection
  public static boolean reqComaEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaEntityProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_COMA_ENTITY_PROJECTION, "<req coma entity projection>");
    r = reqNamedComaEntityProjection(b, l + 1);
    if (!r) r = reqUnnamedOrRefComaEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '$' qid
  public static boolean reqComaEntityProjectionRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaEntityProjectionRef")) return false;
    if (!nextTokenIs(b, U_DOLLAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_COMA_ENTITY_PROJECTION_REF, null);
    r = consumeToken(b, U_DOLLAR);
    p = r; // pin = 1
    r = r && qid(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '+'? qid /* reqParamsAndAnnotations */ reqComaEntityProjection
  public static boolean reqComaFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaFieldProjection")) return false;
    if (!nextTokenIs(b, "<req coma field projection>", U_PLUS, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_COMA_FIELD_PROJECTION, "<req coma field projection>");
    r = reqComaFieldProjection_0(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && reqComaEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqComaFieldProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaFieldProjection_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // datum reqParamsAndAnnotations
  public static boolean reqComaKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaKeyProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_COMA_KEY_PROJECTION, "<req coma key projection>");
    r = datum(b, l + 1);
    r = r && reqParamsAndAnnotations(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' ( '*' | ( ( reqComaKeyProjection ','? )* ) ) ']'
  public static boolean reqComaKeysProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaKeysProjection")) return false;
    if (!nextTokenIs(b, U_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_COMA_KEYS_PROJECTION, null);
    r = consumeToken(b, U_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqComaKeysProjection_1(b, l + 1));
    r = p && consumeToken(b, U_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '*' | ( ( reqComaKeyProjection ','? )* )
  private static boolean reqComaKeysProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaKeysProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_STAR);
    if (!r) r = reqComaKeysProjection_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( reqComaKeyProjection ','? )*
  private static boolean reqComaKeysProjection_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaKeysProjection_1_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqComaKeysProjection_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqComaKeysProjection_1_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqComaKeyProjection ','?
  private static boolean reqComaKeysProjection_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaKeysProjection_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqComaKeyProjection(b, l + 1);
    r = r && reqComaKeysProjection_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqComaKeysProjection_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaKeysProjection_1_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '*' '+'? ( ( reqBracedComaEntityProjection | reqComaEntityProjection ) )?
  public static boolean reqComaListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaListModelProjection")) return false;
    if (!nextTokenIs(b, U_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_COMA_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, U_STAR);
    p = r; // pin = 1
    r = r && report_error_(b, reqComaListModelProjection_1(b, l + 1));
    r = p && reqComaListModelProjection_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean reqComaListModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaListModelProjection_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  // ( ( reqBracedComaEntityProjection | reqComaEntityProjection ) )?
  private static boolean reqComaListModelProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaListModelProjection_2")) return false;
    reqComaListModelProjection_2_0(b, l + 1);
    return true;
  }

  // reqBracedComaEntityProjection | reqComaEntityProjection
  private static boolean reqComaListModelProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaListModelProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqBracedComaEntityProjection(b, l + 1);
    if (!r) r = reqComaEntityProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // reqComaKeysProjection '+'? ( ( reqBracedComaEntityProjection | reqComaEntityProjection ) )?
  public static boolean reqComaMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaMapModelProjection")) return false;
    if (!nextTokenIs(b, U_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqComaKeysProjection(b, l + 1);
    r = r && reqComaMapModelProjection_1(b, l + 1);
    r = r && reqComaMapModelProjection_2(b, l + 1);
    exit_section_(b, m, U_REQ_COMA_MAP_MODEL_PROJECTION, r);
    return r;
  }

  // '+'?
  private static boolean reqComaMapModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaMapModelProjection_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  // ( ( reqBracedComaEntityProjection | reqComaEntityProjection ) )?
  private static boolean reqComaMapModelProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaMapModelProjection_2")) return false;
    reqComaMapModelProjection_2_0(b, l + 1);
    return true;
  }

  // reqBracedComaEntityProjection | reqComaEntityProjection
  private static boolean reqComaMapModelProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaMapModelProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqBracedComaEntityProjection(b, l + 1);
    if (!r) r = reqComaEntityProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( ( reqComaRecordModelProjection
  //                              | reqComaListModelProjection
  //                              | reqComaMapModelProjection
  //                              ) reqModelPolymorphicTail ?
  //                            )?
  public static boolean reqComaModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_COMA_MODEL_PROJECTION, "<req coma model projection>");
    reqComaModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // ( reqComaRecordModelProjection
  //                              | reqComaListModelProjection
  //                              | reqComaMapModelProjection
  //                              ) reqModelPolymorphicTail ?
  private static boolean reqComaModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqComaModelProjection_0_0(b, l + 1);
    r = r && reqComaModelProjection_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqComaRecordModelProjection
  //                              | reqComaListModelProjection
  //                              | reqComaMapModelProjection
  private static boolean reqComaModelProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaModelProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqComaRecordModelProjection(b, l + 1);
    if (!r) r = reqComaListModelProjection(b, l + 1);
    if (!r) r = reqComaMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqModelPolymorphicTail ?
  private static boolean reqComaModelProjection_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaModelProjection_0_1")) return false;
    reqModelPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // reqParamsAndAnnotations reqComaModelProjection reqModelMeta?
  static boolean reqComaModelProjectionWithProperties(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaModelProjectionWithProperties")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqParamsAndAnnotations(b, l + 1);
    r = r && reqComaModelProjection(b, l + 1);
    r = r && reqComaModelProjectionWithProperties_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqModelMeta?
  private static boolean reqComaModelProjectionWithProperties_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaModelProjectionWithProperties_2")) return false;
    reqModelMeta(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ':' '(' (reqComaMultiTagProjectionItem ','?)* ')'
  public static boolean reqComaMultiTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaMultiTagProjection")) return false;
    if (!nextTokenIs(b, U_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_COMA_MULTI_TAG_PROJECTION, null);
    r = consumeTokens(b, 2, U_COLON, U_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, reqComaMultiTagProjection_2(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqComaMultiTagProjectionItem ','?)*
  private static boolean reqComaMultiTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaMultiTagProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqComaMultiTagProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqComaMultiTagProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqComaMultiTagProjectionItem ','?
  private static boolean reqComaMultiTagProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaMultiTagProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqComaMultiTagProjectionItem(b, l + 1);
    r = r && reqComaMultiTagProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqComaMultiTagProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaMultiTagProjection_2_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? tagName reqComaModelProjectionWithProperties
  public static boolean reqComaMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaMultiTagProjectionItem")) return false;
    if (!nextTokenIs(b, "<req coma multi tag projection item>", U_PLUS, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_COMA_MULTI_TAG_PROJECTION_ITEM, "<req coma multi tag projection item>");
    r = reqComaMultiTagProjectionItem_0(b, l + 1);
    r = r && tagName(b, l + 1);
    r = r && reqComaModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqComaMultiTagProjectionItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaMultiTagProjectionItem_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '(' ( '*' | ( (reqComaFieldProjection ','?)* ) ) ')'
  public static boolean reqComaRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaRecordModelProjection")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_COMA_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqComaRecordModelProjection_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '*' | ( (reqComaFieldProjection ','?)* )
  private static boolean reqComaRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaRecordModelProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_STAR);
    if (!r) r = reqComaRecordModelProjection_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (reqComaFieldProjection ','?)*
  private static boolean reqComaRecordModelProjection_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaRecordModelProjection_1_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqComaRecordModelProjection_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqComaRecordModelProjection_1_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqComaFieldProjection ','?
  private static boolean reqComaRecordModelProjection_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaRecordModelProjection_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqComaFieldProjection(b, l + 1);
    r = r && reqComaRecordModelProjection_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqComaRecordModelProjection_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaRecordModelProjection_1_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ( ( ':' '+'? tagName) | '+' )? reqComaModelProjectionWithProperties
  public static boolean reqComaSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_COMA_SINGLE_TAG_PROJECTION, "<req coma single tag projection>");
    r = reqComaSingleTagProjection_0(b, l + 1);
    r = r && reqComaModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ( ':' '+'? tagName) | '+' )?
  private static boolean reqComaSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaSingleTagProjection_0")) return false;
    reqComaSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ( ':' '+'? tagName) | '+'
  private static boolean reqComaSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqComaSingleTagProjection_0_0_0(b, l + 1);
    if (!r) r = consumeToken(b, U_PLUS);
    exit_section_(b, m, null, r);
    return r;
  }

  // ':' '+'? tagName
  private static boolean reqComaSingleTagProjection_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaSingleTagProjection_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_COLON);
    r = r && reqComaSingleTagProjection_0_0_0_1(b, l + 1);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean reqComaSingleTagProjection_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqComaSingleTagProjection_0_0_0_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '(' (reqEntityMultiTailItem ','?)* ')'
  public static boolean reqEntityMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqEntityMultiTail")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_ENTITY_MULTI_TAIL, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqEntityMultiTail_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqEntityMultiTailItem ','?)*
  private static boolean reqEntityMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqEntityMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqEntityMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqEntityMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqEntityMultiTailItem ','?
  private static boolean reqEntityMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqEntityMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqEntityMultiTailItem(b, l + 1);
    r = r && reqEntityMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqEntityMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqEntityMultiTail_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeRef reqComaEntityProjection
  public static boolean reqEntityMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqEntityMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_ENTITY_MULTI_TAIL_ITEM, "<req entity multi tail item>");
    r = typeRef(b, l + 1);
    r = r && reqComaEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ':' '~' ( reqEntitySingleTail | reqEntityMultiTail )
  public static boolean reqEntityPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqEntityPolymorphicTail")) return false;
    if (!nextTokenIs(b, U_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_ENTITY_POLYMORPHIC_TAIL, null);
    r = consumeTokens(b, 2, U_COLON, U_TILDA);
    p = r; // pin = 2
    r = r && reqEntityPolymorphicTail_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // reqEntitySingleTail | reqEntityMultiTail
  private static boolean reqEntityPolymorphicTail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqEntityPolymorphicTail_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqEntitySingleTail(b, l + 1);
    if (!r) r = reqEntityMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // typeRef reqComaEntityProjection
  public static boolean reqEntitySingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqEntitySingleTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_ENTITY_SINGLE_TAIL, "<req entity single tail>");
    r = typeRef(b, l + 1);
    r = r && reqComaEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // reqVarPath
  public static boolean reqFieldPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqFieldPath")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_FIELD_PATH, "<req field path>");
    r = reqVarPath(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // qid reqFieldPath
  public static boolean reqFieldPathEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqFieldPathEntry")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && reqFieldPath(b, l + 1);
    exit_section_(b, m, U_REQ_FIELD_PATH_ENTRY, r);
    return r;
  }

  /* ********************************************************** */
  // '/' reqTrunkKeyProjection reqVarPath
  public static boolean reqMapModelPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqMapModelPath")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_MAP_MODEL_PATH, null);
    r = consumeToken(b, U_SLASH);
    r = r && reqTrunkKeyProjection(b, l + 1);
    p = r; // pin = 2
    r = r && reqVarPath(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '@' '+'? reqComaModelProjection
  public static boolean reqModelMeta(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelMeta")) return false;
    if (!nextTokenIs(b, U_AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_AT);
    r = r && reqModelMeta_1(b, l + 1);
    r = r && reqComaModelProjection(b, l + 1);
    exit_section_(b, m, U_REQ_MODEL_META, r);
    return r;
  }

  // '+'?
  private static boolean reqModelMeta_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelMeta_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '(' (reqModelMultiTailItem ','?)* ')'
  public static boolean reqModelMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelMultiTail")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_MODEL_MULTI_TAIL, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqModelMultiTail_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqModelMultiTailItem ','?)*
  private static boolean reqModelMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqModelMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqModelMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqModelMultiTailItem ','?
  private static boolean reqModelMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqModelMultiTailItem(b, l + 1);
    r = r && reqModelMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqModelMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelMultiTail_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? typeRef reqComaModelProjectionWithProperties
  public static boolean reqModelMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_MODEL_MULTI_TAIL_ITEM, "<req model multi tail item>");
    r = reqModelMultiTailItem_0(b, l + 1);
    r = r && typeRef(b, l + 1);
    r = r && reqComaModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqModelMultiTailItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelMultiTailItem_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // ( reqRecordModelPath
  //                  | reqMapModelPath
  //                  )?
  public static boolean reqModelPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelPath")) return false;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_MODEL_PATH, "<req model path>");
    reqModelPath_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // reqRecordModelPath
  //                  | reqMapModelPath
  private static boolean reqModelPath_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelPath_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqRecordModelPath(b, l + 1);
    if (!r) r = reqMapModelPath(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '~' ( reqModelSingleTail | reqModelMultiTail )
  public static boolean reqModelPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelPolymorphicTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_TILDA);
    r = r && reqModelPolymorphicTail_1(b, l + 1);
    exit_section_(b, m, U_REQ_MODEL_POLYMORPHIC_TAIL, r);
    return r;
  }

  // reqModelSingleTail | reqModelMultiTail
  private static boolean reqModelPolymorphicTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelPolymorphicTail_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqModelSingleTail(b, l + 1);
    if (!r) r = reqModelMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '+'? typeRef reqComaModelProjectionWithProperties
  public static boolean reqModelSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelSingleTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_MODEL_SINGLE_TAIL, "<req model single tail>");
    r = reqModelSingleTail_0(b, l + 1);
    r = r && typeRef(b, l + 1);
    r = r && reqComaModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqModelSingleTail_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqModelSingleTail_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '$' qid '=' reqUnnamedOrRefComaEntityProjection
  public static boolean reqNamedComaEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqNamedComaEntityProjection")) return false;
    if (!nextTokenIs(b, U_DOLLAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_NAMED_COMA_ENTITY_PROJECTION, null);
    r = consumeToken(b, U_DOLLAR);
    r = r && qid(b, l + 1);
    r = r && consumeToken(b, U_EQ);
    p = r; // pin = 3
    r = r && reqUnnamedOrRefComaEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '$' qid '=' reqUnnamedOrRefTrunkEntityProjection
  public static boolean reqNamedTrunkEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqNamedTrunkEntityProjection")) return false;
    if (!nextTokenIs(b, U_DOLLAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_NAMED_TRUNK_ENTITY_PROJECTION, null);
    r = consumeToken(b, U_DOLLAR);
    r = r && qid(b, l + 1);
    r = r && consumeToken(b, U_EQ);
    p = r; // pin = 3
    r = r && reqUnnamedOrRefTrunkEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ';' qid '=' datum
  public static boolean reqParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqParam")) return false;
    if (!nextTokenIs(b, U_SEMICOLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SEMICOLON);
    r = r && qid(b, l + 1);
    r = r && consumeToken(b, U_EQ);
    r = r && datum(b, l + 1);
    exit_section_(b, m, U_REQ_PARAM, r);
    return r;
  }

  /* ********************************************************** */
  // ( reqParam | reqAnnotation )*
  static boolean reqParamsAndAnnotations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqParamsAndAnnotations")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqParamsAndAnnotations_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqParamsAndAnnotations", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqParam | reqAnnotation
  private static boolean reqParamsAndAnnotations_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqParamsAndAnnotations_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqParam(b, l + 1);
    if (!r) r = reqAnnotation(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '/' reqFieldPathEntry
  public static boolean reqRecordModelPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqRecordModelPath")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && reqFieldPathEntry(b, l + 1);
    exit_section_(b, m, U_REQ_RECORD_MODEL_PATH, r);
    return r;
  }

  /* ********************************************************** */
  // ':' '*'
  public static boolean reqStarTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqStarTagProjection")) return false;
    if (!nextTokenIs(b, U_COLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, U_COLON, U_STAR);
    exit_section_(b, m, U_REQ_STAR_TAG_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // reqNamedTrunkEntityProjection | reqUnnamedOrRefTrunkEntityProjection
  public static boolean reqTrunkEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkEntityProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_TRUNK_ENTITY_PROJECTION, "<req trunk entity projection>");
    r = reqNamedTrunkEntityProjection(b, l + 1);
    if (!r) r = reqUnnamedOrRefTrunkEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '$' qid
  public static boolean reqTrunkEntityProjectionRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkEntityProjectionRef")) return false;
    if (!nextTokenIs(b, U_DOLLAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_TRUNK_ENTITY_PROJECTION_REF, null);
    r = consumeToken(b, U_DOLLAR);
    p = r; // pin = 1
    r = r && qid(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // reqTrunkEntityProjection
  public static boolean reqTrunkFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkFieldProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_TRUNK_FIELD_PROJECTION, "<req trunk field projection>");
    r = reqTrunkEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // datum reqParamsAndAnnotations
  static boolean reqTrunkKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkKeyProjection")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = datum(b, l + 1);
    r = r && reqParamsAndAnnotations(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '/' reqTrunkKeyProjection '+'? reqTrunkEntityProjection
  public static boolean reqTrunkMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkMapModelProjection")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && reqTrunkKeyProjection(b, l + 1);
    r = r && reqTrunkMapModelProjection_2(b, l + 1);
    r = r && reqTrunkEntityProjection(b, l + 1);
    exit_section_(b, m, U_REQ_TRUNK_MAP_MODEL_PROJECTION, r);
    return r;
  }

  // '+'?
  private static boolean reqTrunkMapModelProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkMapModelProjection_2")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // ( reqTrunkRecordModelProjection // no tails on paths
  //                             | reqTrunkMapModelProjection
  //                             | ( reqComaRecordModelProjection reqModelPolymorphicTail? )
  //                             | ( reqComaMapModelProjection reqModelPolymorphicTail? )
  //                             | ( reqComaListModelProjection reqModelPolymorphicTail? )
  //                             )?
  public static boolean reqTrunkModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_TRUNK_MODEL_PROJECTION, "<req trunk model projection>");
    reqTrunkModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // reqTrunkRecordModelProjection // no tails on paths
  //                             | reqTrunkMapModelProjection
  //                             | ( reqComaRecordModelProjection reqModelPolymorphicTail? )
  //                             | ( reqComaMapModelProjection reqModelPolymorphicTail? )
  //                             | ( reqComaListModelProjection reqModelPolymorphicTail? )
  private static boolean reqTrunkModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqTrunkRecordModelProjection(b, l + 1);
    if (!r) r = reqTrunkMapModelProjection(b, l + 1);
    if (!r) r = reqTrunkModelProjection_0_2(b, l + 1);
    if (!r) r = reqTrunkModelProjection_0_3(b, l + 1);
    if (!r) r = reqTrunkModelProjection_0_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqComaRecordModelProjection reqModelPolymorphicTail?
  private static boolean reqTrunkModelProjection_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkModelProjection_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqComaRecordModelProjection(b, l + 1);
    r = r && reqTrunkModelProjection_0_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqModelPolymorphicTail?
  private static boolean reqTrunkModelProjection_0_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkModelProjection_0_2_1")) return false;
    reqModelPolymorphicTail(b, l + 1);
    return true;
  }

  // reqComaMapModelProjection reqModelPolymorphicTail?
  private static boolean reqTrunkModelProjection_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkModelProjection_0_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqComaMapModelProjection(b, l + 1);
    r = r && reqTrunkModelProjection_0_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqModelPolymorphicTail?
  private static boolean reqTrunkModelProjection_0_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkModelProjection_0_3_1")) return false;
    reqModelPolymorphicTail(b, l + 1);
    return true;
  }

  // reqComaListModelProjection reqModelPolymorphicTail?
  private static boolean reqTrunkModelProjection_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkModelProjection_0_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqComaListModelProjection(b, l + 1);
    r = r && reqTrunkModelProjection_0_4_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqModelPolymorphicTail?
  private static boolean reqTrunkModelProjection_0_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkModelProjection_0_4_1")) return false;
    reqModelPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // reqParamsAndAnnotations reqTrunkModelProjection
  static boolean reqTrunkModelProjectionWithProperties(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkModelProjectionWithProperties")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqParamsAndAnnotations(b, l + 1);
    r = r && reqTrunkModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '/' '+'? qid reqTrunkFieldProjection
  public static boolean reqTrunkRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkRecordModelProjection")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_TRUNK_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, U_SLASH);
    r = r && reqTrunkRecordModelProjection_1(b, l + 1);
    r = r && qid(b, l + 1);
    p = r; // pin = 3
    r = r && reqTrunkFieldProjection(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean reqTrunkRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkRecordModelProjection_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // ( ( ':' '+'? tagName ) | '+' )? reqTrunkModelProjectionWithProperties reqModelMeta?
  public static boolean reqTrunkSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_TRUNK_SINGLE_TAG_PROJECTION, "<req trunk single tag projection>");
    r = reqTrunkSingleTagProjection_0(b, l + 1);
    r = r && reqTrunkModelProjectionWithProperties(b, l + 1);
    r = r && reqTrunkSingleTagProjection_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ( ':' '+'? tagName ) | '+' )?
  private static boolean reqTrunkSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkSingleTagProjection_0")) return false;
    reqTrunkSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ( ':' '+'? tagName ) | '+'
  private static boolean reqTrunkSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqTrunkSingleTagProjection_0_0_0(b, l + 1);
    if (!r) r = consumeToken(b, U_PLUS);
    exit_section_(b, m, null, r);
    return r;
  }

  // ':' '+'? tagName
  private static boolean reqTrunkSingleTagProjection_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkSingleTagProjection_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_COLON);
    r = r && reqTrunkSingleTagProjection_0_0_0_1(b, l + 1);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean reqTrunkSingleTagProjection_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkSingleTagProjection_0_0_0_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  // reqModelMeta?
  private static boolean reqTrunkSingleTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqTrunkSingleTagProjection_2")) return false;
    reqModelMeta(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( reqStarTagProjection
  //                                       | reqComaMultiTagProjection
  //                                       | reqComaSingleTagProjection
  //                                       ) reqEntityPolymorphicTail?
  public static boolean reqUnnamedComaEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUnnamedComaEntityProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UNNAMED_COMA_ENTITY_PROJECTION, "<req unnamed coma entity projection>");
    r = reqUnnamedComaEntityProjection_0(b, l + 1);
    r = r && reqUnnamedComaEntityProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // reqStarTagProjection
  //                                       | reqComaMultiTagProjection
  //                                       | reqComaSingleTagProjection
  private static boolean reqUnnamedComaEntityProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUnnamedComaEntityProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqStarTagProjection(b, l + 1);
    if (!r) r = reqComaMultiTagProjection(b, l + 1);
    if (!r) r = reqComaSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqEntityPolymorphicTail?
  private static boolean reqUnnamedComaEntityProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUnnamedComaEntityProjection_1")) return false;
    reqEntityPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // reqComaEntityProjectionRef | reqUnnamedComaEntityProjection
  public static boolean reqUnnamedOrRefComaEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUnnamedOrRefComaEntityProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UNNAMED_OR_REF_COMA_ENTITY_PROJECTION, "<req unnamed or ref coma entity projection>");
    r = reqComaEntityProjectionRef(b, l + 1);
    if (!r) r = reqUnnamedComaEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // reqTrunkEntityProjectionRef | reqUnnamedTrunkEntityProjection
  public static boolean reqUnnamedOrRefTrunkEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUnnamedOrRefTrunkEntityProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UNNAMED_OR_REF_TRUNK_ENTITY_PROJECTION, "<req unnamed or ref trunk entity projection>");
    r = reqTrunkEntityProjectionRef(b, l + 1);
    if (!r) r = reqUnnamedTrunkEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( reqStarTagProjection
  //                                     | reqComaMultiTagProjection
  //                                     | reqTrunkSingleTagProjection
  //                                     ) reqEntityPolymorphicTail?
  public static boolean reqUnnamedTrunkEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUnnamedTrunkEntityProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UNNAMED_TRUNK_ENTITY_PROJECTION, "<req unnamed trunk entity projection>");
    r = reqUnnamedTrunkEntityProjection_0(b, l + 1);
    r = r && reqUnnamedTrunkEntityProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // reqStarTagProjection
  //                                     | reqComaMultiTagProjection
  //                                     | reqTrunkSingleTagProjection
  private static boolean reqUnnamedTrunkEntityProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUnnamedTrunkEntityProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqStarTagProjection(b, l + 1);
    if (!r) r = reqComaMultiTagProjection(b, l + 1);
    if (!r) r = reqTrunkSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqEntityPolymorphicTail?
  private static boolean reqUnnamedTrunkEntityProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUnnamedTrunkEntityProjection_1")) return false;
    reqEntityPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( ':' tagName)? reqParamsAndAnnotations reqModelPath
  public static boolean reqVarPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqVarPath")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_VAR_PATH, "<req var path>");
    r = reqVarPath_0(b, l + 1);
    r = r && reqParamsAndAnnotations(b, l + 1);
    r = r && reqModelPath(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' tagName)?
  private static boolean reqVarPath_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqVarPath_0")) return false;
    reqVarPath_0_0(b, l + 1);
    return true;
  }

  // ':' tagName
  private static boolean reqVarPath_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqVarPath_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_COLON);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ('?' | '&') PARAM_NAME '=' datum
  public static boolean requestParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requestParam")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQUEST_PARAM, "<request parameter>");
    r = requestParam_0(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeTokens(b, -1, U_PARAM_NAME, U_EQ));
    r = p && datum(b, l + 1) && r;
    exit_section_(b, l, m, r, p, requestParamRecover_parser_);
    return r || p;
  }

  // '?' | '&'
  private static boolean requestParam_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requestParam_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_QMARK);
    if (!r) r = consumeToken(b, U_AMP);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ! ( '?' | '&' )
  static boolean requestParamRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requestParamRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !requestParamRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '?' | '&'
  private static boolean requestParamRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requestParamRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_QMARK);
    if (!r) r = consumeToken(b, U_AMP);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // requestParam*
  static boolean requestParams(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requestParams")) return false;
    int c = current_position_(b);
    while (true) {
      if (!requestParam(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "requestParams", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // url
  static boolean root(PsiBuilder b, int l) {
    return url(b, l + 1);
  }

  /* ********************************************************** */
  // qid
  public static boolean tagName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tagName")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, U_TAG_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // qnTypeRef | anonList | anonMap
  public static boolean typeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, U_TYPE_REF, "<type>");
    r = qnTypeRef(b, l + 1);
    if (!r) r = anonList(b, l + 1);
    if (!r) r = anonMap(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // readUrl | nonReadUrl
  public static boolean url(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "url")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, U_URL, null);
    r = readUrl(b, l + 1);
    if (!r) r = nonReadUrl(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // typeRef defaultOverride?
  public static boolean valueTypeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueTypeRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_VALUE_TYPE_REF, "<value type ref>");
    r = typeRef(b, l + 1);
    r = r && valueTypeRef_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // defaultOverride?
  private static boolean valueTypeRef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueTypeRef_1")) return false;
    defaultOverride(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // qid
  public static boolean varTagRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTagRef")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, U_VAR_TAG_REF, r);
    return r;
  }

  final static Parser dataValueRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return dataValueRecover(b, l + 1);
    }
  };
  final static Parser requestParamRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return requestParamRecover(b, l + 1);
    }
  };
}
