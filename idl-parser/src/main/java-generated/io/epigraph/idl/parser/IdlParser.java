// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static io.epigraph.idl.lexer.IdlElementTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class IdlParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == I_ANNOTATION) {
      r = annotation(b, 0);
    }
    else if (t == I_DATA) {
      r = data(b, 0);
    }
    else if (t == I_DATA_ENTRY) {
      r = dataEntry(b, 0);
    }
    else if (t == I_DATA_VALUE) {
      r = dataValue(b, 0);
    }
    else if (t == I_DATUM) {
      r = datum(b, 0);
    }
    else if (t == I_ENUM_DATUM) {
      r = enumDatum(b, 0);
    }
    else if (t == I_FQN) {
      r = fqn(b, 0);
    }
    else if (t == I_FQN_SEGMENT) {
      r = fqnSegment(b, 0);
    }
    else if (t == I_FQN_TYPE_REF) {
      r = fqnTypeRef(b, 0);
    }
    else if (t == I_IMPORT_STATEMENT) {
      r = importStatement(b, 0);
    }
    else if (t == I_IMPORTS) {
      r = imports(b, 0);
    }
    else if (t == I_LIST_DATUM) {
      r = listDatum(b, 0);
    }
    else if (t == I_MAP_DATUM) {
      r = mapDatum(b, 0);
    }
    else if (t == I_MAP_DATUM_ENTRY) {
      r = mapDatumEntry(b, 0);
    }
    else if (t == I_NAMESPACE_DECL) {
      r = namespaceDecl(b, 0);
    }
    else if (t == I_NULL_DATUM) {
      r = nullDatum(b, 0);
    }
    else if (t == I_OP_INPUT_COMA_FIELD_PROJECTION) {
      r = opInputComaFieldProjection(b, 0);
    }
    else if (t == I_OP_INPUT_COMA_KEY_PROJECTION) {
      r = opInputComaKeyProjection(b, 0);
    }
    else if (t == I_OP_INPUT_COMA_LIST_MODEL_PROJECTION) {
      r = opInputComaListModelProjection(b, 0);
    }
    else if (t == I_OP_INPUT_COMA_MAP_MODEL_PROJECTION) {
      r = opInputComaMapModelProjection(b, 0);
    }
    else if (t == I_OP_INPUT_COMA_MODEL_PROJECTION) {
      r = opInputComaModelProjection(b, 0);
    }
    else if (t == I_OP_INPUT_COMA_MULTI_TAG_PROJECTION) {
      r = opInputComaMultiTagProjection(b, 0);
    }
    else if (t == I_OP_INPUT_COMA_MULTI_TAG_PROJECTION_ITEM) {
      r = opInputComaMultiTagProjectionItem(b, 0);
    }
    else if (t == I_OP_INPUT_COMA_RECORD_MODEL_PROJECTION) {
      r = opInputComaRecordModelProjection(b, 0);
    }
    else if (t == I_OP_INPUT_COMA_SINGLE_TAG_PROJECTION) {
      r = opInputComaSingleTagProjection(b, 0);
    }
    else if (t == I_OP_INPUT_COMA_VAR_PROJECTION) {
      r = opInputComaVarProjection(b, 0);
    }
    else if (t == I_OP_INPUT_DEFAULT_VALUE) {
      r = opInputDefaultValue(b, 0);
    }
    else if (t == I_OP_INPUT_FIELD_PROJECTION_BODY_PART) {
      r = opInputFieldProjectionBodyPart(b, 0);
    }
    else if (t == I_OP_INPUT_MODEL_META) {
      r = opInputModelMeta(b, 0);
    }
    else if (t == I_OP_INPUT_MODEL_PROPERTY) {
      r = opInputModelProperty(b, 0);
    }
    else if (t == I_OP_INPUT_TRUNK_FIELD_PROJECTION) {
      r = opInputTrunkFieldProjection(b, 0);
    }
    else if (t == I_OP_INPUT_TRUNK_MODEL_PROJECTION) {
      r = opInputTrunkModelProjection(b, 0);
    }
    else if (t == I_OP_INPUT_TRUNK_RECORD_MODEL_PROJECTION) {
      r = opInputTrunkRecordModelProjection(b, 0);
    }
    else if (t == I_OP_INPUT_TRUNK_SINGLE_TAG_PROJECTION) {
      r = opInputTrunkSingleTagProjection(b, 0);
    }
    else if (t == I_OP_INPUT_TRUNK_VAR_PROJECTION) {
      r = opInputTrunkVarProjection(b, 0);
    }
    else if (t == I_OP_INPUT_VAR_MULTI_TAIL) {
      r = opInputVarMultiTail(b, 0);
    }
    else if (t == I_OP_INPUT_VAR_MULTI_TAIL_ITEM) {
      r = opInputVarMultiTailItem(b, 0);
    }
    else if (t == I_OP_INPUT_VAR_POLYMORPHIC_TAIL) {
      r = opInputVarPolymorphicTail(b, 0);
    }
    else if (t == I_OP_INPUT_VAR_SINGLE_TAIL) {
      r = opInputVarSingleTail(b, 0);
    }
    else if (t == I_OP_OUTPUT_FIELD_PROJECTION) {
      r = opOutputFieldProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_FIELD_PROJECTION_BODY_PART) {
      r = opOutputFieldProjectionBodyPart(b, 0);
    }
    else if (t == I_OP_OUTPUT_KEY_PROJECTION) {
      r = opOutputKeyProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_KEY_PROJECTION_PART) {
      r = opOutputKeyProjectionPart(b, 0);
    }
    else if (t == I_OP_OUTPUT_LIST_MODEL_PROJECTION) {
      r = opOutputListModelProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_MAP_MODEL_PROJECTION) {
      r = opOutputMapModelProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_MODEL_META) {
      r = opOutputModelMeta(b, 0);
    }
    else if (t == I_OP_OUTPUT_MODEL_PROJECTION) {
      r = opOutputModelProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_MODEL_PROPERTY) {
      r = opOutputModelProperty(b, 0);
    }
    else if (t == I_OP_OUTPUT_MULTI_TAG_PROJECTION) {
      r = opOutputMultiTagProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM) {
      r = opOutputMultiTagProjectionItem(b, 0);
    }
    else if (t == I_OP_OUTPUT_RECORD_MODEL_PROJECTION) {
      r = opOutputRecordModelProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_SINGLE_TAG_PROJECTION) {
      r = opOutputSingleTagProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_VAR_MULTI_TAIL) {
      r = opOutputVarMultiTail(b, 0);
    }
    else if (t == I_OP_OUTPUT_VAR_MULTI_TAIL_ITEM) {
      r = opOutputVarMultiTailItem(b, 0);
    }
    else if (t == I_OP_OUTPUT_VAR_POLYMORPHIC_TAIL) {
      r = opOutputVarPolymorphicTail(b, 0);
    }
    else if (t == I_OP_OUTPUT_VAR_PROJECTION) {
      r = opOutputVarProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_VAR_SINGLE_TAIL) {
      r = opOutputVarSingleTail(b, 0);
    }
    else if (t == I_OP_PARAM) {
      r = opParam(b, 0);
    }
    else if (t == I_PRIMITIVE_DATUM) {
      r = primitiveDatum(b, 0);
    }
    else if (t == I_QID) {
      r = qid(b, 0);
    }
    else if (t == I_RECORD_DATUM) {
      r = recordDatum(b, 0);
    }
    else if (t == I_RECORD_DATUM_ENTRY) {
      r = recordDatumEntry(b, 0);
    }
    else if (t == I_REQ_ANNOTATION) {
      r = reqAnnotation(b, 0);
    }
    else if (t == I_REQ_OUTPUT_COMA_FIELD_PROJECTION) {
      r = reqOutputComaFieldProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_COMA_KEY_PROJECTION) {
      r = reqOutputComaKeyProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_COMA_KEYS_PROJECTION) {
      r = reqOutputComaKeysProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_COMA_LIST_MODEL_PROJECTION) {
      r = reqOutputComaListModelProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_COMA_MAP_MODEL_PROJECTION) {
      r = reqOutputComaMapModelProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_COMA_MODEL_PROJECTION) {
      r = reqOutputComaModelProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION) {
      r = reqOutputComaMultiTagProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION_ITEM) {
      r = reqOutputComaMultiTagProjectionItem(b, 0);
    }
    else if (t == I_REQ_OUTPUT_COMA_RECORD_MODEL_PROJECTION) {
      r = reqOutputComaRecordModelProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_COMA_SINGLE_TAG_PROJECTION) {
      r = reqOutputComaSingleTagProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_COMA_VAR_PROJECTION) {
      r = reqOutputComaVarProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_MODEL_META) {
      r = reqOutputModelMeta(b, 0);
    }
    else if (t == I_REQ_OUTPUT_TRUNK_MAP_MODEL_PROJECTION) {
      r = reqOutputTrunkMapModelProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_TRUNK_MODEL_PROJECTION) {
      r = reqOutputTrunkModelProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_TRUNK_RECORD_MODEL_PROJECTION) {
      r = reqOutputTrunkRecordModelProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_TRUNK_SINGLE_TAG_PROJECTION) {
      r = reqOutputTrunkSingleTagProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_TRUNK_VAR_PROJECTION) {
      r = reqOutputTrunkVarProjection(b, 0);
    }
    else if (t == I_REQ_OUTPUT_VAR_MULTI_TAIL) {
      r = reqOutputVarMultiTail(b, 0);
    }
    else if (t == I_REQ_OUTPUT_VAR_MULTI_TAIL_ITEM) {
      r = reqOutputVarMultiTailItem(b, 0);
    }
    else if (t == I_REQ_OUTPUT_VAR_POLYMORPHIC_TAIL) {
      r = reqOutputVarPolymorphicTail(b, 0);
    }
    else if (t == I_REQ_OUTPUT_VAR_SINGLE_TAIL) {
      r = reqOutputVarSingleTail(b, 0);
    }
    else if (t == I_REQ_PARAM) {
      r = reqParam(b, 0);
    }
    else if (t == I_TAG_NAME) {
      r = tagName(b, 0);
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
    create_token_set_(I_OP_INPUT_COMA_MODEL_PROJECTION, I_OP_INPUT_TRUNK_MODEL_PROJECTION),
    create_token_set_(I_REQ_OUTPUT_COMA_MODEL_PROJECTION, I_REQ_OUTPUT_TRUNK_MODEL_PROJECTION),
    create_token_set_(I_DATUM, I_ENUM_DATUM, I_LIST_DATUM, I_MAP_DATUM,
      I_NULL_DATUM, I_PRIMITIVE_DATUM, I_RECORD_DATUM),
  };

  /* ********************************************************** */
  // qid '=' dataValue
  public static boolean annotation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotation")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_ANNOTATION, "<custom annotation>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, I_EQ);
    p = r; // pin = 2
    r = r && dataValue(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // dataTypeSpec? '<' dataEntry* '>'
  public static boolean data(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "data")) return false;
    if (!nextTokenIs(b, "<data>", I_ANGLE_LEFT, I_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_DATA, "<data>");
    r = data_0(b, l + 1);
    r = r && consumeToken(b, I_ANGLE_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, data_2(b, l + 1));
    r = p && consumeToken(b, I_ANGLE_RIGHT) && r;
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
    Marker m = enter_section_(b, l, _NONE_, I_DATA_ENTRY, "<data entry>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, I_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, datum(b, l + 1));
    r = p && dataEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean dataEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataEntry_3")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // fqnTypeRef
  static boolean dataTypeSpec(PsiBuilder b, int l) {
    return fqnTypeRef(b, l + 1);
  }

  /* ********************************************************** */
  // data | datum
  public static boolean dataValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_DATA_VALUE, "<data value>");
    r = data(b, l + 1);
    if (!r) r = datum(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ! ( '#' | qid | primitiveDatum | '}' | ')' | '>' | ']' | ',' )
  static boolean dataValueRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !dataValueRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '#' | qid | primitiveDatum | '}' | ')' | '>' | ']' | ','
  private static boolean dataValueRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_HASH);
    if (!r) r = qid(b, l + 1);
    if (!r) r = primitiveDatum(b, l + 1);
    if (!r) r = consumeToken(b, I_CURLY_RIGHT);
    if (!r) r = consumeToken(b, I_PAREN_RIGHT);
    if (!r) r = consumeToken(b, I_ANGLE_RIGHT);
    if (!r) r = consumeToken(b, I_BRACKET_RIGHT);
    if (!r) r = consumeToken(b, I_COMMA);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // recordDatum | mapDatum | listDatum | primitiveDatum | enumDatum | nullDatum
  public static boolean datum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "datum")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, I_DATUM, "<datum>");
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
  // '#' qid
  public static boolean enumDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumDatum")) return false;
    if (!nextTokenIs(b, I_HASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_HASH);
    r = r && qid(b, l + 1);
    exit_section_(b, m, I_ENUM_DATUM, r);
    return r;
  }

  /* ********************************************************** */
  // fqnSegment ('.' fqnSegment)*
  public static boolean fqn(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fqn")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fqnSegment(b, l + 1);
    r = r && fqn_1(b, l + 1);
    exit_section_(b, m, I_FQN, r);
    return r;
  }

  // ('.' fqnSegment)*
  private static boolean fqn_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fqn_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!fqn_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "fqn_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // '.' fqnSegment
  private static boolean fqn_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fqn_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_DOT);
    r = r && fqnSegment(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qid {
  // //  implements="com.intellij.psi.PsiNameIdentifierOwner"
  // //  methods=[getName setName getNameIdentifier getSchemaFqn getSchemaFqnTypeRef isLast getReference getFqn]
  // }
  public static boolean fqnSegment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fqnSegment")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && fqnSegment_1(b, l + 1);
    exit_section_(b, m, I_FQN_SEGMENT, r);
    return r;
  }

  // {
  // //  implements="com.intellij.psi.PsiNameIdentifierOwner"
  // //  methods=[getName setName getNameIdentifier getSchemaFqn getSchemaFqnTypeRef isLast getReference getFqn]
  // }
  private static boolean fqnSegment_1(PsiBuilder b, int l) {
    return true;
  }

  /* ********************************************************** */
  // fqn
  public static boolean fqnTypeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fqnTypeRef")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_FQN_TYPE_REF, "<type>");
    r = fqn(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // namespaceDeclRecover
  static boolean importRecover(PsiBuilder b, int l) {
    return namespaceDeclRecover(b, l + 1);
  }

  /* ********************************************************** */
  // 'import' fqn
  public static boolean importStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_IMPORT_STATEMENT, "<import statement>");
    r = consumeToken(b, I_IMPORT);
    p = r; // pin = 1
    r = r && fqn(b, l + 1);
    exit_section_(b, l, m, r, p, importRecover_parser_);
    return r || p;
  }

  /* ********************************************************** */
  // importStatement*
  public static boolean imports(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "imports")) return false;
    Marker m = enter_section_(b, l, _NONE_, I_IMPORTS, "<imports>");
    int c = current_position_(b);
    while (true) {
      if (!importStatement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "imports", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // dataTypeSpec? '[' (dataValue ','?)* ']'
  public static boolean listDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listDatum")) return false;
    if (!nextTokenIs(b, "<list datum>", I_BRACKET_LEFT, I_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_LIST_DATUM, "<list datum>");
    r = listDatum_0(b, l + 1);
    r = r && consumeToken(b, I_BRACKET_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, listDatum_2(b, l + 1));
    r = p && consumeToken(b, I_BRACKET_RIGHT) && r;
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
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // dataTypeSpec? '(' mapDatumEntry* ')'
  public static boolean mapDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapDatum")) return false;
    if (!nextTokenIs(b, "<map datum>", I_PAREN_LEFT, I_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_MAP_DATUM, "<map datum>");
    r = mapDatum_0(b, l + 1);
    r = r && consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, mapDatum_2(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
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
    Marker m = enter_section_(b, l, _NONE_, I_MAP_DATUM_ENTRY, "<map datum entry>");
    r = datum(b, l + 1);
    r = r && consumeToken(b, I_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataValue(b, l + 1));
    r = p && mapDatumEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean mapDatumEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapDatumEntry_3")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '{' namespaceBodyPart* '}'
  static boolean namespaceBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceBody")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, namespaceBody_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // namespaceBodyPart*
  private static boolean namespaceBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!namespaceBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "namespaceBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // annotation
  static boolean namespaceBodyPart(PsiBuilder b, int l) {
    return annotation(b, l + 1);
  }

  /* ********************************************************** */
  // 'namespace' fqn namespaceBody?
  public static boolean namespaceDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDecl")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_NAMESPACE_DECL, "<namespace decl>");
    r = consumeToken(b, I_NAMESPACE);
    p = r; // pin = 1
    r = r && report_error_(b, fqn(b, l + 1));
    r = p && namespaceDecl_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, namespaceDeclRecover_parser_);
    return r || p;
  }

  // namespaceBody?
  private static boolean namespaceDecl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDecl_2")) return false;
    namespaceBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ! ('import' | 'namespace' )
  static boolean namespaceDeclRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDeclRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !namespaceDeclRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'import' | 'namespace'
  private static boolean namespaceDeclRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDeclRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_IMPORT);
    if (!r) r = consumeToken(b, I_NAMESPACE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (dataTypeSpec '@')? 'null'
  public static boolean nullDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nullDatum")) return false;
    if (!nextTokenIs(b, "<null datum>", I_NULL, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_NULL_DATUM, "<null datum>");
    r = nullDatum_0(b, l + 1);
    r = r && consumeToken(b, I_NULL);
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
    r = r && consumeToken(b, I_AT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (opInputFieldProjectionBodyPart ','? )* opInputComaVarProjection? '}'
  static boolean opInputComaComplexFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaComplexFieldProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_CURLY_LEFT);
    r = r && opInputComaComplexFieldProjection_1(b, l + 1);
    r = r && opInputComaComplexFieldProjection_2(b, l + 1);
    r = r && consumeToken(b, I_CURLY_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (opInputFieldProjectionBodyPart ','? )*
  private static boolean opInputComaComplexFieldProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaComplexFieldProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputComaComplexFieldProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputComaComplexFieldProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputFieldProjectionBodyPart ','?
  private static boolean opInputComaComplexFieldProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaComplexFieldProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputFieldProjectionBodyPart(b, l + 1);
    r = r && opInputComaComplexFieldProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputComaComplexFieldProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaComplexFieldProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // opInputComaVarProjection?
  private static boolean opInputComaComplexFieldProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaComplexFieldProjection_2")) return false;
    opInputComaVarProjection(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (opInputModelProperty ','?)* opInputComaModelProjection '}'
  static boolean opInputComaComplexTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaComplexTagProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opInputComaComplexTagProjection_1(b, l + 1));
    r = p && report_error_(b, opInputComaModelProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opInputModelProperty ','?)*
  private static boolean opInputComaComplexTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaComplexTagProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputComaComplexTagProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputComaComplexTagProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputModelProperty ','?
  private static boolean opInputComaComplexTagProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaComplexTagProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputModelProperty(b, l + 1);
    r = r && opInputComaComplexTagProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputComaComplexTagProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaComplexTagProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? qid (opInputComaComplexFieldProjection | opInputComaSimpleFieldProjection)
  public static boolean opInputComaFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaFieldProjection")) return false;
    if (!nextTokenIs(b, "<op input coma field projection>", I_PLUS, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_COMA_FIELD_PROJECTION, "<op input coma field projection>");
    r = opInputComaFieldProjection_0(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && opInputComaFieldProjection_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opInputComaFieldProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaFieldProjection_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opInputComaComplexFieldProjection | opInputComaSimpleFieldProjection
  private static boolean opInputComaFieldProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaFieldProjection_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComaComplexFieldProjection(b, l + 1);
    if (!r) r = opInputComaSimpleFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '[' ']'
  public static boolean opInputComaKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaKeyProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_COMA_KEY_PROJECTION, null);
    r = consumeToken(b, I_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && consumeToken(b, I_BRACKET_RIGHT);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '*' '(' opInputComaVarProjection ')'
  public static boolean opInputComaListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaListModelProjection")) return false;
    if (!nextTokenIs(b, I_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_COMA_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, I_STAR);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_PAREN_LEFT));
    r = p && report_error_(b, opInputComaVarProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // opInputComaKeyProjection '(' opInputComaVarProjection ')'
  public static boolean opInputComaMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaMapModelProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComaKeyProjection(b, l + 1);
    r = r && consumeToken(b, I_PAREN_LEFT);
    r = r && opInputComaVarProjection(b, l + 1);
    r = r && consumeToken(b, I_PAREN_RIGHT);
    exit_section_(b, m, I_OP_INPUT_COMA_MAP_MODEL_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // ( opInputComaRecordModelProjection
  //                                | opInputComaListModelProjection
  //                                | opInputComaMapModelProjection
  //                                )?
  public static boolean opInputComaModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_COMA_MODEL_PROJECTION, "<op input coma model projection>");
    opInputComaModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // opInputComaRecordModelProjection
  //                                | opInputComaListModelProjection
  //                                | opInputComaMapModelProjection
  private static boolean opInputComaModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComaRecordModelProjection(b, l + 1);
    if (!r) r = opInputComaListModelProjection(b, l + 1);
    if (!r) r = opInputComaMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ':' '(' (opInputComaMultiTagProjectionItem ','?)* ')'
  public static boolean opInputComaMultiTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaMultiTagProjection")) return false;
    if (!nextTokenIs(b, I_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_COMA_MULTI_TAG_PROJECTION, null);
    r = consumeToken(b, I_COLON);
    r = r && consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, opInputComaMultiTagProjection_2(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opInputComaMultiTagProjectionItem ','?)*
  private static boolean opInputComaMultiTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaMultiTagProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputComaMultiTagProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputComaMultiTagProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputComaMultiTagProjectionItem ','?
  private static boolean opInputComaMultiTagProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaMultiTagProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComaMultiTagProjectionItem(b, l + 1);
    r = r && opInputComaMultiTagProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputComaMultiTagProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaMultiTagProjection_2_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? tagName ( opInputComaComplexTagProjection | opInputComaSimpleTagProjection )
  public static boolean opInputComaMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaMultiTagProjectionItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_COMA_MULTI_TAG_PROJECTION_ITEM, "<op input coma multi tag projection item>");
    r = opInputComaMultiTagProjectionItem_0(b, l + 1);
    r = r && tagName(b, l + 1);
    r = r && opInputComaMultiTagProjectionItem_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opInputComaMultiTagProjectionItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaMultiTagProjectionItem_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opInputComaComplexTagProjection | opInputComaSimpleTagProjection
  private static boolean opInputComaMultiTagProjectionItem_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaMultiTagProjectionItem_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComaComplexTagProjection(b, l + 1);
    if (!r) r = opInputComaSimpleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (opInputComaFieldProjection ','?)* ')'
  public static boolean opInputComaRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaRecordModelProjection")) return false;
    if (!nextTokenIs(b, I_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_COMA_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opInputComaRecordModelProjection_1(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opInputComaFieldProjection ','?)*
  private static boolean opInputComaRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaRecordModelProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputComaRecordModelProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputComaRecordModelProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputComaFieldProjection ','?
  private static boolean opInputComaRecordModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaRecordModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComaFieldProjection(b, l + 1);
    r = r && opInputComaRecordModelProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputComaRecordModelProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaRecordModelProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opInputComaVarProjection
  static boolean opInputComaSimpleFieldProjection(PsiBuilder b, int l) {
    return opInputComaVarProjection(b, l + 1);
  }

  /* ********************************************************** */
  // opInputComaModelProjection
  static boolean opInputComaSimpleTagProjection(PsiBuilder b, int l) {
    return opInputComaModelProjection(b, l + 1);
  }

  /* ********************************************************** */
  // ( ':' '+'? tagName)? (opInputComaComplexTagProjection | opInputComaSimpleTagProjection )
  public static boolean opInputComaSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_COMA_SINGLE_TAG_PROJECTION, "<op input coma single tag projection>");
    r = opInputComaSingleTagProjection_0(b, l + 1);
    r = r && opInputComaSingleTagProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' '+'? tagName)?
  private static boolean opInputComaSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaSingleTagProjection_0")) return false;
    opInputComaSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' '+'? tagName
  private static boolean opInputComaSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_COLON);
    r = r && opInputComaSingleTagProjection_0_0_1(b, l + 1);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean opInputComaSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaSingleTagProjection_0_0_1")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opInputComaComplexTagProjection | opInputComaSimpleTagProjection
  private static boolean opInputComaSingleTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaSingleTagProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComaComplexTagProjection(b, l + 1);
    if (!r) r = opInputComaSimpleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( opInputComaMultiTagProjection | opInputComaSingleTagProjection ) opInputVarPolymorphicTail?
  public static boolean opInputComaVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_COMA_VAR_PROJECTION, "<op input coma var projection>");
    r = opInputComaVarProjection_0(b, l + 1);
    r = r && opInputComaVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // opInputComaMultiTagProjection | opInputComaSingleTagProjection
  private static boolean opInputComaVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComaMultiTagProjection(b, l + 1);
    if (!r) r = opInputComaSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opInputVarPolymorphicTail?
  private static boolean opInputComaVarProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComaVarProjection_1")) return false;
    opInputVarPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'default' ':' datum
  public static boolean opInputDefaultValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputDefaultValue")) return false;
    if (!nextTokenIs(b, I_DEFAULT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_DEFAULT_VALUE, null);
    r = consumeToken(b, I_DEFAULT);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_COLON));
    r = p && datum(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // annotation
  public static boolean opInputFieldProjectionBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputFieldProjectionBodyPart")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = annotation(b, l + 1);
    exit_section_(b, m, I_OP_INPUT_FIELD_PROJECTION_BODY_PART, r);
    return r;
  }

  /* ********************************************************** */
  // 'meta' ':' '+'? opInputComaModelProjection
  public static boolean opInputModelMeta(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelMeta")) return false;
    if (!nextTokenIs(b, I_META)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_META);
    r = r && consumeToken(b, I_COLON);
    r = r && opInputModelMeta_2(b, l + 1);
    r = r && opInputComaModelProjection(b, l + 1);
    exit_section_(b, m, I_OP_INPUT_MODEL_META, r);
    return r;
  }

  // '+'?
  private static boolean opInputModelMeta_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelMeta_2")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  /* ********************************************************** */
  // opInputDefaultValue | annotation | opInputModelMeta
  public static boolean opInputModelProperty(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelProperty")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_MODEL_PROPERTY, "<op input model property>");
    r = opInputDefaultValue(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    if (!r) r = opInputModelMeta(b, l + 1);
    exit_section_(b, l, m, r, false, opInputModelPropertyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // ! ( '}' )
  static boolean opInputModelPropertyRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelPropertyRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !opInputModelPropertyRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( '}' )
  private static boolean opInputModelPropertyRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelPropertyRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_CURLY_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (opInputFieldProjectionBodyPart ','? )* opInputTrunkVarProjection? '}'
  static boolean opInputTrunkComplexFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkComplexFieldProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_CURLY_LEFT);
    r = r && opInputTrunkComplexFieldProjection_1(b, l + 1);
    r = r && opInputTrunkComplexFieldProjection_2(b, l + 1);
    r = r && consumeToken(b, I_CURLY_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (opInputFieldProjectionBodyPart ','? )*
  private static boolean opInputTrunkComplexFieldProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkComplexFieldProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputTrunkComplexFieldProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputTrunkComplexFieldProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputFieldProjectionBodyPart ','?
  private static boolean opInputTrunkComplexFieldProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkComplexFieldProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputFieldProjectionBodyPart(b, l + 1);
    r = r && opInputTrunkComplexFieldProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputTrunkComplexFieldProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkComplexFieldProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // opInputTrunkVarProjection?
  private static boolean opInputTrunkComplexFieldProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkComplexFieldProjection_2")) return false;
    opInputTrunkVarProjection(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (opInputModelProperty ','?)* opInputTrunkModelProjection '}'
  static boolean opInputTrunkComplexSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkComplexSingleTagProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opInputTrunkComplexSingleTagProjection_1(b, l + 1));
    r = p && report_error_(b, opInputTrunkModelProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opInputModelProperty ','?)*
  private static boolean opInputTrunkComplexSingleTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkComplexSingleTagProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputTrunkComplexSingleTagProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputTrunkComplexSingleTagProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputModelProperty ','?
  private static boolean opInputTrunkComplexSingleTagProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkComplexSingleTagProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputModelProperty(b, l + 1);
    r = r && opInputTrunkComplexSingleTagProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputTrunkComplexSingleTagProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkComplexSingleTagProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? qid (opInputTrunkComplexFieldProjection | opInputTrunkSimpleFieldProjection)
  public static boolean opInputTrunkFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkFieldProjection")) return false;
    if (!nextTokenIs(b, "<op input trunk field projection>", I_PLUS, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_TRUNK_FIELD_PROJECTION, "<op input trunk field projection>");
    r = opInputTrunkFieldProjection_0(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && opInputTrunkFieldProjection_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opInputTrunkFieldProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkFieldProjection_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opInputTrunkComplexFieldProjection | opInputTrunkSimpleFieldProjection
  private static boolean opInputTrunkFieldProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkFieldProjection_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputTrunkComplexFieldProjection(b, l + 1);
    if (!r) r = opInputTrunkSimpleFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( opInputTrunkRecordModelProjection
  //                                 | opInputComaRecordModelProjection
  //                                 | opInputComaListModelProjection
  //                                 | opInputComaMapModelProjection
  //                                 )?
  public static boolean opInputTrunkModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_TRUNK_MODEL_PROJECTION, "<op input trunk model projection>");
    opInputTrunkModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // opInputTrunkRecordModelProjection
  //                                 | opInputComaRecordModelProjection
  //                                 | opInputComaListModelProjection
  //                                 | opInputComaMapModelProjection
  private static boolean opInputTrunkModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputTrunkRecordModelProjection(b, l + 1);
    if (!r) r = opInputComaRecordModelProjection(b, l + 1);
    if (!r) r = opInputComaListModelProjection(b, l + 1);
    if (!r) r = opInputComaMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '/' opInputTrunkFieldProjection
  public static boolean opInputTrunkRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkRecordModelProjection")) return false;
    if (!nextTokenIs(b, I_SLASH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_TRUNK_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, I_SLASH);
    p = r; // pin = 1
    r = r && opInputTrunkFieldProjection(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // opInputTrunkVarProjection
  static boolean opInputTrunkSimpleFieldProjection(PsiBuilder b, int l) {
    return opInputTrunkVarProjection(b, l + 1);
  }

  /* ********************************************************** */
  // opInputTrunkModelProjection
  static boolean opInputTrunkSimpleSingleTagProjection(PsiBuilder b, int l) {
    return opInputTrunkModelProjection(b, l + 1);
  }

  /* ********************************************************** */
  // ( ':' '+'? tagName)? (opInputTrunkComplexSingleTagProjection | opInputTrunkSimpleSingleTagProjection )
  public static boolean opInputTrunkSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_TRUNK_SINGLE_TAG_PROJECTION, "<op input trunk single tag projection>");
    r = opInputTrunkSingleTagProjection_0(b, l + 1);
    r = r && opInputTrunkSingleTagProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' '+'? tagName)?
  private static boolean opInputTrunkSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkSingleTagProjection_0")) return false;
    opInputTrunkSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' '+'? tagName
  private static boolean opInputTrunkSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_COLON);
    r = r && opInputTrunkSingleTagProjection_0_0_1(b, l + 1);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean opInputTrunkSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkSingleTagProjection_0_0_1")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opInputTrunkComplexSingleTagProjection | opInputTrunkSimpleSingleTagProjection
  private static boolean opInputTrunkSingleTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkSingleTagProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputTrunkComplexSingleTagProjection(b, l + 1);
    if (!r) r = opInputTrunkSimpleSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( opInputComaMultiTagProjection | opInputTrunkSingleTagProjection ) opInputVarPolymorphicTail?
  public static boolean opInputTrunkVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_TRUNK_VAR_PROJECTION, "<op input trunk var projection>");
    r = opInputTrunkVarProjection_0(b, l + 1);
    r = r && opInputTrunkVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // opInputComaMultiTagProjection | opInputTrunkSingleTagProjection
  private static boolean opInputTrunkVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComaMultiTagProjection(b, l + 1);
    if (!r) r = opInputTrunkSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opInputVarPolymorphicTail?
  private static boolean opInputTrunkVarProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputTrunkVarProjection_1")) return false;
    opInputVarPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '~' '(' (opInputVarMultiTailItem ','?)* ')'
  public static boolean opInputVarMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarMultiTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_VAR_MULTI_TAIL, null);
    r = consumeToken(b, I_TILDA);
    r = r && consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, opInputVarMultiTail_2(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opInputVarMultiTailItem ','?)*
  private static boolean opInputVarMultiTail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarMultiTail_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputVarMultiTail_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputVarMultiTail_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputVarMultiTailItem ','?
  private static boolean opInputVarMultiTail_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarMultiTail_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputVarMultiTailItem(b, l + 1);
    r = r && opInputVarMultiTail_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputVarMultiTail_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarMultiTail_2_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // fqnTypeRef opInputComaVarProjection
  public static boolean opInputVarMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarMultiTailItem")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fqnTypeRef(b, l + 1);
    r = r && opInputComaVarProjection(b, l + 1);
    exit_section_(b, m, I_OP_INPUT_VAR_MULTI_TAIL_ITEM, r);
    return r;
  }

  /* ********************************************************** */
  // opInputVarSingleTail | opInputVarMultiTail
  public static boolean opInputVarPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarPolymorphicTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputVarSingleTail(b, l + 1);
    if (!r) r = opInputVarMultiTail(b, l + 1);
    exit_section_(b, m, I_OP_INPUT_VAR_POLYMORPHIC_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // '~' fqnTypeRef opInputComaVarProjection
  public static boolean opInputVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarSingleTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_TILDA);
    r = r && fqnTypeRef(b, l + 1);
    r = r && opInputComaVarProjection(b, l + 1);
    exit_section_(b, m, I_OP_INPUT_VAR_SINGLE_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (opOutputFieldProjectionBodyPart ','? )* opOutputVarProjection? '}'
  static boolean opOutputComplexFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexFieldProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_CURLY_LEFT);
    r = r && opOutputComplexFieldProjection_1(b, l + 1);
    r = r && opOutputComplexFieldProjection_2(b, l + 1);
    r = r && consumeToken(b, I_CURLY_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (opOutputFieldProjectionBodyPart ','? )*
  private static boolean opOutputComplexFieldProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexFieldProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputComplexFieldProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputComplexFieldProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputFieldProjectionBodyPart ','?
  private static boolean opOutputComplexFieldProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexFieldProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputFieldProjectionBodyPart(b, l + 1);
    r = r && opOutputComplexFieldProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputComplexFieldProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexFieldProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // opOutputVarProjection?
  private static boolean opOutputComplexFieldProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexFieldProjection_2")) return false;
    opOutputVarProjection(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (opOutputModelProperty ','?)* opOutputModelProjection '}'
  static boolean opOutputComplexTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexTagProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opOutputComplexTagProjection_1(b, l + 1));
    r = p && report_error_(b, opOutputModelProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opOutputModelProperty ','?)*
  private static boolean opOutputComplexTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexTagProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputComplexTagProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputComplexTagProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputModelProperty ','?
  private static boolean opOutputComplexTagProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexTagProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputModelProperty(b, l + 1);
    r = r && opOutputComplexTagProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputComplexTagProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexTagProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? qid (opOutputComplexFieldProjection | opOutputSimpleFieldProjection)
  public static boolean opOutputFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjection")) return false;
    if (!nextTokenIs(b, "<op output field projection>", I_PLUS, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_FIELD_PROJECTION, "<op output field projection>");
    r = opOutputFieldProjection_0(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && opOutputFieldProjection_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opOutputFieldProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjection_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opOutputComplexFieldProjection | opOutputSimpleFieldProjection
  private static boolean opOutputFieldProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjection_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputComplexFieldProjection(b, l + 1);
    if (!r) r = opOutputSimpleFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // opParam | annotation
  public static boolean opOutputFieldProjectionBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjectionBodyPart")) return false;
    if (!nextTokenIs(b, "<op output field projection body part>", I_SEMICOLON, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_FIELD_PROJECTION_BODY_PART, "<op output field projection body part>");
    r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' opOutputKeyProjectionInt ']'
  public static boolean opOutputKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_KEY_PROJECTION, null);
    r = consumeToken(b, I_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opOutputKeyProjectionInt(b, l + 1));
    r = p && consumeToken(b, I_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ('required' ','?| 'forbidden' ','?)? (opOutputKeyProjectionPart ','?)*
  static boolean opOutputKeyProjectionInt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionInt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = opOutputKeyProjectionInt_0(b, l + 1);
    r = r && opOutputKeyProjectionInt_1(b, l + 1);
    exit_section_(b, l, m, r, false, opOutputKeyProjectionRecover_parser_);
    return r;
  }

  // ('required' ','?| 'forbidden' ','?)?
  private static boolean opOutputKeyProjectionInt_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionInt_0")) return false;
    opOutputKeyProjectionInt_0_0(b, l + 1);
    return true;
  }

  // 'required' ','?| 'forbidden' ','?
  private static boolean opOutputKeyProjectionInt_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionInt_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputKeyProjectionInt_0_0_0(b, l + 1);
    if (!r) r = opOutputKeyProjectionInt_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'required' ','?
  private static boolean opOutputKeyProjectionInt_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionInt_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_REQURIED);
    r = r && opOutputKeyProjectionInt_0_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputKeyProjectionInt_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionInt_0_0_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // 'forbidden' ','?
  private static boolean opOutputKeyProjectionInt_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionInt_0_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_FORBIDDEN);
    r = r && opOutputKeyProjectionInt_0_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputKeyProjectionInt_0_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionInt_0_0_1_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // (opOutputKeyProjectionPart ','?)*
  private static boolean opOutputKeyProjectionInt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionInt_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputKeyProjectionInt_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputKeyProjectionInt_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputKeyProjectionPart ','?
  private static boolean opOutputKeyProjectionInt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionInt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputKeyProjectionPart(b, l + 1);
    r = r && opOutputKeyProjectionInt_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputKeyProjectionInt_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionInt_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opParam | annotation
  public static boolean opOutputKeyProjectionPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionPart")) return false;
    if (!nextTokenIs(b, "<op output key projection part>", I_SEMICOLON, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_KEY_PROJECTION_PART, "<op output key projection part>");
    r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ! ( ']' )
  static boolean opOutputKeyProjectionRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !opOutputKeyProjectionRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ']' )
  private static boolean opOutputKeyProjectionRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_BRACKET_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '*' '(' opOutputVarProjection ')'
  public static boolean opOutputListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputListModelProjection")) return false;
    if (!nextTokenIs(b, I_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, I_STAR);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_PAREN_LEFT));
    r = p && report_error_(b, opOutputVarProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // opOutputKeyProjection '(' opOutputVarProjection ')'
  public static boolean opOutputMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMapModelProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_MAP_MODEL_PROJECTION, null);
    r = opOutputKeyProjection(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_PAREN_LEFT));
    r = p && report_error_(b, opOutputVarProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'meta' ':' '+'? opOutputModelProjection
  public static boolean opOutputModelMeta(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelMeta")) return false;
    if (!nextTokenIs(b, I_META)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_META);
    r = r && consumeToken(b, I_COLON);
    r = r && opOutputModelMeta_2(b, l + 1);
    r = r && opOutputModelProjection(b, l + 1);
    exit_section_(b, m, I_OP_OUTPUT_MODEL_META, r);
    return r;
  }

  // '+'?
  private static boolean opOutputModelMeta_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelMeta_2")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  /* ********************************************************** */
  // ( opOutputRecordModelProjection
  //                             | opOutputListModelProjection
  //                             | opOutputMapModelProjection
  // //                          | opOutputEnumModelProjection
  // //                          | opOutputPrimitiveModelProjection
  //                             )?
  public static boolean opOutputModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_MODEL_PROJECTION, "<op output model projection>");
    opOutputModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // opOutputRecordModelProjection
  //                             | opOutputListModelProjection
  //                             | opOutputMapModelProjection
  private static boolean opOutputModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputRecordModelProjection(b, l + 1);
    if (!r) r = opOutputListModelProjection(b, l + 1);
    if (!r) r = opOutputMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // opParam | annotation | opOutputModelMeta
  public static boolean opOutputModelProperty(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProperty")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_MODEL_PROPERTY, "<op output model property>");
    r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    if (!r) r = opOutputModelMeta(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ':' '(' (opOutputMultiTagProjectionItem ','?)* ')'
  public static boolean opOutputMultiTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMultiTagProjection")) return false;
    if (!nextTokenIs(b, I_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_MULTI_TAG_PROJECTION, null);
    r = consumeToken(b, I_COLON);
    r = r && consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, opOutputMultiTagProjection_2(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opOutputMultiTagProjectionItem ','?)*
  private static boolean opOutputMultiTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMultiTagProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputMultiTagProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputMultiTagProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputMultiTagProjectionItem ','?
  private static boolean opOutputMultiTagProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMultiTagProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputMultiTagProjectionItem(b, l + 1);
    r = r && opOutputMultiTagProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputMultiTagProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMultiTagProjection_2_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? tagName ( opOutputComplexTagProjection | opOutputSimpleTagProjection )
  public static boolean opOutputMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMultiTagProjectionItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM, "<op output multi tag projection item>");
    r = opOutputMultiTagProjectionItem_0(b, l + 1);
    r = r && tagName(b, l + 1);
    r = r && opOutputMultiTagProjectionItem_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opOutputMultiTagProjectionItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMultiTagProjectionItem_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opOutputComplexTagProjection | opOutputSimpleTagProjection
  private static boolean opOutputMultiTagProjectionItem_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMultiTagProjectionItem_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputComplexTagProjection(b, l + 1);
    if (!r) r = opOutputSimpleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (opOutputFieldProjection ','?)* ')'
  public static boolean opOutputRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputRecordModelProjection")) return false;
    if (!nextTokenIs(b, I_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opOutputRecordModelProjection_1(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opOutputFieldProjection ','?)*
  private static boolean opOutputRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputRecordModelProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputRecordModelProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputRecordModelProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputFieldProjection ','?
  private static boolean opOutputRecordModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputRecordModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputFieldProjection(b, l + 1);
    r = r && opOutputRecordModelProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputRecordModelProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputRecordModelProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opOutputVarProjection
  static boolean opOutputSimpleFieldProjection(PsiBuilder b, int l) {
    return opOutputVarProjection(b, l + 1);
  }

  /* ********************************************************** */
  // opOutputModelProjection
  static boolean opOutputSimpleTagProjection(PsiBuilder b, int l) {
    return opOutputModelProjection(b, l + 1);
  }

  /* ********************************************************** */
  // ( ':' '+'? tagName)? (opOutputComplexTagProjection | opOutputSimpleTagProjection )
  public static boolean opOutputSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_SINGLE_TAG_PROJECTION, "<op output single tag projection>");
    r = opOutputSingleTagProjection_0(b, l + 1);
    r = r && opOutputSingleTagProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' '+'? tagName)?
  private static boolean opOutputSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection_0")) return false;
    opOutputSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' '+'? tagName
  private static boolean opOutputSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_COLON);
    r = r && opOutputSingleTagProjection_0_0_1(b, l + 1);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean opOutputSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection_0_0_1")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opOutputComplexTagProjection | opOutputSimpleTagProjection
  private static boolean opOutputSingleTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputComplexTagProjection(b, l + 1);
    if (!r) r = opOutputSimpleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '~' '(' (opOutputVarMultiTailItem ','?)* ')'
  public static boolean opOutputVarMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarMultiTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_VAR_MULTI_TAIL, null);
    r = consumeToken(b, I_TILDA);
    r = r && consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, opOutputVarMultiTail_2(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opOutputVarMultiTailItem ','?)*
  private static boolean opOutputVarMultiTail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarMultiTail_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputVarMultiTail_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputVarMultiTail_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputVarMultiTailItem ','?
  private static boolean opOutputVarMultiTail_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarMultiTail_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputVarMultiTailItem(b, l + 1);
    r = r && opOutputVarMultiTail_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputVarMultiTail_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarMultiTail_2_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // fqnTypeRef opOutputVarProjection
  public static boolean opOutputVarMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarMultiTailItem")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fqnTypeRef(b, l + 1);
    r = r && opOutputVarProjection(b, l + 1);
    exit_section_(b, m, I_OP_OUTPUT_VAR_MULTI_TAIL_ITEM, r);
    return r;
  }

  /* ********************************************************** */
  // opOutputVarSingleTail | opOutputVarMultiTail
  public static boolean opOutputVarPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarPolymorphicTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputVarSingleTail(b, l + 1);
    if (!r) r = opOutputVarMultiTail(b, l + 1);
    exit_section_(b, m, I_OP_OUTPUT_VAR_POLYMORPHIC_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // ( opOutputMultiTagProjection | opOutputSingleTagProjection ) opOutputVarPolymorphicTail?
  public static boolean opOutputVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_VAR_PROJECTION, "<op output var projection>");
    r = opOutputVarProjection_0(b, l + 1);
    r = r && opOutputVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // opOutputMultiTagProjection | opOutputSingleTagProjection
  private static boolean opOutputVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputMultiTagProjection(b, l + 1);
    if (!r) r = opOutputSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opOutputVarPolymorphicTail?
  private static boolean opOutputVarProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarProjection_1")) return false;
    opOutputVarPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '~' fqnTypeRef opOutputVarProjection
  public static boolean opOutputVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarSingleTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_TILDA);
    r = r && fqnTypeRef(b, l + 1);
    r = r && opOutputVarProjection(b, l + 1);
    exit_section_(b, m, I_OP_OUTPUT_VAR_SINGLE_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // ';' '+'? qid ':' fqnTypeRef opInputComaModelProjection opParamDefault? opParamBody?
  public static boolean opParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParam")) return false;
    if (!nextTokenIs(b, I_SEMICOLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_PARAM, null);
    r = consumeToken(b, I_SEMICOLON);
    p = r; // pin = 1
    r = r && report_error_(b, opParam_1(b, l + 1));
    r = p && report_error_(b, qid(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, I_COLON)) && r;
    r = p && report_error_(b, fqnTypeRef(b, l + 1)) && r;
    r = p && report_error_(b, opInputComaModelProjection(b, l + 1)) && r;
    r = p && report_error_(b, opParam_6(b, l + 1)) && r;
    r = p && opParam_7(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean opParam_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParam_1")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opParamDefault?
  private static boolean opParam_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParam_6")) return false;
    opParamDefault(b, l + 1);
    return true;
  }

  // opParamBody?
  private static boolean opParam_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParam_7")) return false;
    opParamBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' ( opParamBodyPart ','? )* '}'
  static boolean opParamBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParamBody")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opParamBody_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ( opParamBodyPart ','? )*
  private static boolean opParamBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParamBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opParamBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opParamBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opParamBodyPart ','?
  private static boolean opParamBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParamBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opParamBodyPart(b, l + 1);
    r = r && opParamBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opParamBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParamBody_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // annotation
  static boolean opParamBodyPart(PsiBuilder b, int l) {
    return annotation(b, l + 1);
  }

  /* ********************************************************** */
  // '=' datum
  static boolean opParamDefault(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParamDefault")) return false;
    if (!nextTokenIs(b, I_EQ)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_EQ);
    p = r; // pin = 1
    r = r && datum(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (dataTypeSpec '@')? (string | number | boolean)
  public static boolean primitiveDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveDatum")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_PRIMITIVE_DATUM, "<primitive datum>");
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
    r = r && consumeToken(b, I_AT);
    exit_section_(b, m, null, r);
    return r;
  }

  // string | number | boolean
  private static boolean primitiveDatum_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveDatum_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_STRING);
    if (!r) r = consumeToken(b, I_NUMBER);
    if (!r) r = consumeToken(b, I_BOOLEAN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // id
  public static boolean qid(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qid")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_ID);
    exit_section_(b, m, I_QID, r);
    return r;
  }

  /* ********************************************************** */
  // dataTypeSpec? '{' recordDatumEntry* '}'
  public static boolean recordDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordDatum")) return false;
    if (!nextTokenIs(b, "<record datum>", I_CURLY_LEFT, I_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_RECORD_DATUM, "<record datum>");
    r = recordDatum_0(b, l + 1);
    r = r && consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, recordDatum_2(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
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
    Marker m = enter_section_(b, l, _NONE_, I_RECORD_DATUM_ENTRY, "<record datum entry>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, I_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataValue(b, l + 1));
    r = p && recordDatumEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean recordDatumEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordDatumEntry_3")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '!' annotation
  public static boolean reqAnnotation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqAnnotation")) return false;
    if (!nextTokenIs(b, I_BANG)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_BANG);
    r = r && annotation(b, l + 1);
    exit_section_(b, m, I_REQ_ANNOTATION, r);
    return r;
  }

  /* ********************************************************** */
  // '+'? qid reqParamsAndAnnotations reqOutputComaVarProjection
  public static boolean reqOutputComaFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaFieldProjection")) return false;
    if (!nextTokenIs(b, "<req output coma field projection>", I_PLUS, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_COMA_FIELD_PROJECTION, "<req output coma field projection>");
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
    consumeToken(b, I_PLUS);
    return true;
  }

  /* ********************************************************** */
  // datum reqParamsAndAnnotations
  public static boolean reqOutputComaKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeyProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_COMA_KEY_PROJECTION, "<req output coma key projection>");
    r = datum(b, l + 1);
    r = r && reqParamsAndAnnotations(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '+'? ( '[' ( reqOutputComaKeyProjection ','? )* ']' ) | ( '[' '*' ']' )
  public static boolean reqOutputComaKeysProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection")) return false;
    if (!nextTokenIs(b, "<req output coma keys projection>", I_PLUS, I_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_COMA_KEYS_PROJECTION, "<req output coma keys projection>");
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
    consumeToken(b, I_PLUS);
    return true;
  }

  // '[' ( reqOutputComaKeyProjection ','? )* ']'
  private static boolean reqOutputComaKeysProjection_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_BRACKET_LEFT);
    r = r && reqOutputComaKeysProjection_0_1_1(b, l + 1);
    r = r && consumeToken(b, I_BRACKET_RIGHT);
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
    consumeToken(b, I_COMMA);
    return true;
  }

  // '[' '*' ']'
  private static boolean reqOutputComaKeysProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaKeysProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_BRACKET_LEFT);
    r = r && consumeToken(b, I_STAR);
    r = r && consumeToken(b, I_BRACKET_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '*' '(' reqOutputComaVarProjection ')'
  public static boolean reqOutputComaListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaListModelProjection")) return false;
    if (!nextTokenIs(b, I_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_COMA_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, I_STAR);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_PAREN_LEFT));
    r = p && report_error_(b, reqOutputComaVarProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // reqOutputComaKeysProjection '(' reqOutputComaVarProjection ')'
  public static boolean reqOutputComaMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMapModelProjection")) return false;
    if (!nextTokenIs(b, "<req output coma map model projection>", I_PLUS, I_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_COMA_MAP_MODEL_PROJECTION, "<req output coma map model projection>");
    r = reqOutputComaKeysProjection(b, l + 1);
    r = r && consumeToken(b, I_PAREN_LEFT);
    r = r && reqOutputComaVarProjection(b, l + 1);
    r = r && consumeToken(b, I_PAREN_RIGHT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( reqOutputComaRecordModelProjection
  //                                  | reqOutputComaListModelProjection
  //                                  | reqOutputComaMapModelProjection
  //                                  )?
  public static boolean reqOutputComaModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_COMA_MODEL_PROJECTION, "<req output coma model projection>");
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
    if (!nextTokenIs(b, I_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION, null);
    r = consumeToken(b, I_COLON);
    r = r && consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, reqOutputComaMultiTagProjection_2(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
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
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? tagName reqOutputComaTagProjectionItem
  public static boolean reqOutputComaMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMultiTagProjectionItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_COMA_MULTI_TAG_PROJECTION_ITEM, "<req output coma multi tag projection item>");
    r = reqOutputComaMultiTagProjectionItem_0(b, l + 1);
    r = r && tagName(b, l + 1);
    r = r && reqOutputComaTagProjectionItem(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean reqOutputComaMultiTagProjectionItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaMultiTagProjectionItem_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '(' (reqOutputComaFieldProjection ','?)* ')'
  public static boolean reqOutputComaRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaRecordModelProjection")) return false;
    if (!nextTokenIs(b, I_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_COMA_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, reqOutputComaRecordModelProjection_1(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
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
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ( ':' '+'? tagName)? reqOutputComaTagProjectionItem
  public static boolean reqOutputComaSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_COMA_SINGLE_TAG_PROJECTION, "<req output coma single tag projection>");
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
    r = consumeToken(b, I_COLON);
    r = r && reqOutputComaSingleTagProjection_0_0_1(b, l + 1);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean reqOutputComaSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputComaSingleTagProjection_0_0_1")) return false;
    consumeToken(b, I_PLUS);
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
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_COMA_VAR_PROJECTION, "<req output coma var projection>");
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
    if (!nextTokenIs(b, I_AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_AT);
    r = r && reqOutputModelMeta_1(b, l + 1);
    r = r && reqOutputComaModelProjection(b, l + 1);
    exit_section_(b, m, I_REQ_OUTPUT_MODEL_META, r);
    return r;
  }

  // '+'?
  private static boolean reqOutputModelMeta_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputModelMeta_1")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '+'? qid reqParamsAndAnnotations reqOutputTrunkVarProjection
  static boolean reqOutputTrunkFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkFieldProjection")) return false;
    if (!nextTokenIs(b, "", I_PLUS, I_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = reqOutputTrunkFieldProjection_0(b, l + 1);
    r = r && qid(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, reqParamsAndAnnotations(b, l + 1));
    r = p && reqOutputTrunkVarProjection(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean reqOutputTrunkFieldProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkFieldProjection_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
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
    consumeToken(b, I_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '/' reqOutputTrunkKeyProjection
  public static boolean reqOutputTrunkMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkMapModelProjection")) return false;
    if (!nextTokenIs(b, I_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_SLASH);
    r = r && reqOutputTrunkKeyProjection(b, l + 1);
    exit_section_(b, m, I_REQ_OUTPUT_TRUNK_MAP_MODEL_PROJECTION, r);
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
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_TRUNK_MODEL_PROJECTION, "<req output trunk model projection>");
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
  // '/' reqOutputTrunkFieldProjection
  public static boolean reqOutputTrunkRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkRecordModelProjection")) return false;
    if (!nextTokenIs(b, I_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_SLASH);
    r = r && reqOutputTrunkFieldProjection(b, l + 1);
    exit_section_(b, m, I_REQ_OUTPUT_TRUNK_RECORD_MODEL_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // ( ':' '+'? tagName )? reqParamsAndAnnotations reqOutputTrunkModelProjection reqOutputModelMeta?
  public static boolean reqOutputTrunkSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_TRUNK_SINGLE_TAG_PROJECTION, "<req output trunk single tag projection>");
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
    r = consumeToken(b, I_COLON);
    r = r && reqOutputTrunkSingleTagProjection_0_0_1(b, l + 1);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean reqOutputTrunkSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputTrunkSingleTagProjection_0_0_1")) return false;
    consumeToken(b, I_PLUS);
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
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_TRUNK_VAR_PROJECTION, "<req output trunk var projection>");
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
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_REQ_OUTPUT_VAR_MULTI_TAIL, null);
    r = consumeToken(b, I_TILDA);
    r = r && consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, reqOutputVarMultiTail_2(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
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
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // fqnTypeRef reqOutputComaVarProjection
  public static boolean reqOutputVarMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarMultiTailItem")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fqnTypeRef(b, l + 1);
    r = r && reqOutputComaVarProjection(b, l + 1);
    exit_section_(b, m, I_REQ_OUTPUT_VAR_MULTI_TAIL_ITEM, r);
    return r;
  }

  /* ********************************************************** */
  // reqOutputVarSingleTail | reqOutputVarMultiTail
  public static boolean reqOutputVarPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarPolymorphicTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reqOutputVarSingleTail(b, l + 1);
    if (!r) r = reqOutputVarMultiTail(b, l + 1);
    exit_section_(b, m, I_REQ_OUTPUT_VAR_POLYMORPHIC_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // '~' fqnTypeRef reqOutputComaVarProjection
  public static boolean reqOutputVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqOutputVarSingleTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_TILDA);
    r = r && fqnTypeRef(b, l + 1);
    r = r && reqOutputComaVarProjection(b, l + 1);
    exit_section_(b, m, I_REQ_OUTPUT_VAR_SINGLE_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // ';' qid '=' datum
  public static boolean reqParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reqParam")) return false;
    if (!nextTokenIs(b, I_SEMICOLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_SEMICOLON);
    r = r && qid(b, l + 1);
    r = r && consumeToken(b, I_EQ);
    r = r && datum(b, l + 1);
    exit_section_(b, m, I_REQ_PARAM, r);
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
  // namespaceDecl imports opOutputVarProjection opInputTrunkVarProjection reqOutputTrunkVarProjection
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    if (!nextTokenIs(b, I_NAMESPACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespaceDecl(b, l + 1);
    r = r && imports(b, l + 1);
    r = r && opOutputVarProjection(b, l + 1);
    r = r && opInputTrunkVarProjection(b, l + 1);
    r = r && reqOutputTrunkVarProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qid | '_'
  public static boolean tagName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tagName")) return false;
    if (!nextTokenIs(b, "<tag name>", I_UNDERSCORE, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_TAG_NAME, "<tag name>");
    r = qid(b, l + 1);
    if (!r) r = consumeToken(b, I_UNDERSCORE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  final static Parser dataValueRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return dataValueRecover(b, l + 1);
    }
  };
  final static Parser importRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return importRecover(b, l + 1);
    }
  };
  final static Parser namespaceDeclRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return namespaceDeclRecover(b, l + 1);
    }
  };
  final static Parser opInputModelPropertyRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return opInputModelPropertyRecover(b, l + 1);
    }
  };
  final static Parser opOutputKeyProjectionRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return opOutputKeyProjectionRecover(b, l + 1);
    }
  };
}
