// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static io.epigraph.url.lexer.UrlElementTypes.*;
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
    else if (t == U_LIST_DATUM) {
      r = listDatum(b, 0);
    }
    else if (t == U_MAP_DATUM) {
      r = mapDatum(b, 0);
    }
    else if (t == U_MAP_DATUM_ENTRY) {
      r = mapDatumEntry(b, 0);
    }
    else if (t == U_NULL_DATUM) {
      r = nullDatum(b, 0);
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
    else if (t == U_RECORD_DATUM) {
      r = recordDatum(b, 0);
    }
    else if (t == U_RECORD_DATUM_ENTRY) {
      r = recordDatumEntry(b, 0);
    }
    else if (t == U_REQ_ANNOTATION) {
      r = reqAnnotation(b, 0);
    }
    else if (t == U_REQ_OUTPUT_COMA_FIELD_PROJECTION) {
      r = reqOutputComaFieldProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_COMA_KEY_PROJECTION) {
      r = reqOutputComaKeyProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_COMA_KEYS_PROJECTION) {
      r = reqOutputComaKeysProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_COMA_LIST_MODEL_PROJECTION) {
      r = reqOutputComaListModelProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_COMA_MAP_MODEL_PROJECTION) {
      r = reqOutputComaMapModelProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_COMA_MODEL_PROJECTION) {
      r = reqOutputComaModelProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION) {
      r = reqOutputComaMultiTagProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION_ITEM) {
      r = reqOutputComaMultiTagProjectionItem(b, 0);
    }
    else if (t == U_REQ_OUTPUT_COMA_RECORD_MODEL_PROJECTION) {
      r = reqOutputComaRecordModelProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_COMA_SINGLE_TAG_PROJECTION) {
      r = reqOutputComaSingleTagProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_COMA_VAR_PROJECTION) {
      r = reqOutputComaVarProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_MODEL_META) {
      r = reqOutputModelMeta(b, 0);
    }
    else if (t == U_REQ_OUTPUT_TRUNK_FIELD_PROJECTION) {
      r = reqOutputTrunkFieldProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_TRUNK_MAP_MODEL_PROJECTION) {
      r = reqOutputTrunkMapModelProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_TRUNK_MODEL_PROJECTION) {
      r = reqOutputTrunkModelProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_TRUNK_RECORD_MODEL_PROJECTION) {
      r = reqOutputTrunkRecordModelProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_TRUNK_SINGLE_TAG_PROJECTION) {
      r = reqOutputTrunkSingleTagProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_TRUNK_VAR_PROJECTION) {
      r = reqOutputTrunkVarProjection(b, 0);
    }
    else if (t == U_REQ_OUTPUT_VAR_MULTI_TAIL) {
      r = reqOutputVarMultiTail(b, 0);
    }
    else if (t == U_REQ_OUTPUT_VAR_MULTI_TAIL_ITEM) {
      r = reqOutputVarMultiTailItem(b, 0);
    }
    else if (t == U_REQ_OUTPUT_VAR_POLYMORPHIC_TAIL) {
      r = reqOutputVarPolymorphicTail(b, 0);
    }
    else if (t == U_REQ_OUTPUT_VAR_SINGLE_TAIL) {
      r = reqOutputVarSingleTail(b, 0);
    }
    else if (t == U_REQ_PARAM) {
      r = reqParam(b, 0);
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
    create_token_set_(U_REQ_OUTPUT_COMA_MODEL_PROJECTION, U_REQ_OUTPUT_TRUNK_MODEL_PROJECTION),
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
    r = consumeToken(b, U_LIST);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, U_BRACKET_LEFT));
    r = p && report_error_(b, valueTypeRef(b, l + 1)) && r;
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
    r = consumeToken(b, U_MAP);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, U_BRACKET_LEFT));
    r = p && report_error_(b, typeRef(b, l + 1)) && r;
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
  // ! ( '#' | qid | primitiveDatum | ')' | '>' | ']' | ',' | '?' )
  static boolean dataValueRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !dataValueRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '#' | qid | primitiveDatum | ')' | '>' | ']' | ',' | '?'
  private static boolean dataValueRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_HASH);
    if (!r) r = qid(b, l + 1);
    if (!r) r = primitiveDatum(b, l + 1);
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
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_HASH);
    r = r && qid(b, l + 1);
    exit_section_(b, m, U_ENUM_DATUM, r);
    return r;
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
  // '+'? qid reqParamsAndAnnotations reqOutputComaVarProjection
  public static boolean reqOutputComaFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaFieldProjection")) return false;
    if (!nextTokenIs(b, "<req output coma field projection>", U_PLUS, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_FIELD_PROJECTION, "<req output coma field projection>");
    r = reqOutputComaFieldProjection_0(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && reqParamsAndAnnotations(b, l + 1);
    r = r && reqOutputComaVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqOutputComaFieldProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaFieldProjection_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // datum reqParamsAndAnnotations
  public static boolean reqOutputComaKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeyProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_KEY_PROJECTION, "<req output coma key projection>");
    r = datum(b, l + 1);
    r = r && reqParamsAndAnnotations(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '+'? ( '[' ( reqOutputComaKeyProjection ','? )* ']' ) | ( '[' '*' ']' )
  public static boolean reqOutputComaKeysProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection")) return false;
    if (!nextTokenIs(b, "<req output coma keys projection>", U_PLUS, U_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_KEYS_PROJECTION, "<req output coma keys projection>");
    r = reqOutputComaKeysProjection_0(b, l + 1);
    if (!r) r = reqOutputComaKeysProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'? ( '[' ( reqOutputComaKeyProjection ','? )* ']' )
  private static boolean reqOutputComaKeysProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaKeysProjection_0_0(b, l + 1);
    r = r && reqOutputComaKeysProjection_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean reqOutputComaKeysProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_0_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  // '[' ( reqOutputComaKeyProjection ','? )* ']'
  private static boolean reqOutputComaKeysProjection_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_BRACKET_LEFT);
    r = r && reqOutputComaKeysProjection_0_1_1(b, l + 1);
    r = r && consumeToken(b, U_BRACKET_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( reqOutputComaKeyProjection ','? )*
  private static boolean reqOutputComaKeysProjection_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_0_1_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqOutputComaKeysProjection_0_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqOutputComaKeysProjection_0_1_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqOutputComaKeyProjection ','?
  private static boolean reqOutputComaKeysProjection_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_0_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaKeyProjection(b, l + 1);
    r = r && reqOutputComaKeysProjection_0_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqOutputComaKeysProjection_0_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_0_1_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  // '[' '*' ']'
  private static boolean reqOutputComaKeysProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_BRACKET_LEFT);
    r = r && consumeToken(b, U_STAR);
    r = r && consumeToken(b, U_BRACKET_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '*' ( '(' reqOutputComaVarProjection ')' )?
  public static boolean reqOutputComaListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaListModelProjection")) return false;
    if (!nextTokenIs(b, U_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, U_STAR);
    p = r; // pin = 1
    r = r && reqOutputComaListModelProjection_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ( '(' reqOutputComaVarProjection ')' )?
  private static boolean reqOutputComaListModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaListModelProjection_1")) return false;
    reqOutputComaListModelProjection_1_0(b, l + 1);
    return true;
  }

  // '(' reqOutputComaVarProjection ')'
  private static boolean reqOutputComaListModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaListModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_PAREN_LEFT);
    r = r && reqOutputComaVarProjection(b, l + 1);
    r = r && consumeToken(b, U_PAREN_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // reqOutputComaKeysProjection ( '(' reqOutputComaVarProjection ')' )?
  public static boolean reqOutputComaMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMapModelProjection")) return false;
    if (!nextTokenIs(b, "<req output coma map model projection>", U_PLUS, U_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_MAP_MODEL_PROJECTION, "<req output coma map model projection>");
    r = reqOutputComaKeysProjection(b, l + 1);
    r = r && reqOutputComaMapModelProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( '(' reqOutputComaVarProjection ')' )?
  private static boolean reqOutputComaMapModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMapModelProjection_1")) return false;
    reqOutputComaMapModelProjection_1_0(b, l + 1);
    return true;
  }

  // '(' reqOutputComaVarProjection ')'
  private static boolean reqOutputComaMapModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMapModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_PAREN_LEFT);
    r = r && reqOutputComaVarProjection(b, l + 1);
    r = r && consumeToken(b, U_PAREN_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( reqOutputComaRecordModelProjection
  //                                  | reqOutputComaListModelProjection
  //                                  | reqOutputComaMapModelProjection
  //                                  )?
  public static boolean reqOutputComaModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_MODEL_PROJECTION, "<req output coma model projection>");
    reqOutputComaModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // reqOutputComaRecordModelProjection
  //                                  | reqOutputComaListModelProjection
  //                                  | reqOutputComaMapModelProjection
  private static boolean reqOutputComaModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaRecordModelProjection(b, l + 1);
    if (!r) r = reqOutputComaListModelProjection(b, l + 1);
    if (!r) r = reqOutputComaMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ':' '(' (reqOutputComaMultiTagProjectionItem ','?)* ')'
  public static boolean reqOutputComaMultiTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMultiTagProjection")) return false;
    if (!nextTokenIs(b, U_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION, null);
    r = consumeToken(b, U_COLON);
    r = r && consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, reqOutputComaMultiTagProjection_2(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqOutputComaMultiTagProjectionItem ','?)*
  private static boolean reqOutputComaMultiTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMultiTagProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqOutputComaMultiTagProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqOutputComaMultiTagProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqOutputComaMultiTagProjectionItem ','?
  private static boolean reqOutputComaMultiTagProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMultiTagProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaMultiTagProjectionItem(b, l + 1);
    r = r && reqOutputComaMultiTagProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqOutputComaMultiTagProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMultiTagProjection_2_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? tagName reqOutputComaTagProjectionItem
  public static boolean reqOutputComaMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMultiTagProjectionItem")) return false;
    if (!nextTokenIs(b, "<req output coma multi tag projection item>", U_PLUS, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION_ITEM, "<req output coma multi tag projection item>");
    r = reqOutputComaMultiTagProjectionItem_0(b, l + 1);
    r = r && tagName(b, l + 1);
    r = r && reqOutputComaTagProjectionItem(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqOutputComaMultiTagProjectionItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMultiTagProjectionItem_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '(' (reqOutputComaFieldProjection ','?)* ')'
  public static boolean reqOutputComaRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaRecordModelProjection")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqOutputComaRecordModelProjection_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqOutputComaFieldProjection ','?)*
  private static boolean reqOutputComaRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaRecordModelProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqOutputComaRecordModelProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqOutputComaRecordModelProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqOutputComaFieldProjection ','?
  private static boolean reqOutputComaRecordModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaRecordModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaFieldProjection(b, l + 1);
    r = r && reqOutputComaRecordModelProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqOutputComaRecordModelProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaRecordModelProjection_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ( ':' '+'? tagName)? reqOutputComaTagProjectionItem
  public static boolean reqOutputComaSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_SINGLE_TAG_PROJECTION, "<req output coma single tag projection>");
    r = reqOutputComaSingleTagProjection_0(b, l + 1);
    r = r && reqOutputComaTagProjectionItem(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' '+'? tagName)?
  private static boolean reqOutputComaSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaSingleTagProjection_0")) return false;
    reqOutputComaSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' '+'? tagName
  private static boolean reqOutputComaSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_COLON);
    r = r && reqOutputComaSingleTagProjection_0_0_1(b, l + 1);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean reqOutputComaSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaSingleTagProjection_0_0_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // reqParamsAndAnnotations reqOutputComaModelProjection reqOutputModelMeta?
  static boolean reqOutputComaTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaTagProjectionItem")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqParamsAndAnnotations(b, l + 1);
    r = r && reqOutputComaModelProjection(b, l + 1);
    r = r && reqOutputComaTagProjectionItem_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqOutputModelMeta?
  private static boolean reqOutputComaTagProjectionItem_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaTagProjectionItem_2")) return false;
    reqOutputModelMeta(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( reqOutputComaMultiTagProjection | reqOutputComaSingleTagProjection ) reqOutputVarPolymorphicTail?
  public static boolean reqOutputComaVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_VAR_PROJECTION, "<req output coma var projection>");
    r = reqOutputComaVarProjection_0(b, l + 1);
    r = r && reqOutputComaVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // reqOutputComaMultiTagProjection | reqOutputComaSingleTagProjection
  private static boolean reqOutputComaVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaMultiTagProjection(b, l + 1);
    if (!r) r = reqOutputComaSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqOutputVarPolymorphicTail?
  private static boolean reqOutputComaVarProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaVarProjection_1")) return false;
    reqOutputVarPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '@' '+'? reqOutputComaModelProjection
  public static boolean reqOutputModelMeta(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelMeta")) return false;
    if (!nextTokenIs(b, U_AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_AT);
    r = r && reqOutputModelMeta_1(b, l + 1);
    r = r && reqOutputComaModelProjection(b, l + 1);
    exit_section_(b, m, U_REQ_OUTPUT_MODEL_META, r);
    return r;
  }

  // '+'?
  private static boolean reqOutputModelMeta_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelMeta_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // reqParamsAndAnnotations reqOutputTrunkVarProjection
  public static boolean reqOutputTrunkFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkFieldProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_TRUNK_FIELD_PROJECTION, "<req output trunk field projection>");
    r = reqParamsAndAnnotations(b, l + 1);
    r = r && reqOutputTrunkVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '+'? datum reqParamsAndAnnotations reqOutputTrunkVarProjection
  static boolean reqOutputTrunkKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkKeyProjection")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = reqOutputTrunkKeyProjection_0(b, l + 1);
    r = r && datum(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, reqParamsAndAnnotations(b, l + 1));
    r = p && reqOutputTrunkVarProjection(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean reqOutputTrunkKeyProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkKeyProjection_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '/' reqOutputTrunkKeyProjection
  public static boolean reqOutputTrunkMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkMapModelProjection")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && reqOutputTrunkKeyProjection(b, l + 1);
    exit_section_(b, m, U_REQ_OUTPUT_TRUNK_MAP_MODEL_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // ( reqOutputTrunkRecordModelProjection
  //                                   | reqOutputTrunkMapModelProjection
  //                                   | reqOutputComaRecordModelProjection
  //                                   | reqOutputComaMapModelProjection
  //                                   | reqOutputComaListModelProjection
  //                                   )?
  public static boolean reqOutputTrunkModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_TRUNK_MODEL_PROJECTION, "<req output trunk model projection>");
    reqOutputTrunkModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // reqOutputTrunkRecordModelProjection
  //                                   | reqOutputTrunkMapModelProjection
  //                                   | reqOutputComaRecordModelProjection
  //                                   | reqOutputComaMapModelProjection
  //                                   | reqOutputComaListModelProjection
  private static boolean reqOutputTrunkModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputTrunkRecordModelProjection(b, l + 1);
    if (!r) r = reqOutputTrunkMapModelProjection(b, l + 1);
    if (!r) r = reqOutputComaRecordModelProjection(b, l + 1);
    if (!r) r = reqOutputComaMapModelProjection(b, l + 1);
    if (!r) r = reqOutputComaListModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '/' '+'? qid reqOutputTrunkFieldProjection
  public static boolean reqOutputTrunkRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkRecordModelProjection")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_TRUNK_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, U_SLASH);
    r = r && reqOutputTrunkRecordModelProjection_1(b, l + 1);
    r = r && qid(b, l + 1);
    p = r; // pin = 3
    r = r && reqOutputTrunkFieldProjection(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean reqOutputTrunkRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkRecordModelProjection_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // ( ':' '+'? tagName )? reqParamsAndAnnotations reqOutputTrunkModelProjection reqOutputModelMeta?
  public static boolean reqOutputTrunkSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_TRUNK_SINGLE_TAG_PROJECTION, "<req output trunk single tag projection>");
    r = reqOutputTrunkSingleTagProjection_0(b, l + 1);
    r = r && reqParamsAndAnnotations(b, l + 1);
    r = r && reqOutputTrunkModelProjection(b, l + 1);
    r = r && reqOutputTrunkSingleTagProjection_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' '+'? tagName )?
  private static boolean reqOutputTrunkSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkSingleTagProjection_0")) return false;
    reqOutputTrunkSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' '+'? tagName
  private static boolean reqOutputTrunkSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_COLON);
    r = r && reqOutputTrunkSingleTagProjection_0_0_1(b, l + 1);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean reqOutputTrunkSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkSingleTagProjection_0_0_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  // reqOutputModelMeta?
  private static boolean reqOutputTrunkSingleTagProjection_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkSingleTagProjection_3")) return false;
    reqOutputModelMeta(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( reqOutputComaMultiTagProjection | reqOutputTrunkSingleTagProjection ) reqOutputVarPolymorphicTail?
  public static boolean reqOutputTrunkVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_TRUNK_VAR_PROJECTION, "<req output trunk var projection>");
    r = reqOutputTrunkVarProjection_0(b, l + 1);
    r = r && reqOutputTrunkVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // reqOutputComaMultiTagProjection | reqOutputTrunkSingleTagProjection
  private static boolean reqOutputTrunkVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaMultiTagProjection(b, l + 1);
    if (!r) r = reqOutputTrunkSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqOutputVarPolymorphicTail?
  private static boolean reqOutputTrunkVarProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkVarProjection_1")) return false;
    reqOutputVarPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '~' '(' (reqOutputVarMultiTailItem ','?)* ')'
  public static boolean reqOutputVarMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarMultiTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_VAR_MULTI_TAIL, null);
    r = consumeToken(b, U_TILDA);
    r = r && consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, reqOutputVarMultiTail_2(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqOutputVarMultiTailItem ','?)*
  private static boolean reqOutputVarMultiTail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarMultiTail_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqOutputVarMultiTail_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqOutputVarMultiTail_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqOutputVarMultiTailItem ','?
  private static boolean reqOutputVarMultiTail_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarMultiTail_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputVarMultiTailItem(b, l + 1);
    r = r && reqOutputVarMultiTail_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqOutputVarMultiTail_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarMultiTail_2_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeRef reqOutputComaVarProjection
  public static boolean reqOutputVarMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_VAR_MULTI_TAIL_ITEM, "<req output var multi tail item>");
    r = typeRef(b, l + 1);
    r = r && reqOutputComaVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // reqOutputVarSingleTail | reqOutputVarMultiTail
  public static boolean reqOutputVarPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarPolymorphicTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputVarSingleTail(b, l + 1);
    if (!r) r = reqOutputVarMultiTail(b, l + 1);
    exit_section_(b, m, U_REQ_OUTPUT_VAR_POLYMORPHIC_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // '~' typeRef reqOutputComaVarProjection
  public static boolean reqOutputVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarSingleTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_TILDA);
    r = r && typeRef(b, l + 1);
    r = r && reqOutputComaVarProjection(b, l + 1);
    exit_section_(b, m, U_REQ_OUTPUT_VAR_SINGLE_TAIL, r);
    return r;
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
  // ('?' | '&') PARAM_NAME '=' datum
  public static boolean requestParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requestParam")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQUEST_PARAM, "<request parameter>");
    r = requestParam_0(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, U_PARAM_NAME));
    r = p && report_error_(b, consumeToken(b, U_EQ)) && r;
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
  // '/' qid reqOutputTrunkFieldProjection requestParams
  public static boolean url(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "url")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && qid(b, l + 1);
    r = r && reqOutputTrunkFieldProjection(b, l + 1);
    r = r && requestParams(b, l + 1);
    exit_section_(b, m, U_URL, r);
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
