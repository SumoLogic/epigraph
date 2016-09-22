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
    if (t == I_CUSTOM_PARAM) {
      r = customParam(b, 0);
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
    else if (t == I_OP_INPUT_DEFAULT_VALUE) {
      r = opInputDefaultValue(b, 0);
    }
    else if (t == I_OP_INPUT_FIELD_PROJECTION) {
      r = opInputFieldProjection(b, 0);
    }
    else if (t == I_OP_INPUT_FIELD_PROJECTION_BODY_PART) {
      r = opInputFieldProjectionBodyPart(b, 0);
    }
    else if (t == I_OP_INPUT_KEY_PROJECTION) {
      r = opInputKeyProjection(b, 0);
    }
    else if (t == I_OP_INPUT_LIST_MODEL_PROJECTION) {
      r = opInputListModelProjection(b, 0);
    }
    else if (t == I_OP_INPUT_MAP_MODEL_PROJECTION) {
      r = opInputMapModelProjection(b, 0);
    }
    else if (t == I_OP_INPUT_MODEL_META) {
      r = opInputModelMeta(b, 0);
    }
    else if (t == I_OP_INPUT_MODEL_PROJECTION) {
      r = opInputModelProjection(b, 0);
    }
    else if (t == I_OP_INPUT_MODEL_PROPERTY) {
      r = opInputModelProperty(b, 0);
    }
    else if (t == I_OP_INPUT_MULTI_TAG_PROJECTION) {
      r = opInputMultiTagProjection(b, 0);
    }
    else if (t == I_OP_INPUT_MULTI_TAG_PROJECTION_ITEM) {
      r = opInputMultiTagProjectionItem(b, 0);
    }
    else if (t == I_OP_INPUT_RECORD_MODEL_PROJECTION) {
      r = opInputRecordModelProjection(b, 0);
    }
    else if (t == I_OP_INPUT_SINGLE_TAG_PROJECTION) {
      r = opInputSingleTagProjection(b, 0);
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
    else if (t == I_OP_INPUT_VAR_PROJECTION) {
      r = opInputVarProjection(b, 0);
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
    else if (t == I_OP_TAG_NAME) {
      r = opTagName(b, 0);
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
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(I_DATUM, I_ENUM_DATUM, I_LIST_DATUM, I_MAP_DATUM,
      I_NULL_DATUM, I_PRIMITIVE_DATUM, I_RECORD_DATUM),
  };

  /* ********************************************************** */
  // qid '=' dataValue
  public static boolean customParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customParam")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_CUSTOM_PARAM, "<custom attribute>");
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
  // ! ( qid | primitiveDatum | '}' | ')' | '>' | ']' | ',' )
  static boolean dataValueRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !dataValueRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // qid | primitiveDatum | '}' | ')' | '>' | ']' | ','
  private static boolean dataValueRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
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
  // qid
  public static boolean enumDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumDatum")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
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
  // customParam
  static boolean namespaceBodyPart(PsiBuilder b, int l) {
    return customParam(b, l + 1);
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
  // '{' (opInputFieldProjectionBodyPart ','? )* opInputVarProjection? '}'
  static boolean opInputComplexFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexFieldProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_CURLY_LEFT);
    r = r && opInputComplexFieldProjection_1(b, l + 1);
    r = r && opInputComplexFieldProjection_2(b, l + 1);
    r = r && consumeToken(b, I_CURLY_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (opInputFieldProjectionBodyPart ','? )*
  private static boolean opInputComplexFieldProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexFieldProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputComplexFieldProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputComplexFieldProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputFieldProjectionBodyPart ','?
  private static boolean opInputComplexFieldProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexFieldProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputFieldProjectionBodyPart(b, l + 1);
    r = r && opInputComplexFieldProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputComplexFieldProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexFieldProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // opInputVarProjection?
  private static boolean opInputComplexFieldProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexFieldProjection_2")) return false;
    opInputVarProjection(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (opInputModelProperty ','?)* opInputModelProjection '}'
  static boolean opInputComplexMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexMultiTagProjectionItem")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opInputComplexMultiTagProjectionItem_1(b, l + 1));
    r = p && report_error_(b, opInputModelProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opInputModelProperty ','?)*
  private static boolean opInputComplexMultiTagProjectionItem_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexMultiTagProjectionItem_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputComplexMultiTagProjectionItem_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputComplexMultiTagProjectionItem_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputModelProperty ','?
  private static boolean opInputComplexMultiTagProjectionItem_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexMultiTagProjectionItem_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputModelProperty(b, l + 1);
    r = r && opInputComplexMultiTagProjectionItem_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputComplexMultiTagProjectionItem_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexMultiTagProjectionItem_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '{' (opInputModelProperty ','?)* opInputModelProjection '}'
  static boolean opInputComplexSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexSingleTagProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opInputComplexSingleTagProjection_1(b, l + 1));
    r = p && report_error_(b, opInputModelProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opInputModelProperty ','?)*
  private static boolean opInputComplexSingleTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexSingleTagProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputComplexSingleTagProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputComplexSingleTagProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputModelProperty ','?
  private static boolean opInputComplexSingleTagProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexSingleTagProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputModelProperty(b, l + 1);
    r = r && opInputComplexSingleTagProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputComplexSingleTagProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexSingleTagProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
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
  // '+'? qid (opInputComplexFieldProjection | opInputSimpleFieldProjection)
  public static boolean opInputFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputFieldProjection")) return false;
    if (!nextTokenIs(b, "<op input field projection>", I_PLUS, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_FIELD_PROJECTION, "<op input field projection>");
    r = opInputFieldProjection_0(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && opInputFieldProjection_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opInputFieldProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputFieldProjection_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opInputComplexFieldProjection | opInputSimpleFieldProjection
  private static boolean opInputFieldProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputFieldProjection_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComplexFieldProjection(b, l + 1);
    if (!r) r = opInputSimpleFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // customParam
  public static boolean opInputFieldProjectionBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputFieldProjectionBodyPart")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = customParam(b, l + 1);
    exit_section_(b, m, I_OP_INPUT_FIELD_PROJECTION_BODY_PART, r);
    return r;
  }

  /* ********************************************************** */
  // '[' ']'
  public static boolean opInputKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputKeyProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_BRACKET_LEFT);
    r = r && consumeToken(b, I_BRACKET_RIGHT);
    exit_section_(b, m, I_OP_INPUT_KEY_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // '*' '(' opInputVarProjection ')'
  public static boolean opInputListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputListModelProjection")) return false;
    if (!nextTokenIs(b, I_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, I_STAR);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_PAREN_LEFT));
    r = p && report_error_(b, opInputVarProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // opInputKeyProjection '(' opInputVarProjection ')'
  public static boolean opInputMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputMapModelProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_MAP_MODEL_PROJECTION, null);
    r = opInputKeyProjection(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_PAREN_LEFT));
    r = p && report_error_(b, opInputVarProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'meta' ':' '+'? opInputModelProjection
  public static boolean opInputModelMeta(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelMeta")) return false;
    if (!nextTokenIs(b, I_META)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_META);
    r = r && consumeToken(b, I_COLON);
    r = r && opInputModelMeta_2(b, l + 1);
    r = r && opInputModelProjection(b, l + 1);
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
  // ( opInputRecordModelProjection
  //                            | opInputListModelProjection
  //                            | opInputMapModelProjection
  // //                         | opInputEnumModelProjection
  // //                         | opInputPrimitiveModelProjection
  //                            )?
  public static boolean opInputModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_MODEL_PROJECTION, "<op input model projection>");
    opInputModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // opInputRecordModelProjection
  //                            | opInputListModelProjection
  //                            | opInputMapModelProjection
  private static boolean opInputModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputRecordModelProjection(b, l + 1);
    if (!r) r = opInputListModelProjection(b, l + 1);
    if (!r) r = opInputMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // opInputDefaultValue | customParam | opInputModelMeta
  public static boolean opInputModelProperty(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelProperty")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_MODEL_PROPERTY, "<op input model property>");
    r = opInputDefaultValue(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    if (!r) r = opInputModelMeta(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ':' '(' (opInputMultiTagProjectionItem ','?)* ')'
  public static boolean opInputMultiTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputMultiTagProjection")) return false;
    if (!nextTokenIs(b, I_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_MULTI_TAG_PROJECTION, null);
    r = consumeToken(b, I_COLON);
    r = r && consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, opInputMultiTagProjection_2(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opInputMultiTagProjectionItem ','?)*
  private static boolean opInputMultiTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputMultiTagProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputMultiTagProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputMultiTagProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputMultiTagProjectionItem ','?
  private static boolean opInputMultiTagProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputMultiTagProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputMultiTagProjectionItem(b, l + 1);
    r = r && opInputMultiTagProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputMultiTagProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputMultiTagProjection_2_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? opTagName ( opInputComplexMultiTagProjectionItem | opInputSimpleMultiTagProjectionItem )
  public static boolean opInputMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputMultiTagProjectionItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_MULTI_TAG_PROJECTION_ITEM, "<op input multi tag projection item>");
    r = opInputMultiTagProjectionItem_0(b, l + 1);
    r = r && opTagName(b, l + 1);
    r = r && opInputMultiTagProjectionItem_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opInputMultiTagProjectionItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputMultiTagProjectionItem_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opInputComplexMultiTagProjectionItem | opInputSimpleMultiTagProjectionItem
  private static boolean opInputMultiTagProjectionItem_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputMultiTagProjectionItem_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComplexMultiTagProjectionItem(b, l + 1);
    if (!r) r = opInputSimpleMultiTagProjectionItem(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (opInputFieldProjection ','?)* ')'
  public static boolean opInputRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputRecordModelProjection")) return false;
    if (!nextTokenIs(b, I_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opInputRecordModelProjection_1(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opInputFieldProjection ','?)*
  private static boolean opInputRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputRecordModelProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputRecordModelProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputRecordModelProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputFieldProjection ','?
  private static boolean opInputRecordModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputRecordModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputFieldProjection(b, l + 1);
    r = r && opInputRecordModelProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputRecordModelProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputRecordModelProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opInputVarProjection
  static boolean opInputSimpleFieldProjection(PsiBuilder b, int l) {
    return opInputVarProjection(b, l + 1);
  }

  /* ********************************************************** */
  // opInputModelProjection
  static boolean opInputSimpleMultiTagProjectionItem(PsiBuilder b, int l) {
    return opInputModelProjection(b, l + 1);
  }

  /* ********************************************************** */
  // opInputModelProjection
  static boolean opInputSimpleSingleTagProjection(PsiBuilder b, int l) {
    return opInputModelProjection(b, l + 1);
  }

  /* ********************************************************** */
  // ( ':' '+'? opTagName)? (opInputComplexSingleTagProjection | opInputSimpleSingleTagProjection )
  public static boolean opInputSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_SINGLE_TAG_PROJECTION, "<op input single tag projection>");
    r = opInputSingleTagProjection_0(b, l + 1);
    r = r && opInputSingleTagProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' '+'? opTagName)?
  private static boolean opInputSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputSingleTagProjection_0")) return false;
    opInputSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' '+'? opTagName
  private static boolean opInputSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_COLON);
    r = r && opInputSingleTagProjection_0_0_1(b, l + 1);
    r = r && opTagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean opInputSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputSingleTagProjection_0_0_1")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opInputComplexSingleTagProjection | opInputSimpleSingleTagProjection
  private static boolean opInputSingleTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputSingleTagProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComplexSingleTagProjection(b, l + 1);
    if (!r) r = opInputSimpleSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
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
  // fqnTypeRef opInputVarProjection
  public static boolean opInputVarMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarMultiTailItem")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fqnTypeRef(b, l + 1);
    r = r && opInputVarProjection(b, l + 1);
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
  // ( opInputMultiTagProjection | opInputSingleTagProjection ) opInputVarPolymorphicTail?
  public static boolean opInputVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_VAR_PROJECTION, "<op input var projection>");
    r = opInputVarProjection_0(b, l + 1);
    r = r && opInputVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // opInputMultiTagProjection | opInputSingleTagProjection
  private static boolean opInputVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputMultiTagProjection(b, l + 1);
    if (!r) r = opInputSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opInputVarPolymorphicTail?
  private static boolean opInputVarProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarProjection_1")) return false;
    opInputVarPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '~' fqnTypeRef opInputVarProjection
  public static boolean opInputVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarSingleTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_TILDA);
    r = r && fqnTypeRef(b, l + 1);
    r = r && opInputVarProjection(b, l + 1);
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
  static boolean opOutputComplexMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexMultiTagProjectionItem")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opOutputComplexMultiTagProjectionItem_1(b, l + 1));
    r = p && report_error_(b, opOutputModelProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opOutputModelProperty ','?)*
  private static boolean opOutputComplexMultiTagProjectionItem_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexMultiTagProjectionItem_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputComplexMultiTagProjectionItem_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputComplexMultiTagProjectionItem_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputModelProperty ','?
  private static boolean opOutputComplexMultiTagProjectionItem_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexMultiTagProjectionItem_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputModelProperty(b, l + 1);
    r = r && opOutputComplexMultiTagProjectionItem_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputComplexMultiTagProjectionItem_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexMultiTagProjectionItem_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '{' (opOutputModelProperty ','?)* opOutputModelProjection '}'
  static boolean opOutputComplexSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexSingleTagProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opOutputComplexSingleTagProjection_1(b, l + 1));
    r = p && report_error_(b, opOutputModelProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opOutputModelProperty ','?)*
  private static boolean opOutputComplexSingleTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexSingleTagProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputComplexSingleTagProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputComplexSingleTagProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputModelProperty ','?
  private static boolean opOutputComplexSingleTagProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexSingleTagProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputModelProperty(b, l + 1);
    r = r && opOutputComplexSingleTagProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputComplexSingleTagProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputComplexSingleTagProjection_1_0_1")) return false;
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
  // opParam | customParam
  public static boolean opOutputFieldProjectionBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjectionBodyPart")) return false;
    if (!nextTokenIs(b, "<op output field projection body part>", I_SEMICOLON, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_FIELD_PROJECTION_BODY_PART, "<op output field projection body part>");
    r = opParam(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' ('required' ','?| 'forbidden' ','?)? (opOutputKeyProjectionPart ','?)* ']'
  public static boolean opOutputKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_KEY_PROJECTION, null);
    r = consumeToken(b, I_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opOutputKeyProjection_1(b, l + 1));
    r = p && report_error_(b, opOutputKeyProjection_2(b, l + 1)) && r;
    r = p && consumeToken(b, I_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ('required' ','?| 'forbidden' ','?)?
  private static boolean opOutputKeyProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_1")) return false;
    opOutputKeyProjection_1_0(b, l + 1);
    return true;
  }

  // 'required' ','?| 'forbidden' ','?
  private static boolean opOutputKeyProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputKeyProjection_1_0_0(b, l + 1);
    if (!r) r = opOutputKeyProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'required' ','?
  private static boolean opOutputKeyProjection_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_REQURIED);
    r = r && opOutputKeyProjection_1_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputKeyProjection_1_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_1_0_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // 'forbidden' ','?
  private static boolean opOutputKeyProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_FORBIDDEN);
    r = r && opOutputKeyProjection_1_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputKeyProjection_1_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_1_0_1_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // (opOutputKeyProjectionPart ','?)*
  private static boolean opOutputKeyProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputKeyProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputKeyProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputKeyProjectionPart ','?
  private static boolean opOutputKeyProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputKeyProjectionPart(b, l + 1);
    r = r && opOutputKeyProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputKeyProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_2_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opParam | customParam
  public static boolean opOutputKeyProjectionPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionPart")) return false;
    if (!nextTokenIs(b, "<op output key projection part>", I_SEMICOLON, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_KEY_PROJECTION_PART, "<op output key projection part>");
    r = opParam(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // opParam | customParam | opOutputModelMeta
  public static boolean opOutputModelProperty(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProperty")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_MODEL_PROPERTY, "<op output model property>");
    r = opParam(b, l + 1);
    if (!r) r = customParam(b, l + 1);
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
  // '+'? opTagName ( opOutputComplexMultiTagProjectionItem | opOutputSimpleMultiTagProjectionItem )
  public static boolean opOutputMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMultiTagProjectionItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM, "<op output multi tag projection item>");
    r = opOutputMultiTagProjectionItem_0(b, l + 1);
    r = r && opTagName(b, l + 1);
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

  // opOutputComplexMultiTagProjectionItem | opOutputSimpleMultiTagProjectionItem
  private static boolean opOutputMultiTagProjectionItem_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMultiTagProjectionItem_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputComplexMultiTagProjectionItem(b, l + 1);
    if (!r) r = opOutputSimpleMultiTagProjectionItem(b, l + 1);
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
  static boolean opOutputSimpleMultiTagProjectionItem(PsiBuilder b, int l) {
    return opOutputModelProjection(b, l + 1);
  }

  /* ********************************************************** */
  // opOutputModelProjection
  static boolean opOutputSimpleSingleTagProjection(PsiBuilder b, int l) {
    return opOutputModelProjection(b, l + 1);
  }

  /* ********************************************************** */
  // ( ':' '+'? opTagName)? (opOutputComplexSingleTagProjection | opOutputSimpleSingleTagProjection )
  public static boolean opOutputSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_SINGLE_TAG_PROJECTION, "<op output single tag projection>");
    r = opOutputSingleTagProjection_0(b, l + 1);
    r = r && opOutputSingleTagProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' '+'? opTagName)?
  private static boolean opOutputSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection_0")) return false;
    opOutputSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' '+'? opTagName
  private static boolean opOutputSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_COLON);
    r = r && opOutputSingleTagProjection_0_0_1(b, l + 1);
    r = r && opTagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean opOutputSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection_0_0_1")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opOutputComplexSingleTagProjection | opOutputSimpleSingleTagProjection
  private static boolean opOutputSingleTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputComplexSingleTagProjection(b, l + 1);
    if (!r) r = opOutputSimpleSingleTagProjection(b, l + 1);
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
  // ';' '+'? qid ':' fqnTypeRef opInputModelProjection opParamDefault? opParamBody?
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
    r = p && report_error_(b, opInputModelProjection(b, l + 1)) && r;
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
  // customParam
  static boolean opParamBodyPart(PsiBuilder b, int l) {
    return customParam(b, l + 1);
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
  // qid | '_'
  public static boolean opTagName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opTagName")) return false;
    if (!nextTokenIs(b, "<op tag name>", I_UNDERSCORE, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_TAG_NAME, "<op tag name>");
    r = qid(b, l + 1);
    if (!r) r = consumeToken(b, I_UNDERSCORE);
    exit_section_(b, l, m, r, false, null);
    return r;
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
  // namespaceDecl imports opOutputVarProjection
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    if (!nextTokenIs(b, I_NAMESPACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespaceDecl(b, l + 1);
    r = r && imports(b, l + 1);
    r = r && opOutputVarProjection(b, l + 1);
    exit_section_(b, m, null, r);
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
}
