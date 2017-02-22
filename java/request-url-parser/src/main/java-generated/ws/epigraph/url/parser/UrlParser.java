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
    else if (t == U_CREATE_URL) {
      r = createUrl(b, 0);
    }
    else if (t == U_CUSTOM_URL) {
      r = customUrl(b, 0);
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
    else if (t == U_DELETE_URL) {
      r = deleteUrl(b, 0);
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
    else if (t == U_REQ_DELETE_FIELD_PROJECTION) {
      r = reqDeleteFieldProjection(b, 0);
    }
    else if (t == U_REQ_DELETE_FIELD_PROJECTION_ENTRY) {
      r = reqDeleteFieldProjectionEntry(b, 0);
    }
    else if (t == U_REQ_DELETE_KEY_PROJECTION) {
      r = reqDeleteKeyProjection(b, 0);
    }
    else if (t == U_REQ_DELETE_KEYS_PROJECTION) {
      r = reqDeleteKeysProjection(b, 0);
    }
    else if (t == U_REQ_DELETE_LIST_MODEL_PROJECTION) {
      r = reqDeleteListModelProjection(b, 0);
    }
    else if (t == U_REQ_DELETE_MAP_MODEL_PROJECTION) {
      r = reqDeleteMapModelProjection(b, 0);
    }
    else if (t == U_REQ_DELETE_MODEL_MULTI_TAIL) {
      r = reqDeleteModelMultiTail(b, 0);
    }
    else if (t == U_REQ_DELETE_MODEL_MULTI_TAIL_ITEM) {
      r = reqDeleteModelMultiTailItem(b, 0);
    }
    else if (t == U_REQ_DELETE_MODEL_POLYMORPHIC_TAIL) {
      r = reqDeleteModelPolymorphicTail(b, 0);
    }
    else if (t == U_REQ_DELETE_MODEL_PROJECTION) {
      r = reqDeleteModelProjection(b, 0);
    }
    else if (t == U_REQ_DELETE_MODEL_SINGLE_TAIL) {
      r = reqDeleteModelSingleTail(b, 0);
    }
    else if (t == U_REQ_DELETE_MULTI_TAG_PROJECTION) {
      r = reqDeleteMultiTagProjection(b, 0);
    }
    else if (t == U_REQ_DELETE_MULTI_TAG_PROJECTION_ITEM) {
      r = reqDeleteMultiTagProjectionItem(b, 0);
    }
    else if (t == U_REQ_DELETE_RECORD_MODEL_PROJECTION) {
      r = reqDeleteRecordModelProjection(b, 0);
    }
    else if (t == U_REQ_DELETE_SINGLE_TAG_PROJECTION) {
      r = reqDeleteSingleTagProjection(b, 0);
    }
    else if (t == U_REQ_DELETE_VAR_MULTI_TAIL) {
      r = reqDeleteVarMultiTail(b, 0);
    }
    else if (t == U_REQ_DELETE_VAR_MULTI_TAIL_ITEM) {
      r = reqDeleteVarMultiTailItem(b, 0);
    }
    else if (t == U_REQ_DELETE_VAR_POLYMORPHIC_TAIL) {
      r = reqDeleteVarPolymorphicTail(b, 0);
    }
    else if (t == U_REQ_DELETE_VAR_PROJECTION) {
      r = reqDeleteVarProjection(b, 0);
    }
    else if (t == U_REQ_DELETE_VAR_SINGLE_TAIL) {
      r = reqDeleteVarSingleTail(b, 0);
    }
    else if (t == U_REQ_FIELD_PATH) {
      r = reqFieldPath(b, 0);
    }
    else if (t == U_REQ_FIELD_PATH_ENTRY) {
      r = reqFieldPathEntry(b, 0);
    }
    else if (t == U_REQ_INPUT_FIELD_PROJECTION) {
      r = reqInputFieldProjection(b, 0);
    }
    else if (t == U_REQ_INPUT_FIELD_PROJECTION_ENTRY) {
      r = reqInputFieldProjectionEntry(b, 0);
    }
    else if (t == U_REQ_INPUT_KEY_PROJECTION) {
      r = reqInputKeyProjection(b, 0);
    }
    else if (t == U_REQ_INPUT_KEYS_PROJECTION) {
      r = reqInputKeysProjection(b, 0);
    }
    else if (t == U_REQ_INPUT_LIST_MODEL_PROJECTION) {
      r = reqInputListModelProjection(b, 0);
    }
    else if (t == U_REQ_INPUT_MAP_MODEL_PROJECTION) {
      r = reqInputMapModelProjection(b, 0);
    }
    else if (t == U_REQ_INPUT_MODEL_MULTI_TAIL) {
      r = reqInputModelMultiTail(b, 0);
    }
    else if (t == U_REQ_INPUT_MODEL_MULTI_TAIL_ITEM) {
      r = reqInputModelMultiTailItem(b, 0);
    }
    else if (t == U_REQ_INPUT_MODEL_POLYMORPHIC_TAIL) {
      r = reqInputModelPolymorphicTail(b, 0);
    }
    else if (t == U_REQ_INPUT_MODEL_PROJECTION) {
      r = reqInputModelProjection(b, 0);
    }
    else if (t == U_REQ_INPUT_MODEL_SINGLE_TAIL) {
      r = reqInputModelSingleTail(b, 0);
    }
    else if (t == U_REQ_INPUT_MULTI_TAG_PROJECTION) {
      r = reqInputMultiTagProjection(b, 0);
    }
    else if (t == U_REQ_INPUT_MULTI_TAG_PROJECTION_ITEM) {
      r = reqInputMultiTagProjectionItem(b, 0);
    }
    else if (t == U_REQ_INPUT_RECORD_MODEL_PROJECTION) {
      r = reqInputRecordModelProjection(b, 0);
    }
    else if (t == U_REQ_INPUT_SINGLE_TAG_PROJECTION) {
      r = reqInputSingleTagProjection(b, 0);
    }
    else if (t == U_REQ_INPUT_VAR_MULTI_TAIL) {
      r = reqInputVarMultiTail(b, 0);
    }
    else if (t == U_REQ_INPUT_VAR_MULTI_TAIL_ITEM) {
      r = reqInputVarMultiTailItem(b, 0);
    }
    else if (t == U_REQ_INPUT_VAR_POLYMORPHIC_TAIL) {
      r = reqInputVarPolymorphicTail(b, 0);
    }
    else if (t == U_REQ_INPUT_VAR_PROJECTION) {
      r = reqInputVarProjection(b, 0);
    }
    else if (t == U_REQ_INPUT_VAR_SINGLE_TAIL) {
      r = reqInputVarSingleTail(b, 0);
    }
    else if (t == U_REQ_MAP_MODEL_PATH) {
      r = reqMapModelPath(b, 0);
    }
    else if (t == U_REQ_MODEL_PATH) {
      r = reqModelPath(b, 0);
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
    else if (t == U_REQ_OUTPUT_MODEL_MULTI_TAIL) {
      r = reqOutputModelMultiTail(b, 0);
    }
    else if (t == U_REQ_OUTPUT_MODEL_MULTI_TAIL_ITEM) {
      r = reqOutputModelMultiTailItem(b, 0);
    }
    else if (t == U_REQ_OUTPUT_MODEL_POLYMORPHIC_TAIL) {
      r = reqOutputModelPolymorphicTail(b, 0);
    }
    else if (t == U_REQ_OUTPUT_MODEL_SINGLE_TAIL) {
      r = reqOutputModelSingleTail(b, 0);
    }
    else if (t == U_REQ_OUTPUT_STAR_TAG_PROJECTION) {
      r = reqOutputStarTagProjection(b, 0);
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
    else if (t == U_REQ_RECORD_MODEL_PATH) {
      r = reqRecordModelPath(b, 0);
    }
    else if (t == U_REQ_UPDATE_FIELD_PROJECTION) {
      r = reqUpdateFieldProjection(b, 0);
    }
    else if (t == U_REQ_UPDATE_FIELD_PROJECTION_ENTRY) {
      r = reqUpdateFieldProjectionEntry(b, 0);
    }
    else if (t == U_REQ_UPDATE_KEY_PROJECTION) {
      r = reqUpdateKeyProjection(b, 0);
    }
    else if (t == U_REQ_UPDATE_KEYS_PROJECTION) {
      r = reqUpdateKeysProjection(b, 0);
    }
    else if (t == U_REQ_UPDATE_LIST_MODEL_PROJECTION) {
      r = reqUpdateListModelProjection(b, 0);
    }
    else if (t == U_REQ_UPDATE_MAP_MODEL_PROJECTION) {
      r = reqUpdateMapModelProjection(b, 0);
    }
    else if (t == U_REQ_UPDATE_MODEL_MULTI_TAIL) {
      r = reqUpdateModelMultiTail(b, 0);
    }
    else if (t == U_REQ_UPDATE_MODEL_MULTI_TAIL_ITEM) {
      r = reqUpdateModelMultiTailItem(b, 0);
    }
    else if (t == U_REQ_UPDATE_MODEL_POLYMORPHIC_TAIL) {
      r = reqUpdateModelPolymorphicTail(b, 0);
    }
    else if (t == U_REQ_UPDATE_MODEL_PROJECTION) {
      r = reqUpdateModelProjection(b, 0);
    }
    else if (t == U_REQ_UPDATE_MODEL_SINGLE_TAIL) {
      r = reqUpdateModelSingleTail(b, 0);
    }
    else if (t == U_REQ_UPDATE_MULTI_TAG_PROJECTION) {
      r = reqUpdateMultiTagProjection(b, 0);
    }
    else if (t == U_REQ_UPDATE_MULTI_TAG_PROJECTION_ITEM) {
      r = reqUpdateMultiTagProjectionItem(b, 0);
    }
    else if (t == U_REQ_UPDATE_RECORD_MODEL_PROJECTION) {
      r = reqUpdateRecordModelProjection(b, 0);
    }
    else if (t == U_REQ_UPDATE_SINGLE_TAG_PROJECTION) {
      r = reqUpdateSingleTagProjection(b, 0);
    }
    else if (t == U_REQ_UPDATE_VAR_MULTI_TAIL) {
      r = reqUpdateVarMultiTail(b, 0);
    }
    else if (t == U_REQ_UPDATE_VAR_MULTI_TAIL_ITEM) {
      r = reqUpdateVarMultiTailItem(b, 0);
    }
    else if (t == U_REQ_UPDATE_VAR_POLYMORPHIC_TAIL) {
      r = reqUpdateVarPolymorphicTail(b, 0);
    }
    else if (t == U_REQ_UPDATE_VAR_PROJECTION) {
      r = reqUpdateVarProjection(b, 0);
    }
    else if (t == U_REQ_UPDATE_VAR_SINGLE_TAIL) {
      r = reqUpdateVarSingleTail(b, 0);
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
    else if (t == U_UPDATE_URL) {
      r = updateUrl(b, 0);
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
    create_token_set_(U_CREATE_URL, U_CUSTOM_URL, U_DELETE_URL, U_READ_URL,
      U_UPDATE_URL, U_URL),
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
  // '/' qid reqFieldPath ('<' reqInputFieldProjection)? ('>' reqOutputTrunkFieldProjection)? requestParams
  public static boolean createUrl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createUrl")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && qid(b, l + 1);
    r = r && reqFieldPath(b, l + 1);
    r = r && createUrl_3(b, l + 1);
    r = r && createUrl_4(b, l + 1);
    r = r && requestParams(b, l + 1);
    exit_section_(b, m, U_CREATE_URL, r);
    return r;
  }

  // ('<' reqInputFieldProjection)?
  private static boolean createUrl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createUrl_3")) return false;
    createUrl_3_0(b, l + 1);
    return true;
  }

  // '<' reqInputFieldProjection
  private static boolean createUrl_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createUrl_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_ANGLE_LEFT);
    r = r && reqInputFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('>' reqOutputTrunkFieldProjection)?
  private static boolean createUrl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createUrl_4")) return false;
    createUrl_4_0(b, l + 1);
    return true;
  }

  // '>' reqOutputTrunkFieldProjection
  private static boolean createUrl_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createUrl_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_ANGLE_RIGHT);
    r = r && reqOutputTrunkFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '/' qid reqFieldPath ('<' reqInputFieldProjection)? ('>' reqOutputTrunkFieldProjection)? requestParams
  public static boolean customUrl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customUrl")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && qid(b, l + 1);
    r = r && reqFieldPath(b, l + 1);
    r = r && customUrl_3(b, l + 1);
    r = r && customUrl_4(b, l + 1);
    r = r && requestParams(b, l + 1);
    exit_section_(b, m, U_CUSTOM_URL, r);
    return r;
  }

  // ('<' reqInputFieldProjection)?
  private static boolean customUrl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customUrl_3")) return false;
    customUrl_3_0(b, l + 1);
    return true;
  }

  // '<' reqInputFieldProjection
  private static boolean customUrl_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customUrl_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_ANGLE_LEFT);
    r = r && reqInputFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('>' reqOutputTrunkFieldProjection)?
  private static boolean customUrl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customUrl_4")) return false;
    customUrl_4_0(b, l + 1);
    return true;
  }

  // '>' reqOutputTrunkFieldProjection
  private static boolean customUrl_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customUrl_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_ANGLE_RIGHT);
    r = r && reqOutputTrunkFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
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
  // '/' qid reqFieldPath '<' reqDeleteFieldProjection ('>' reqOutputTrunkFieldProjection)? requestParams
  public static boolean deleteUrl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteUrl")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && qid(b, l + 1);
    r = r && reqFieldPath(b, l + 1);
    r = r && consumeToken(b, U_ANGLE_LEFT);
    r = r && reqDeleteFieldProjection(b, l + 1);
    r = r && deleteUrl_5(b, l + 1);
    r = r && requestParams(b, l + 1);
    exit_section_(b, m, U_DELETE_URL, r);
    return r;
  }

  // ('>' reqOutputTrunkFieldProjection)?
  private static boolean deleteUrl_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteUrl_5")) return false;
    deleteUrl_5_0(b, l + 1);
    return true;
  }

  // '>' reqOutputTrunkFieldProjection
  private static boolean deleteUrl_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteUrl_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_ANGLE_RIGHT);
    r = r && reqOutputTrunkFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
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
  // '/' qid reqOutputTrunkFieldProjection requestParams
  public static boolean readUrl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readUrl")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && qid(b, l + 1);
    r = r && reqOutputTrunkFieldProjection(b, l + 1);
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
  // '(' reqDeleteVarProjection ')'
  static boolean reqDeleteBracedVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteBracedVarProjection")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_PAREN_LEFT);
    r = r && reqDeleteVarProjection(b, l + 1);
    r = r && consumeToken(b, U_PAREN_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // reqDeleteVarProjection
  public static boolean reqDeleteFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteFieldProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_FIELD_PROJECTION, "<req delete field projection>");
    r = reqDeleteVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // qid reqDeleteFieldProjection
  public static boolean reqDeleteFieldProjectionEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteFieldProjectionEntry")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && reqDeleteFieldProjection(b, l + 1);
    exit_section_(b, m, U_REQ_DELETE_FIELD_PROJECTION_ENTRY, r);
    return r;
  }

  /* ********************************************************** */
  // datum reqParamsAndAnnotations
  public static boolean reqDeleteKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteKeyProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_KEY_PROJECTION, "<req delete key projection>");
    r = datum(b, l + 1);
    r = r && reqParamsAndAnnotations(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '['( '*' | ( ( reqDeleteKeyProjection ','? )* ) ) ']'
  public static boolean reqDeleteKeysProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteKeysProjection")) return false;
    if (!nextTokenIs(b, U_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_KEYS_PROJECTION, null);
    r = consumeToken(b, U_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqDeleteKeysProjection_1(b, l + 1));
    r = p && consumeToken(b, U_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '*' | ( ( reqDeleteKeyProjection ','? )* )
  private static boolean reqDeleteKeysProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteKeysProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_STAR);
    if (!r) r = reqDeleteKeysProjection_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( reqDeleteKeyProjection ','? )*
  private static boolean reqDeleteKeysProjection_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteKeysProjection_1_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqDeleteKeysProjection_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqDeleteKeysProjection_1_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqDeleteKeyProjection ','?
  private static boolean reqDeleteKeysProjection_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteKeysProjection_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteKeyProjection(b, l + 1);
    r = r && reqDeleteKeysProjection_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqDeleteKeysProjection_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteKeysProjection_1_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '*' ( ( reqDeleteBracedVarProjection | reqDeleteVarProjection ) )?
  public static boolean reqDeleteListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteListModelProjection")) return false;
    if (!nextTokenIs(b, U_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, U_STAR);
    p = r; // pin = 1
    r = r && reqDeleteListModelProjection_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ( ( reqDeleteBracedVarProjection | reqDeleteVarProjection ) )?
  private static boolean reqDeleteListModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteListModelProjection_1")) return false;
    reqDeleteListModelProjection_1_0(b, l + 1);
    return true;
  }

  // reqDeleteBracedVarProjection | reqDeleteVarProjection
  private static boolean reqDeleteListModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteListModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteBracedVarProjection(b, l + 1);
    if (!r) r = reqDeleteVarProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // reqDeleteKeysProjection ( ( reqDeleteBracedVarProjection | reqDeleteVarProjection ) )?
  public static boolean reqDeleteMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteMapModelProjection")) return false;
    if (!nextTokenIs(b, U_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteKeysProjection(b, l + 1);
    r = r && reqDeleteMapModelProjection_1(b, l + 1);
    exit_section_(b, m, U_REQ_DELETE_MAP_MODEL_PROJECTION, r);
    return r;
  }

  // ( ( reqDeleteBracedVarProjection | reqDeleteVarProjection ) )?
  private static boolean reqDeleteMapModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteMapModelProjection_1")) return false;
    reqDeleteMapModelProjection_1_0(b, l + 1);
    return true;
  }

  // reqDeleteBracedVarProjection | reqDeleteVarProjection
  private static boolean reqDeleteMapModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteMapModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteBracedVarProjection(b, l + 1);
    if (!r) r = reqDeleteVarProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (reqDeleteModelMultiTailItem ','?)* ')'
  public static boolean reqDeleteModelMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelMultiTail")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_MODEL_MULTI_TAIL, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqDeleteModelMultiTail_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqDeleteModelMultiTailItem ','?)*
  private static boolean reqDeleteModelMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqDeleteModelMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqDeleteModelMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqDeleteModelMultiTailItem ','?
  private static boolean reqDeleteModelMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteModelMultiTailItem(b, l + 1);
    r = r && reqDeleteModelMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqDeleteModelMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelMultiTail_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeRef reqDeleteModelProjectionWithProperties
  public static boolean reqDeleteModelMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_MODEL_MULTI_TAIL_ITEM, "<req delete model multi tail item>");
    r = typeRef(b, l + 1);
    r = r && reqDeleteModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '~' ( reqDeleteModelSingleTail | reqDeleteModelMultiTail )
  public static boolean reqDeleteModelPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelPolymorphicTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_TILDA);
    r = r && reqDeleteModelPolymorphicTail_1(b, l + 1);
    exit_section_(b, m, U_REQ_DELETE_MODEL_POLYMORPHIC_TAIL, r);
    return r;
  }

  // reqDeleteModelSingleTail | reqDeleteModelMultiTail
  private static boolean reqDeleteModelPolymorphicTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelPolymorphicTail_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteModelSingleTail(b, l + 1);
    if (!r) r = reqDeleteModelMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( ( reqDeleteRecordModelProjection
  //                                | reqDeleteListModelProjection
  //                                | reqDeleteMapModelProjection
  //                                ) reqDeleteModelPolymorphicTail?
  //                              )?
  public static boolean reqDeleteModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_MODEL_PROJECTION, "<req delete model projection>");
    reqDeleteModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // ( reqDeleteRecordModelProjection
  //                                | reqDeleteListModelProjection
  //                                | reqDeleteMapModelProjection
  //                                ) reqDeleteModelPolymorphicTail?
  private static boolean reqDeleteModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteModelProjection_0_0(b, l + 1);
    r = r && reqDeleteModelProjection_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqDeleteRecordModelProjection
  //                                | reqDeleteListModelProjection
  //                                | reqDeleteMapModelProjection
  private static boolean reqDeleteModelProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteRecordModelProjection(b, l + 1);
    if (!r) r = reqDeleteListModelProjection(b, l + 1);
    if (!r) r = reqDeleteMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqDeleteModelPolymorphicTail?
  private static boolean reqDeleteModelProjection_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelProjection_0_1")) return false;
    reqDeleteModelPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // reqParamsAndAnnotations reqDeleteModelProjection
  static boolean reqDeleteModelProjectionWithProperties(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelProjectionWithProperties")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqParamsAndAnnotations(b, l + 1);
    r = r && reqDeleteModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // typeRef reqDeleteModelProjectionWithProperties
  public static boolean reqDeleteModelSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteModelSingleTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_MODEL_SINGLE_TAIL, "<req delete model single tail>");
    r = typeRef(b, l + 1);
    r = r && reqDeleteModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ':' '(' (reqDeleteMultiTagProjectionItem ','?)* ')'
  public static boolean reqDeleteMultiTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteMultiTagProjection")) return false;
    if (!nextTokenIs(b, U_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_MULTI_TAG_PROJECTION, null);
    r = consumeTokens(b, 2, U_COLON, U_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, reqDeleteMultiTagProjection_2(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqDeleteMultiTagProjectionItem ','?)*
  private static boolean reqDeleteMultiTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteMultiTagProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqDeleteMultiTagProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqDeleteMultiTagProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqDeleteMultiTagProjectionItem ','?
  private static boolean reqDeleteMultiTagProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteMultiTagProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteMultiTagProjectionItem(b, l + 1);
    r = r && reqDeleteMultiTagProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqDeleteMultiTagProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteMultiTagProjection_2_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // tagName reqDeleteModelProjectionWithProperties
  public static boolean reqDeleteMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteMultiTagProjectionItem")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = tagName(b, l + 1);
    r = r && reqDeleteModelProjectionWithProperties(b, l + 1);
    exit_section_(b, m, U_REQ_DELETE_MULTI_TAG_PROJECTION_ITEM, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (reqDeleteFieldProjectionEntry ','?)* ')'
  public static boolean reqDeleteRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteRecordModelProjection")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqDeleteRecordModelProjection_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqDeleteFieldProjectionEntry ','?)*
  private static boolean reqDeleteRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteRecordModelProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqDeleteRecordModelProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqDeleteRecordModelProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqDeleteFieldProjectionEntry ','?
  private static boolean reqDeleteRecordModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteRecordModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteFieldProjectionEntry(b, l + 1);
    r = r && reqDeleteRecordModelProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqDeleteRecordModelProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteRecordModelProjection_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ( ':' tagName)? reqDeleteModelProjectionWithProperties
  public static boolean reqDeleteSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_SINGLE_TAG_PROJECTION, "<req delete single tag projection>");
    r = reqDeleteSingleTagProjection_0(b, l + 1);
    r = r && reqDeleteModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' tagName)?
  private static boolean reqDeleteSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteSingleTagProjection_0")) return false;
    reqDeleteSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' tagName
  private static boolean reqDeleteSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_COLON);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (reqDeleteVarMultiTailItem ','?)* ')'
  public static boolean reqDeleteVarMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteVarMultiTail")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_VAR_MULTI_TAIL, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqDeleteVarMultiTail_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqDeleteVarMultiTailItem ','?)*
  private static boolean reqDeleteVarMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteVarMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqDeleteVarMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqDeleteVarMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqDeleteVarMultiTailItem ','?
  private static boolean reqDeleteVarMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteVarMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteVarMultiTailItem(b, l + 1);
    r = r && reqDeleteVarMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqDeleteVarMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteVarMultiTail_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeRef reqDeleteVarProjection
  public static boolean reqDeleteVarMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteVarMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_VAR_MULTI_TAIL_ITEM, "<req delete var multi tail item>");
    r = typeRef(b, l + 1);
    r = r && reqDeleteVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '~' '~' ( reqDeleteVarSingleTail | reqDeleteVarMultiTail )
  public static boolean reqDeleteVarPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteVarPolymorphicTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_VAR_POLYMORPHIC_TAIL, null);
    r = consumeTokens(b, 2, U_TILDA, U_TILDA);
    p = r; // pin = 2
    r = r && reqDeleteVarPolymorphicTail_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // reqDeleteVarSingleTail | reqDeleteVarMultiTail
  private static boolean reqDeleteVarPolymorphicTail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteVarPolymorphicTail_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteVarSingleTail(b, l + 1);
    if (!r) r = reqDeleteVarMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( reqDeleteMultiTagProjection | reqDeleteSingleTagProjection ) reqDeleteVarPolymorphicTail?
  public static boolean reqDeleteVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_VAR_PROJECTION, "<req delete var projection>");
    r = reqDeleteVarProjection_0(b, l + 1);
    r = r && reqDeleteVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // reqDeleteMultiTagProjection | reqDeleteSingleTagProjection
  private static boolean reqDeleteVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqDeleteMultiTagProjection(b, l + 1);
    if (!r) r = reqDeleteSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqDeleteVarPolymorphicTail?
  private static boolean reqDeleteVarProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteVarProjection_1")) return false;
    reqDeleteVarPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // typeRef reqDeleteVarProjection
  public static boolean reqDeleteVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqDeleteVarSingleTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_DELETE_VAR_SINGLE_TAIL, "<req delete var single tail>");
    r = typeRef(b, l + 1);
    r = r && reqDeleteVarProjection(b, l + 1);
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
  // '(' reqInputVarProjection ')'
  static boolean reqInputBracedVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputBracedVarProjection")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_PAREN_LEFT);
    r = r && reqInputVarProjection(b, l + 1);
    r = r && consumeToken(b, U_PAREN_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // reqInputVarProjection
  public static boolean reqInputFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputFieldProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_FIELD_PROJECTION, "<req input field projection>");
    r = reqInputVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // qid reqInputFieldProjection
  public static boolean reqInputFieldProjectionEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputFieldProjectionEntry")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && reqInputFieldProjection(b, l + 1);
    exit_section_(b, m, U_REQ_INPUT_FIELD_PROJECTION_ENTRY, r);
    return r;
  }

  /* ********************************************************** */
  // datum reqParamsAndAnnotations
  public static boolean reqInputKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputKeyProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_KEY_PROJECTION, "<req input key projection>");
    r = datum(b, l + 1);
    r = r && reqParamsAndAnnotations(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '['( '*' | ( ( reqInputKeyProjection ','? )* ) ) ']'
  public static boolean reqInputKeysProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputKeysProjection")) return false;
    if (!nextTokenIs(b, U_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_KEYS_PROJECTION, null);
    r = consumeToken(b, U_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqInputKeysProjection_1(b, l + 1));
    r = p && consumeToken(b, U_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '*' | ( ( reqInputKeyProjection ','? )* )
  private static boolean reqInputKeysProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputKeysProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_STAR);
    if (!r) r = reqInputKeysProjection_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( reqInputKeyProjection ','? )*
  private static boolean reqInputKeysProjection_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputKeysProjection_1_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqInputKeysProjection_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqInputKeysProjection_1_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqInputKeyProjection ','?
  private static boolean reqInputKeysProjection_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputKeysProjection_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputKeyProjection(b, l + 1);
    r = r && reqInputKeysProjection_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqInputKeysProjection_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputKeysProjection_1_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '*' ( ( reqInputBracedVarProjection | reqInputVarProjection ) )?
  public static boolean reqInputListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputListModelProjection")) return false;
    if (!nextTokenIs(b, U_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, U_STAR);
    p = r; // pin = 1
    r = r && reqInputListModelProjection_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ( ( reqInputBracedVarProjection | reqInputVarProjection ) )?
  private static boolean reqInputListModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputListModelProjection_1")) return false;
    reqInputListModelProjection_1_0(b, l + 1);
    return true;
  }

  // reqInputBracedVarProjection | reqInputVarProjection
  private static boolean reqInputListModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputListModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputBracedVarProjection(b, l + 1);
    if (!r) r = reqInputVarProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // reqInputKeysProjection ( ( reqInputBracedVarProjection |  reqInputVarProjection ) )?
  public static boolean reqInputMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputMapModelProjection")) return false;
    if (!nextTokenIs(b, U_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputKeysProjection(b, l + 1);
    r = r && reqInputMapModelProjection_1(b, l + 1);
    exit_section_(b, m, U_REQ_INPUT_MAP_MODEL_PROJECTION, r);
    return r;
  }

  // ( ( reqInputBracedVarProjection |  reqInputVarProjection ) )?
  private static boolean reqInputMapModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputMapModelProjection_1")) return false;
    reqInputMapModelProjection_1_0(b, l + 1);
    return true;
  }

  // reqInputBracedVarProjection |  reqInputVarProjection
  private static boolean reqInputMapModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputMapModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputBracedVarProjection(b, l + 1);
    if (!r) r = reqInputVarProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (reqInputModelMultiTailItem ','?)* ')'
  public static boolean reqInputModelMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelMultiTail")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_MODEL_MULTI_TAIL, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqInputModelMultiTail_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqInputModelMultiTailItem ','?)*
  private static boolean reqInputModelMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqInputModelMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqInputModelMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqInputModelMultiTailItem ','?
  private static boolean reqInputModelMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputModelMultiTailItem(b, l + 1);
    r = r && reqInputModelMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqInputModelMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelMultiTail_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeRef reqInputModelProjectionWithProperties
  public static boolean reqInputModelMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_MODEL_MULTI_TAIL_ITEM, "<req input model multi tail item>");
    r = typeRef(b, l + 1);
    r = r && reqInputModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '~' ( reqInputModelSingleTail | reqInputModelMultiTail )
  public static boolean reqInputModelPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelPolymorphicTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_TILDA);
    r = r && reqInputModelPolymorphicTail_1(b, l + 1);
    exit_section_(b, m, U_REQ_INPUT_MODEL_POLYMORPHIC_TAIL, r);
    return r;
  }

  // reqInputModelSingleTail | reqInputModelMultiTail
  private static boolean reqInputModelPolymorphicTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelPolymorphicTail_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputModelSingleTail(b, l + 1);
    if (!r) r = reqInputModelMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( ( reqInputRecordModelProjection
  //                               | reqInputListModelProjection
  //                               | reqInputMapModelProjection
  //                               ) reqInputModelPolymorphicTail?
  //                             )?
  public static boolean reqInputModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_MODEL_PROJECTION, "<req input model projection>");
    reqInputModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // ( reqInputRecordModelProjection
  //                               | reqInputListModelProjection
  //                               | reqInputMapModelProjection
  //                               ) reqInputModelPolymorphicTail?
  private static boolean reqInputModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputModelProjection_0_0(b, l + 1);
    r = r && reqInputModelProjection_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqInputRecordModelProjection
  //                               | reqInputListModelProjection
  //                               | reqInputMapModelProjection
  private static boolean reqInputModelProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputRecordModelProjection(b, l + 1);
    if (!r) r = reqInputListModelProjection(b, l + 1);
    if (!r) r = reqInputMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqInputModelPolymorphicTail?
  private static boolean reqInputModelProjection_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelProjection_0_1")) return false;
    reqInputModelPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // reqParamsAndAnnotations reqInputModelProjection
  static boolean reqInputModelProjectionWithProperties(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelProjectionWithProperties")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqParamsAndAnnotations(b, l + 1);
    r = r && reqInputModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // typeRef reqInputModelProjectionWithProperties
  public static boolean reqInputModelSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputModelSingleTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_MODEL_SINGLE_TAIL, "<req input model single tail>");
    r = typeRef(b, l + 1);
    r = r && reqInputModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ':' '(' (reqInputMultiTagProjectionItem ','?)* ')'
  public static boolean reqInputMultiTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputMultiTagProjection")) return false;
    if (!nextTokenIs(b, U_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_MULTI_TAG_PROJECTION, null);
    r = consumeTokens(b, 2, U_COLON, U_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, reqInputMultiTagProjection_2(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqInputMultiTagProjectionItem ','?)*
  private static boolean reqInputMultiTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputMultiTagProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqInputMultiTagProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqInputMultiTagProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqInputMultiTagProjectionItem ','?
  private static boolean reqInputMultiTagProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputMultiTagProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputMultiTagProjectionItem(b, l + 1);
    r = r && reqInputMultiTagProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqInputMultiTagProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputMultiTagProjection_2_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // tagName reqInputModelProjectionWithProperties
  public static boolean reqInputMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputMultiTagProjectionItem")) return false;
    if (!nextTokenIs(b, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = tagName(b, l + 1);
    r = r && reqInputModelProjectionWithProperties(b, l + 1);
    exit_section_(b, m, U_REQ_INPUT_MULTI_TAG_PROJECTION_ITEM, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (reqInputFieldProjectionEntry ','?)* ')'
  public static boolean reqInputRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputRecordModelProjection")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqInputRecordModelProjection_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqInputFieldProjectionEntry ','?)*
  private static boolean reqInputRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputRecordModelProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqInputRecordModelProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqInputRecordModelProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqInputFieldProjectionEntry ','?
  private static boolean reqInputRecordModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputRecordModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputFieldProjectionEntry(b, l + 1);
    r = r && reqInputRecordModelProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqInputRecordModelProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputRecordModelProjection_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ( ':' tagName)? reqInputModelProjectionWithProperties
  public static boolean reqInputSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_SINGLE_TAG_PROJECTION, "<req input single tag projection>");
    r = reqInputSingleTagProjection_0(b, l + 1);
    r = r && reqInputModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' tagName)?
  private static boolean reqInputSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputSingleTagProjection_0")) return false;
    reqInputSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' tagName
  private static boolean reqInputSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_COLON);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (reqInputVarMultiTailItem ','?)* ')'
  public static boolean reqInputVarMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputVarMultiTail")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_VAR_MULTI_TAIL, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqInputVarMultiTail_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqInputVarMultiTailItem ','?)*
  private static boolean reqInputVarMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputVarMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqInputVarMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqInputVarMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqInputVarMultiTailItem ','?
  private static boolean reqInputVarMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputVarMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputVarMultiTailItem(b, l + 1);
    r = r && reqInputVarMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqInputVarMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputVarMultiTail_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeRef reqInputVarProjection
  public static boolean reqInputVarMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputVarMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_VAR_MULTI_TAIL_ITEM, "<req input var multi tail item>");
    r = typeRef(b, l + 1);
    r = r && reqInputVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '~' '~' ( reqInputVarSingleTail | reqInputVarMultiTail )
  public static boolean reqInputVarPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputVarPolymorphicTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_VAR_POLYMORPHIC_TAIL, null);
    r = consumeTokens(b, 2, U_TILDA, U_TILDA);
    p = r; // pin = 2
    r = r && reqInputVarPolymorphicTail_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // reqInputVarSingleTail | reqInputVarMultiTail
  private static boolean reqInputVarPolymorphicTail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputVarPolymorphicTail_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputVarSingleTail(b, l + 1);
    if (!r) r = reqInputVarMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( reqInputMultiTagProjection | reqInputSingleTagProjection ) reqInputVarPolymorphicTail?
  public static boolean reqInputVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_VAR_PROJECTION, "<req input var projection>");
    r = reqInputVarProjection_0(b, l + 1);
    r = r && reqInputVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // reqInputMultiTagProjection | reqInputSingleTagProjection
  private static boolean reqInputVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqInputMultiTagProjection(b, l + 1);
    if (!r) r = reqInputSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqInputVarPolymorphicTail?
  private static boolean reqInputVarProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputVarProjection_1")) return false;
    reqInputVarPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // typeRef reqInputVarProjection
  public static boolean reqInputVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqInputVarSingleTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_INPUT_VAR_SINGLE_TAIL, "<req input var single tail>");
    r = typeRef(b, l + 1);
    r = r && reqInputVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '/' reqOutputTrunkKeyProjection reqVarPath
  public static boolean reqMapModelPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqMapModelPath")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_MAP_MODEL_PATH, null);
    r = consumeToken(b, U_SLASH);
    r = r && reqOutputTrunkKeyProjection(b, l + 1);
    p = r; // pin = 2
    r = r && reqVarPath(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // '(' reqOutputComaVarProjection ')'
  static boolean reqOutputBracedComaVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputBracedComaVarProjection")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_PAREN_LEFT);
    r = r && reqOutputComaVarProjection(b, l + 1);
    r = r && consumeToken(b, U_PAREN_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '+'? qid /* reqParamsAndAnnotations */ reqOutputComaVarProjection
  public static boolean reqOutputComaFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaFieldProjection")) return false;
    if (!nextTokenIs(b, "<req output coma field projection>", U_PLUS, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_FIELD_PROJECTION, "<req output coma field projection>");
    r = reqOutputComaFieldProjection_0(b, l + 1);
    r = r && qid(b, l + 1);
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
  // '[' ( '*' | ( ( reqOutputComaKeyProjection ','? )* ) ) ']'
  public static boolean reqOutputComaKeysProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection")) return false;
    if (!nextTokenIs(b, U_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_KEYS_PROJECTION, null);
    r = consumeToken(b, U_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqOutputComaKeysProjection_1(b, l + 1));
    r = p && consumeToken(b, U_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '*' | ( ( reqOutputComaKeyProjection ','? )* )
  private static boolean reqOutputComaKeysProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_STAR);
    if (!r) r = reqOutputComaKeysProjection_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( reqOutputComaKeyProjection ','? )*
  private static boolean reqOutputComaKeysProjection_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_1_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqOutputComaKeysProjection_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqOutputComaKeysProjection_1_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqOutputComaKeyProjection ','?
  private static boolean reqOutputComaKeysProjection_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaKeyProjection(b, l + 1);
    r = r && reqOutputComaKeysProjection_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqOutputComaKeysProjection_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_1_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '*' ( ( reqOutputBracedComaVarProjection | reqOutputComaVarProjection ) )?
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

  // ( ( reqOutputBracedComaVarProjection | reqOutputComaVarProjection ) )?
  private static boolean reqOutputComaListModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaListModelProjection_1")) return false;
    reqOutputComaListModelProjection_1_0(b, l + 1);
    return true;
  }

  // reqOutputBracedComaVarProjection | reqOutputComaVarProjection
  private static boolean reqOutputComaListModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaListModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputBracedComaVarProjection(b, l + 1);
    if (!r) r = reqOutputComaVarProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // reqOutputComaKeysProjection ( ( reqOutputBracedComaVarProjection | reqOutputComaVarProjection ) )?
  public static boolean reqOutputComaMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMapModelProjection")) return false;
    if (!nextTokenIs(b, U_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaKeysProjection(b, l + 1);
    r = r && reqOutputComaMapModelProjection_1(b, l + 1);
    exit_section_(b, m, U_REQ_OUTPUT_COMA_MAP_MODEL_PROJECTION, r);
    return r;
  }

  // ( ( reqOutputBracedComaVarProjection | reqOutputComaVarProjection ) )?
  private static boolean reqOutputComaMapModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMapModelProjection_1")) return false;
    reqOutputComaMapModelProjection_1_0(b, l + 1);
    return true;
  }

  // reqOutputBracedComaVarProjection | reqOutputComaVarProjection
  private static boolean reqOutputComaMapModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMapModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputBracedComaVarProjection(b, l + 1);
    if (!r) r = reqOutputComaVarProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( ( reqOutputComaRecordModelProjection
  //                                    | reqOutputComaListModelProjection
  //                                    | reqOutputComaMapModelProjection
  //                                    ) reqOutputModelPolymorphicTail ?
  //                                  )?
  public static boolean reqOutputComaModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_MODEL_PROJECTION, "<req output coma model projection>");
    reqOutputComaModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // ( reqOutputComaRecordModelProjection
  //                                    | reqOutputComaListModelProjection
  //                                    | reqOutputComaMapModelProjection
  //                                    ) reqOutputModelPolymorphicTail ?
  private static boolean reqOutputComaModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaModelProjection_0_0(b, l + 1);
    r = r && reqOutputComaModelProjection_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqOutputComaRecordModelProjection
  //                                    | reqOutputComaListModelProjection
  //                                    | reqOutputComaMapModelProjection
  private static boolean reqOutputComaModelProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaModelProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaRecordModelProjection(b, l + 1);
    if (!r) r = reqOutputComaListModelProjection(b, l + 1);
    if (!r) r = reqOutputComaMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqOutputModelPolymorphicTail ?
  private static boolean reqOutputComaModelProjection_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaModelProjection_0_1")) return false;
    reqOutputModelPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // reqParamsAndAnnotations reqOutputComaModelProjection reqOutputModelMeta?
  static boolean reqOutputComaModelProjectionWithProperties(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaModelProjectionWithProperties")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqParamsAndAnnotations(b, l + 1);
    r = r && reqOutputComaModelProjection(b, l + 1);
    r = r && reqOutputComaModelProjectionWithProperties_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqOutputModelMeta?
  private static boolean reqOutputComaModelProjectionWithProperties_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaModelProjectionWithProperties_2")) return false;
    reqOutputModelMeta(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ':' '(' (reqOutputComaMultiTagProjectionItem ','?)* ')'
  public static boolean reqOutputComaMultiTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMultiTagProjection")) return false;
    if (!nextTokenIs(b, U_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION, null);
    r = consumeTokens(b, 2, U_COLON, U_PAREN_LEFT);
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
  // '+'? tagName reqOutputComaModelProjectionWithProperties
  public static boolean reqOutputComaMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMultiTagProjectionItem")) return false;
    if (!nextTokenIs(b, "<req output coma multi tag projection item>", U_PLUS, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION_ITEM, "<req output coma multi tag projection item>");
    r = reqOutputComaMultiTagProjectionItem_0(b, l + 1);
    r = r && tagName(b, l + 1);
    r = r && reqOutputComaModelProjectionWithProperties(b, l + 1);
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
  // '(' ( '*' | ( (reqOutputComaFieldProjection ','?)* ) ) ')'
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

  // '*' | ( (reqOutputComaFieldProjection ','?)* )
  private static boolean reqOutputComaRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaRecordModelProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_STAR);
    if (!r) r = reqOutputComaRecordModelProjection_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (reqOutputComaFieldProjection ','?)*
  private static boolean reqOutputComaRecordModelProjection_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaRecordModelProjection_1_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqOutputComaRecordModelProjection_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqOutputComaRecordModelProjection_1_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqOutputComaFieldProjection ','?
  private static boolean reqOutputComaRecordModelProjection_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaRecordModelProjection_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaFieldProjection(b, l + 1);
    r = r && reqOutputComaRecordModelProjection_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqOutputComaRecordModelProjection_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaRecordModelProjection_1_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ( ':' '+'? tagName)? reqOutputComaModelProjectionWithProperties
  public static boolean reqOutputComaSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_SINGLE_TAG_PROJECTION, "<req output coma single tag projection>");
    r = reqOutputComaSingleTagProjection_0(b, l + 1);
    r = r && reqOutputComaModelProjectionWithProperties(b, l + 1);
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
  // ( reqOutputStarTagProjection
  //                                | reqOutputComaMultiTagProjection
  //                                | reqOutputComaSingleTagProjection
  //                                ) reqOutputVarPolymorphicTail?
  public static boolean reqOutputComaVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_COMA_VAR_PROJECTION, "<req output coma var projection>");
    r = reqOutputComaVarProjection_0(b, l + 1);
    r = r && reqOutputComaVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // reqOutputStarTagProjection
  //                                | reqOutputComaMultiTagProjection
  //                                | reqOutputComaSingleTagProjection
  private static boolean reqOutputComaVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputStarTagProjection(b, l + 1);
    if (!r) r = reqOutputComaMultiTagProjection(b, l + 1);
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
  // '(' (reqOutputModelMultiTailItem ','?)* ')'
  public static boolean reqOutputModelMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelMultiTail")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_MODEL_MULTI_TAIL, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqOutputModelMultiTail_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqOutputModelMultiTailItem ','?)*
  private static boolean reqOutputModelMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqOutputModelMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqOutputModelMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqOutputModelMultiTailItem ','?
  private static boolean reqOutputModelMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputModelMultiTailItem(b, l + 1);
    r = r && reqOutputModelMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqOutputModelMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelMultiTail_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? typeRef reqOutputComaModelProjectionWithProperties
  public static boolean reqOutputModelMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_MODEL_MULTI_TAIL_ITEM, "<req output model multi tail item>");
    r = reqOutputModelMultiTailItem_0(b, l + 1);
    r = r && typeRef(b, l + 1);
    r = r && reqOutputComaModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqOutputModelMultiTailItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelMultiTailItem_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '~' ( reqOutputModelSingleTail | reqOutputModelMultiTail )
  public static boolean reqOutputModelPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelPolymorphicTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_TILDA);
    r = r && reqOutputModelPolymorphicTail_1(b, l + 1);
    exit_section_(b, m, U_REQ_OUTPUT_MODEL_POLYMORPHIC_TAIL, r);
    return r;
  }

  // reqOutputModelSingleTail | reqOutputModelMultiTail
  private static boolean reqOutputModelPolymorphicTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelPolymorphicTail_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputModelSingleTail(b, l + 1);
    if (!r) r = reqOutputModelMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '+'? typeRef reqOutputComaModelProjectionWithProperties
  public static boolean reqOutputModelSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelSingleTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_MODEL_SINGLE_TAIL, "<req output model single tail>");
    r = reqOutputModelSingleTail_0(b, l + 1);
    r = r && typeRef(b, l + 1);
    r = r && reqOutputComaModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqOutputModelSingleTail_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelSingleTail_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // ':' '*'
  public static boolean reqOutputStarTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputStarTagProjection")) return false;
    if (!nextTokenIs(b, U_COLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, U_COLON, U_STAR);
    exit_section_(b, m, U_REQ_OUTPUT_STAR_TAG_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // reqOutputTrunkVarProjection
  public static boolean reqOutputTrunkFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkFieldProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_TRUNK_FIELD_PROJECTION, "<req output trunk field projection>");
    r = reqOutputTrunkVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // datum reqParamsAndAnnotations
  static boolean reqOutputTrunkKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkKeyProjection")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = datum(b, l + 1);
    r = r && reqParamsAndAnnotations(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '/' reqOutputTrunkKeyProjection reqOutputTrunkVarProjection
  public static boolean reqOutputTrunkMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkMapModelProjection")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && reqOutputTrunkKeyProjection(b, l + 1);
    r = r && reqOutputTrunkVarProjection(b, l + 1);
    exit_section_(b, m, U_REQ_OUTPUT_TRUNK_MAP_MODEL_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // ( reqOutputTrunkRecordModelProjection // no tails on paths
  //                                   | reqOutputTrunkMapModelProjection
  //                                   | ( reqOutputComaRecordModelProjection reqOutputModelPolymorphicTail? )
  //                                   | ( reqOutputComaMapModelProjection reqOutputModelPolymorphicTail? )
  //                                   | ( reqOutputComaListModelProjection reqOutputModelPolymorphicTail? )
  //                                   )?
  public static boolean reqOutputTrunkModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_TRUNK_MODEL_PROJECTION, "<req output trunk model projection>");
    reqOutputTrunkModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // reqOutputTrunkRecordModelProjection // no tails on paths
  //                                   | reqOutputTrunkMapModelProjection
  //                                   | ( reqOutputComaRecordModelProjection reqOutputModelPolymorphicTail? )
  //                                   | ( reqOutputComaMapModelProjection reqOutputModelPolymorphicTail? )
  //                                   | ( reqOutputComaListModelProjection reqOutputModelPolymorphicTail? )
  private static boolean reqOutputTrunkModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputTrunkRecordModelProjection(b, l + 1);
    if (!r) r = reqOutputTrunkMapModelProjection(b, l + 1);
    if (!r) r = reqOutputTrunkModelProjection_0_2(b, l + 1);
    if (!r) r = reqOutputTrunkModelProjection_0_3(b, l + 1);
    if (!r) r = reqOutputTrunkModelProjection_0_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqOutputComaRecordModelProjection reqOutputModelPolymorphicTail?
  private static boolean reqOutputTrunkModelProjection_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkModelProjection_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaRecordModelProjection(b, l + 1);
    r = r && reqOutputTrunkModelProjection_0_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqOutputModelPolymorphicTail?
  private static boolean reqOutputTrunkModelProjection_0_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkModelProjection_0_2_1")) return false;
    reqOutputModelPolymorphicTail(b, l + 1);
    return true;
  }

  // reqOutputComaMapModelProjection reqOutputModelPolymorphicTail?
  private static boolean reqOutputTrunkModelProjection_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkModelProjection_0_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaMapModelProjection(b, l + 1);
    r = r && reqOutputTrunkModelProjection_0_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqOutputModelPolymorphicTail?
  private static boolean reqOutputTrunkModelProjection_0_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkModelProjection_0_3_1")) return false;
    reqOutputModelPolymorphicTail(b, l + 1);
    return true;
  }

  // reqOutputComaListModelProjection reqOutputModelPolymorphicTail?
  private static boolean reqOutputTrunkModelProjection_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkModelProjection_0_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputComaListModelProjection(b, l + 1);
    r = r && reqOutputTrunkModelProjection_0_4_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqOutputModelPolymorphicTail?
  private static boolean reqOutputTrunkModelProjection_0_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkModelProjection_0_4_1")) return false;
    reqOutputModelPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // reqParamsAndAnnotations reqOutputTrunkModelProjection
  static boolean reqOutputTrunkModelProjectionWithProperties(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkModelProjectionWithProperties")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqParamsAndAnnotations(b, l + 1);
    r = r && reqOutputTrunkModelProjection(b, l + 1);
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
  // ( ':' '+'? tagName )? reqOutputTrunkModelProjectionWithProperties reqOutputModelMeta?
  public static boolean reqOutputTrunkSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_TRUNK_SINGLE_TAG_PROJECTION, "<req output trunk single tag projection>");
    r = reqOutputTrunkSingleTagProjection_0(b, l + 1);
    r = r && reqOutputTrunkModelProjectionWithProperties(b, l + 1);
    r = r && reqOutputTrunkSingleTagProjection_2(b, l + 1);
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
  private static boolean reqOutputTrunkSingleTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkSingleTagProjection_2")) return false;
    reqOutputModelMeta(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( reqOutputStarTagProjection
  //                                 | reqOutputComaMultiTagProjection
  //                                 | reqOutputTrunkSingleTagProjection
  //                                 ) reqOutputVarPolymorphicTail?
  public static boolean reqOutputTrunkVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_TRUNK_VAR_PROJECTION, "<req output trunk var projection>");
    r = reqOutputTrunkVarProjection_0(b, l + 1);
    r = r && reqOutputTrunkVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // reqOutputStarTagProjection
  //                                 | reqOutputComaMultiTagProjection
  //                                 | reqOutputTrunkSingleTagProjection
  private static boolean reqOutputTrunkVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputStarTagProjection(b, l + 1);
    if (!r) r = reqOutputComaMultiTagProjection(b, l + 1);
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
  // '(' (reqOutputVarMultiTailItem ','?)* ')'
  public static boolean reqOutputVarMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarMultiTail")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_VAR_MULTI_TAIL, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqOutputVarMultiTail_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqOutputVarMultiTailItem ','?)*
  private static boolean reqOutputVarMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqOutputVarMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqOutputVarMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqOutputVarMultiTailItem ','?
  private static boolean reqOutputVarMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputVarMultiTailItem(b, l + 1);
    r = r && reqOutputVarMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqOutputVarMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarMultiTail_1_0_1")) return false;
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
  // '~' '~' ( reqOutputVarSingleTail | reqOutputVarMultiTail )
  public static boolean reqOutputVarPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarPolymorphicTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_VAR_POLYMORPHIC_TAIL, null);
    r = consumeTokens(b, 2, U_TILDA, U_TILDA);
    p = r; // pin = 2
    r = r && reqOutputVarPolymorphicTail_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // reqOutputVarSingleTail | reqOutputVarMultiTail
  private static boolean reqOutputVarPolymorphicTail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarPolymorphicTail_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputVarSingleTail(b, l + 1);
    if (!r) r = reqOutputVarMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // typeRef reqOutputComaVarProjection
  public static boolean reqOutputVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarSingleTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_OUTPUT_VAR_SINGLE_TAIL, "<req output var single tail>");
    r = typeRef(b, l + 1);
    r = r && reqOutputComaVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // '(' reqUpdateVarProjection ')'
  static boolean reqUpdateBracedVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateBracedVarProjection")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_PAREN_LEFT);
    r = r && reqUpdateVarProjection(b, l + 1);
    r = r && consumeToken(b, U_PAREN_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // reqUpdateVarProjection
  public static boolean reqUpdateFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateFieldProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_FIELD_PROJECTION, "<req update field projection>");
    r = reqUpdateVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '+'? qid reqUpdateFieldProjection
  public static boolean reqUpdateFieldProjectionEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateFieldProjectionEntry")) return false;
    if (!nextTokenIs(b, "<req update field projection entry>", U_PLUS, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_FIELD_PROJECTION_ENTRY, "<req update field projection entry>");
    r = reqUpdateFieldProjectionEntry_0(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && reqUpdateFieldProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqUpdateFieldProjectionEntry_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateFieldProjectionEntry_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // datum reqParamsAndAnnotations
  public static boolean reqUpdateKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateKeyProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_KEY_PROJECTION, "<req update key projection>");
    r = datum(b, l + 1);
    r = r && reqParamsAndAnnotations(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '+'? '[' ( reqUpdateKeyProjection ','? )* ']'
  public static boolean reqUpdateKeysProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateKeysProjection")) return false;
    if (!nextTokenIs(b, "<req update keys projection>", U_PLUS, U_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_KEYS_PROJECTION, "<req update keys projection>");
    r = reqUpdateKeysProjection_0(b, l + 1);
    r = r && consumeToken(b, U_BRACKET_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, reqUpdateKeysProjection_2(b, l + 1));
    r = p && consumeToken(b, U_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean reqUpdateKeysProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateKeysProjection_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  // ( reqUpdateKeyProjection ','? )*
  private static boolean reqUpdateKeysProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateKeysProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqUpdateKeysProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqUpdateKeysProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqUpdateKeyProjection ','?
  private static boolean reqUpdateKeysProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateKeysProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateKeyProjection(b, l + 1);
    r = r && reqUpdateKeysProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqUpdateKeysProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateKeysProjection_2_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '*' ( ( reqUpdateBracedVarProjection | reqUpdateVarProjection ) )?
  public static boolean reqUpdateListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateListModelProjection")) return false;
    if (!nextTokenIs(b, U_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, U_STAR);
    p = r; // pin = 1
    r = r && reqUpdateListModelProjection_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ( ( reqUpdateBracedVarProjection | reqUpdateVarProjection ) )?
  private static boolean reqUpdateListModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateListModelProjection_1")) return false;
    reqUpdateListModelProjection_1_0(b, l + 1);
    return true;
  }

  // reqUpdateBracedVarProjection | reqUpdateVarProjection
  private static boolean reqUpdateListModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateListModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateBracedVarProjection(b, l + 1);
    if (!r) r = reqUpdateVarProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // reqUpdateKeysProjection ( ( reqUpdateBracedVarProjection | reqUpdateVarProjection ) )?
  public static boolean reqUpdateMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateMapModelProjection")) return false;
    if (!nextTokenIs(b, "<req update map model projection>", U_PLUS, U_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_MAP_MODEL_PROJECTION, "<req update map model projection>");
    r = reqUpdateKeysProjection(b, l + 1);
    r = r && reqUpdateMapModelProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ( reqUpdateBracedVarProjection | reqUpdateVarProjection ) )?
  private static boolean reqUpdateMapModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateMapModelProjection_1")) return false;
    reqUpdateMapModelProjection_1_0(b, l + 1);
    return true;
  }

  // reqUpdateBracedVarProjection | reqUpdateVarProjection
  private static boolean reqUpdateMapModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateMapModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateBracedVarProjection(b, l + 1);
    if (!r) r = reqUpdateVarProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (reqUpdateModelMultiTailItem ','?)* ')'
  public static boolean reqUpdateModelMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelMultiTail")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_MODEL_MULTI_TAIL, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqUpdateModelMultiTail_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqUpdateModelMultiTailItem ','?)*
  private static boolean reqUpdateModelMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqUpdateModelMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqUpdateModelMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqUpdateModelMultiTailItem ','?
  private static boolean reqUpdateModelMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateModelMultiTailItem(b, l + 1);
    r = r && reqUpdateModelMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqUpdateModelMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelMultiTail_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? typeRef reqUpdateModelProjectionWithProperties
  public static boolean reqUpdateModelMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_MODEL_MULTI_TAIL_ITEM, "<req update model multi tail item>");
    r = reqUpdateModelMultiTailItem_0(b, l + 1);
    r = r && typeRef(b, l + 1);
    r = r && reqUpdateModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqUpdateModelMultiTailItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelMultiTailItem_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '~' ( reqUpdateModelSingleTail | reqUpdateModelMultiTail )
  public static boolean reqUpdateModelPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelPolymorphicTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_TILDA);
    r = r && reqUpdateModelPolymorphicTail_1(b, l + 1);
    exit_section_(b, m, U_REQ_UPDATE_MODEL_POLYMORPHIC_TAIL, r);
    return r;
  }

  // reqUpdateModelSingleTail | reqUpdateModelMultiTail
  private static boolean reqUpdateModelPolymorphicTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelPolymorphicTail_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateModelSingleTail(b, l + 1);
    if (!r) r = reqUpdateModelMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( ( reqUpdateRecordModelProjection
  //                                | reqUpdateMapModelProjection
  //                                | reqUpdateListModelProjection
  //                                ) reqUpdateModelPolymorphicTail?
  //                              )?
  public static boolean reqUpdateModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_MODEL_PROJECTION, "<req update model projection>");
    reqUpdateModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // ( reqUpdateRecordModelProjection
  //                                | reqUpdateMapModelProjection
  //                                | reqUpdateListModelProjection
  //                                ) reqUpdateModelPolymorphicTail?
  private static boolean reqUpdateModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateModelProjection_0_0(b, l + 1);
    r = r && reqUpdateModelProjection_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqUpdateRecordModelProjection
  //                                | reqUpdateMapModelProjection
  //                                | reqUpdateListModelProjection
  private static boolean reqUpdateModelProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateRecordModelProjection(b, l + 1);
    if (!r) r = reqUpdateMapModelProjection(b, l + 1);
    if (!r) r = reqUpdateListModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqUpdateModelPolymorphicTail?
  private static boolean reqUpdateModelProjection_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelProjection_0_1")) return false;
    reqUpdateModelPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // reqParamsAndAnnotations reqUpdateModelProjection
  static boolean reqUpdateModelProjectionWithProperties(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelProjectionWithProperties")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqParamsAndAnnotations(b, l + 1);
    r = r && reqUpdateModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '+'? typeRef reqUpdateModelProjectionWithProperties
  public static boolean reqUpdateModelSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelSingleTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_MODEL_SINGLE_TAIL, "<req update model single tail>");
    r = reqUpdateModelSingleTail_0(b, l + 1);
    r = r && typeRef(b, l + 1);
    r = r && reqUpdateModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqUpdateModelSingleTail_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateModelSingleTail_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // ':' '(' (reqUpdateMultiTagProjectionItem ','?)* ')'
  public static boolean reqUpdateMultiTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateMultiTagProjection")) return false;
    if (!nextTokenIs(b, U_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_MULTI_TAG_PROJECTION, null);
    r = consumeTokens(b, 2, U_COLON, U_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, reqUpdateMultiTagProjection_2(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqUpdateMultiTagProjectionItem ','?)*
  private static boolean reqUpdateMultiTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateMultiTagProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqUpdateMultiTagProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqUpdateMultiTagProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqUpdateMultiTagProjectionItem ','?
  private static boolean reqUpdateMultiTagProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateMultiTagProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateMultiTagProjectionItem(b, l + 1);
    r = r && reqUpdateMultiTagProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqUpdateMultiTagProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateMultiTagProjection_2_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? tagName reqUpdateModelProjectionWithProperties
  public static boolean reqUpdateMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateMultiTagProjectionItem")) return false;
    if (!nextTokenIs(b, "<req update multi tag projection item>", U_PLUS, U_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_MULTI_TAG_PROJECTION_ITEM, "<req update multi tag projection item>");
    r = reqUpdateMultiTagProjectionItem_0(b, l + 1);
    r = r && tagName(b, l + 1);
    r = r && reqUpdateModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqUpdateMultiTagProjectionItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateMultiTagProjectionItem_0")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '(' (reqUpdateFieldProjectionEntry ','?)* ')'
  public static boolean reqUpdateRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateRecordModelProjection")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqUpdateRecordModelProjection_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqUpdateFieldProjectionEntry ','?)*
  private static boolean reqUpdateRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateRecordModelProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqUpdateRecordModelProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqUpdateRecordModelProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqUpdateFieldProjectionEntry ','?
  private static boolean reqUpdateRecordModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateRecordModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateFieldProjectionEntry(b, l + 1);
    r = r && reqUpdateRecordModelProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqUpdateRecordModelProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateRecordModelProjection_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ( ':' '+'? tagName?)? reqUpdateModelProjectionWithProperties
  public static boolean reqUpdateSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_SINGLE_TAG_PROJECTION, "<req update single tag projection>");
    r = reqUpdateSingleTagProjection_0(b, l + 1);
    r = r && reqUpdateModelProjectionWithProperties(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' '+'? tagName?)?
  private static boolean reqUpdateSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateSingleTagProjection_0")) return false;
    reqUpdateSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' '+'? tagName?
  private static boolean reqUpdateSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_COLON);
    r = r && reqUpdateSingleTagProjection_0_0_1(b, l + 1);
    r = r && reqUpdateSingleTagProjection_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean reqUpdateSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateSingleTagProjection_0_0_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  // tagName?
  private static boolean reqUpdateSingleTagProjection_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateSingleTagProjection_0_0_2")) return false;
    tagName(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '(' (reqUpdateVarMultiTailItem ','?)* ')'
  public static boolean reqUpdateVarMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateVarMultiTail")) return false;
    if (!nextTokenIs(b, U_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_VAR_MULTI_TAIL, null);
    r = consumeToken(b, U_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqUpdateVarMultiTail_1(b, l + 1));
    r = p && consumeToken(b, U_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (reqUpdateVarMultiTailItem ','?)*
  private static boolean reqUpdateVarMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateVarMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reqUpdateVarMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reqUpdateVarMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // reqUpdateVarMultiTailItem ','?
  private static boolean reqUpdateVarMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateVarMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateVarMultiTailItem(b, l + 1);
    r = r && reqUpdateVarMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean reqUpdateVarMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateVarMultiTail_1_0_1")) return false;
    consumeToken(b, U_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeRef reqUpdateVarProjection
  public static boolean reqUpdateVarMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateVarMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_VAR_MULTI_TAIL_ITEM, "<req update var multi tail item>");
    r = typeRef(b, l + 1);
    r = r && reqUpdateVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '~' '~' ( reqUpdateVarSingleTail | reqUpdateVarMultiTail )
  public static boolean reqUpdateVarPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateVarPolymorphicTail")) return false;
    if (!nextTokenIs(b, U_TILDA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_VAR_POLYMORPHIC_TAIL, null);
    r = consumeTokens(b, 2, U_TILDA, U_TILDA);
    p = r; // pin = 2
    r = r && reqUpdateVarPolymorphicTail_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // reqUpdateVarSingleTail | reqUpdateVarMultiTail
  private static boolean reqUpdateVarPolymorphicTail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateVarPolymorphicTail_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateVarSingleTail(b, l + 1);
    if (!r) r = reqUpdateVarMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( reqUpdateMultiTagProjection | reqUpdateSingleTagProjection ) reqUpdateVarPolymorphicTail?
  public static boolean reqUpdateVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_VAR_PROJECTION, "<req update var projection>");
    r = reqUpdateVarProjection_0(b, l + 1);
    r = r && reqUpdateVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // reqUpdateMultiTagProjection | reqUpdateSingleTagProjection
  private static boolean reqUpdateVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqUpdateMultiTagProjection(b, l + 1);
    if (!r) r = reqUpdateSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // reqUpdateVarPolymorphicTail?
  private static boolean reqUpdateVarProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateVarProjection_1")) return false;
    reqUpdateVarPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // typeRef reqUpdateVarProjection
  public static boolean reqUpdateVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqUpdateVarSingleTail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, U_REQ_UPDATE_VAR_SINGLE_TAIL, "<req update var single tail>");
    r = typeRef(b, l + 1);
    r = r && reqUpdateVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
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
  // '/' qid reqFieldPath ('<' '+'? reqUpdateFieldProjection)? ('>' reqOutputTrunkFieldProjection)? requestParams
  public static boolean updateUrl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateUrl")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_SLASH);
    r = r && qid(b, l + 1);
    r = r && reqFieldPath(b, l + 1);
    r = r && updateUrl_3(b, l + 1);
    r = r && updateUrl_4(b, l + 1);
    r = r && requestParams(b, l + 1);
    exit_section_(b, m, U_UPDATE_URL, r);
    return r;
  }

  // ('<' '+'? reqUpdateFieldProjection)?
  private static boolean updateUrl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateUrl_3")) return false;
    updateUrl_3_0(b, l + 1);
    return true;
  }

  // '<' '+'? reqUpdateFieldProjection
  private static boolean updateUrl_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateUrl_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_ANGLE_LEFT);
    r = r && updateUrl_3_0_1(b, l + 1);
    r = r && reqUpdateFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean updateUrl_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateUrl_3_0_1")) return false;
    consumeToken(b, U_PLUS);
    return true;
  }

  // ('>' reqOutputTrunkFieldProjection)?
  private static boolean updateUrl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateUrl_4")) return false;
    updateUrl_4_0(b, l + 1);
    return true;
  }

  // '>' reqOutputTrunkFieldProjection
  private static boolean updateUrl_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateUrl_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, U_ANGLE_RIGHT);
    r = r && reqOutputTrunkFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // readUrl | createUrl | updateUrl | deleteUrl | customUrl
  public static boolean url(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "url")) return false;
    if (!nextTokenIs(b, U_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, U_URL, null);
    r = readUrl(b, l + 1);
    if (!r) r = createUrl(b, l + 1);
    if (!r) r = updateUrl(b, l + 1);
    if (!r) r = deleteUrl(b, l + 1);
    if (!r) r = customUrl(b, l + 1);
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
