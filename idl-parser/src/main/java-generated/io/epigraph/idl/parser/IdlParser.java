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
    else if (t == I_ANON_LIST) {
      r = anonList(b, 0);
    }
    else if (t == I_ANON_MAP) {
      r = anonMap(b, 0);
    }
    else if (t == I_CREATE_OPERATION_BODY_PART) {
      r = createOperationBodyPart(b, 0);
    }
    else if (t == I_CREATE_OPERATION_DEF) {
      r = createOperationDef(b, 0);
    }
    else if (t == I_CUSTOM_OPERATION_BODY_PART) {
      r = customOperationBodyPart(b, 0);
    }
    else if (t == I_CUSTOM_OPERATION_DEF) {
      r = customOperationDef(b, 0);
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
    else if (t == I_DEFAULT_OVERRIDE) {
      r = defaultOverride(b, 0);
    }
    else if (t == I_DELETE_OPERATION_BODY_PART) {
      r = deleteOperationBodyPart(b, 0);
    }
    else if (t == I_DELETE_OPERATION_DEF) {
      r = deleteOperationDef(b, 0);
    }
    else if (t == I_ENUM_DATUM) {
      r = enumDatum(b, 0);
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
    else if (t == I_OP_DELETE_FIELD_PROJECTION) {
      r = opDeleteFieldProjection(b, 0);
    }
    else if (t == I_OP_DELETE_FIELD_PROJECTION_BODY_PART) {
      r = opDeleteFieldProjectionBodyPart(b, 0);
    }
    else if (t == I_OP_DELETE_KEY_PROJECTION) {
      r = opDeleteKeyProjection(b, 0);
    }
    else if (t == I_OP_DELETE_KEY_PROJECTION_PART) {
      r = opDeleteKeyProjectionPart(b, 0);
    }
    else if (t == I_OP_DELETE_LIST_MODEL_PROJECTION) {
      r = opDeleteListModelProjection(b, 0);
    }
    else if (t == I_OP_DELETE_MAP_MODEL_PROJECTION) {
      r = opDeleteMapModelProjection(b, 0);
    }
    else if (t == I_OP_DELETE_MODEL_PROJECTION) {
      r = opDeleteModelProjection(b, 0);
    }
    else if (t == I_OP_DELETE_MODEL_PROPERTY) {
      r = opDeleteModelProperty(b, 0);
    }
    else if (t == I_OP_DELETE_MULTI_TAG_PROJECTION) {
      r = opDeleteMultiTagProjection(b, 0);
    }
    else if (t == I_OP_DELETE_MULTI_TAG_PROJECTION_ITEM) {
      r = opDeleteMultiTagProjectionItem(b, 0);
    }
    else if (t == I_OP_DELETE_RECORD_MODEL_PROJECTION) {
      r = opDeleteRecordModelProjection(b, 0);
    }
    else if (t == I_OP_DELETE_SINGLE_TAG_PROJECTION) {
      r = opDeleteSingleTagProjection(b, 0);
    }
    else if (t == I_OP_DELETE_VAR_MULTI_TAIL) {
      r = opDeleteVarMultiTail(b, 0);
    }
    else if (t == I_OP_DELETE_VAR_MULTI_TAIL_ITEM) {
      r = opDeleteVarMultiTailItem(b, 0);
    }
    else if (t == I_OP_DELETE_VAR_POLYMORPHIC_TAIL) {
      r = opDeleteVarPolymorphicTail(b, 0);
    }
    else if (t == I_OP_DELETE_VAR_PROJECTION) {
      r = opDeleteVarProjection(b, 0);
    }
    else if (t == I_OP_DELETE_VAR_SINGLE_TAIL) {
      r = opDeleteVarSingleTail(b, 0);
    }
    else if (t == I_OP_FIELD_PATH) {
      r = opFieldPath(b, 0);
    }
    else if (t == I_OP_FIELD_PATH_BODY_PART) {
      r = opFieldPathBodyPart(b, 0);
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
    else if (t == I_OP_MAP_MODEL_PATH) {
      r = opMapModelPath(b, 0);
    }
    else if (t == I_OP_MODEL_PATH) {
      r = opModelPath(b, 0);
    }
    else if (t == I_OP_MODEL_PATH_PROPERTY) {
      r = opModelPathProperty(b, 0);
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
    else if (t == I_OP_PATH_KEY_PROJECTION) {
      r = opPathKeyProjection(b, 0);
    }
    else if (t == I_OP_PATH_KEY_PROJECTION_PART) {
      r = opPathKeyProjectionPart(b, 0);
    }
    else if (t == I_OP_RECORD_MODEL_PATH) {
      r = opRecordModelPath(b, 0);
    }
    else if (t == I_OP_VAR_PATH) {
      r = opVarPath(b, 0);
    }
    else if (t == I_OPERATION_DEF) {
      r = operationDef(b, 0);
    }
    else if (t == I_OPERATION_DELETE_PROJECTION) {
      r = operationDeleteProjection(b, 0);
    }
    else if (t == I_OPERATION_INPUT_PROJECTION) {
      r = operationInputProjection(b, 0);
    }
    else if (t == I_OPERATION_INPUT_TYPE) {
      r = operationInputType(b, 0);
    }
    else if (t == I_OPERATION_NAME) {
      r = operationName(b, 0);
    }
    else if (t == I_OPERATION_OUTPUT_PROJECTION) {
      r = operationOutputProjection(b, 0);
    }
    else if (t == I_OPERATION_OUTPUT_TYPE) {
      r = operationOutputType(b, 0);
    }
    else if (t == I_OPERATION_PATH) {
      r = operationPath(b, 0);
    }
    else if (t == I_PRIMITIVE_DATUM) {
      r = primitiveDatum(b, 0);
    }
    else if (t == I_QID) {
      r = qid(b, 0);
    }
    else if (t == I_QN) {
      r = qn(b, 0);
    }
    else if (t == I_QN_SEGMENT) {
      r = qnSegment(b, 0);
    }
    else if (t == I_QN_TYPE_REF) {
      r = qnTypeRef(b, 0);
    }
    else if (t == I_READ_OPERATION_BODY_PART) {
      r = readOperationBodyPart(b, 0);
    }
    else if (t == I_READ_OPERATION_DEF) {
      r = readOperationDef(b, 0);
    }
    else if (t == I_RECORD_DATUM) {
      r = recordDatum(b, 0);
    }
    else if (t == I_RECORD_DATUM_ENTRY) {
      r = recordDatumEntry(b, 0);
    }
    else if (t == I_RESOURCE_DEF) {
      r = resourceDef(b, 0);
    }
    else if (t == I_RESOURCE_NAME) {
      r = resourceName(b, 0);
    }
    else if (t == I_RESOURCE_TYPE) {
      r = resourceType(b, 0);
    }
    else if (t == I_TAG_NAME) {
      r = tagName(b, 0);
    }
    else if (t == I_TYPE_REF) {
      r = typeRef(b, 0);
    }
    else if (t == I_UPDATE_OPERATION_BODY_PART) {
      r = updateOperationBodyPart(b, 0);
    }
    else if (t == I_UPDATE_OPERATION_DEF) {
      r = updateOperationDef(b, 0);
    }
    else if (t == I_VALUE_TYPE_REF) {
      r = valueTypeRef(b, 0);
    }
    else if (t == I_VAR_TAG_REF) {
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
    create_token_set_(I_ANON_LIST, I_ANON_MAP, I_QN_TYPE_REF, I_TYPE_REF),
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
  // 'list' '[' valueTypeRef ']'
  public static boolean anonList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonList")) return false;
    if (!nextTokenIs(b, I_LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_ANON_LIST, null);
    r = consumeToken(b, I_LIST);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_BRACKET_LEFT));
    r = p && report_error_(b, valueTypeRef(b, l + 1)) && r;
    r = p && consumeToken(b, I_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'map' '[' typeRef ',' valueTypeRef ']'
  public static boolean anonMap(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonMap")) return false;
    if (!nextTokenIs(b, I_MAP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_ANON_MAP, null);
    r = consumeToken(b, I_MAP);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_BRACKET_LEFT));
    r = p && report_error_(b, typeRef(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, I_COMMA)) && r;
    r = p && report_error_(b, valueTypeRef(b, l + 1)) && r;
    r = p && consumeToken(b, I_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' (createOperationBodyPart ','?)* '}'
  static boolean createOperationBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createOperationBody")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, createOperationBody_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (createOperationBodyPart ','?)*
  private static boolean createOperationBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createOperationBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!createOperationBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "createOperationBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // createOperationBodyPart ','?
  private static boolean createOperationBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createOperationBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = createOperationBodyPart(b, l + 1);
    r = r && createOperationBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean createOperationBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createOperationBody_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // operationPath |
  //                             operationInputType |
  //                             operationInputProjection |
  //                             operationOutputType |
  //                             operationOutputProjection |
  //                             opParam |
  //                             annotation
  public static boolean createOperationBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createOperationBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_CREATE_OPERATION_BODY_PART, "<create operation body part>");
    r = operationPath(b, l + 1);
    if (!r) r = operationInputType(b, l + 1);
    if (!r) r = operationInputProjection(b, l + 1);
    if (!r) r = operationOutputType(b, l + 1);
    if (!r) r = operationOutputProjection(b, l + 1);
    if (!r) r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, operationBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // operationName? 'CREATE' createOperationBody
  public static boolean createOperationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createOperationDef")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_CREATE_OPERATION_DEF, "<create operation def>");
    r = createOperationDef_0(b, l + 1);
    r = r && consumeToken(b, I_CREATE_OP);
    p = r; // pin = 2
    r = r && createOperationBody(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // operationName?
  private static boolean createOperationDef_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createOperationDef_0")) return false;
    operationName(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (customOperationBodyPart ','?)* '}'
  static boolean customOperationBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customOperationBody")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, customOperationBody_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (customOperationBodyPart ','?)*
  private static boolean customOperationBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customOperationBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!customOperationBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "customOperationBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // customOperationBodyPart ','?
  private static boolean customOperationBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customOperationBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = customOperationBodyPart(b, l + 1);
    r = r && customOperationBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean customOperationBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customOperationBody_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // operationInputProjection | operationOutputProjection | opParam | annotation
  public static boolean customOperationBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customOperationBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_CUSTOM_OPERATION_BODY_PART, "<custom operation body part>");
    r = operationInputProjection(b, l + 1);
    if (!r) r = operationOutputProjection(b, l + 1);
    if (!r) r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, operationBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // qid 'CUSTOM' customOperationBody
  public static boolean customOperationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customOperationDef")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_CUSTOM_OPERATION_DEF, null);
    r = qid(b, l + 1);
    r = r && consumeToken(b, I_CUSTOM_OP);
    p = r; // pin = 2
    r = r && customOperationBody(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // dataTypeSpec? '<' dataEntry* '>'
  public static boolean data(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "data")) return false;
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
  // typeRef
  static boolean dataTypeSpec(PsiBuilder b, int l) {
    return typeRef(b, l + 1);
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
  // 'default' varTagRef
  public static boolean defaultOverride(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defaultOverride")) return false;
    if (!nextTokenIs(b, I_DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_DEFAULT);
    r = r && varTagRef(b, l + 1);
    exit_section_(b, m, I_DEFAULT_OVERRIDE, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (deleteOperationBodyPart ','?)* '}'
  static boolean deleteOperationBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteOperationBody")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, deleteOperationBody_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (deleteOperationBodyPart ','?)*
  private static boolean deleteOperationBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteOperationBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!deleteOperationBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "deleteOperationBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // deleteOperationBodyPart ','?
  private static boolean deleteOperationBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteOperationBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = deleteOperationBodyPart(b, l + 1);
    r = r && deleteOperationBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean deleteOperationBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteOperationBody_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // operationPath |
  //                             operationDeleteProjection |
  //                             operationOutputType |
  //                             operationOutputProjection |
  //                             opParam |
  //                             annotation
  public static boolean deleteOperationBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteOperationBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_DELETE_OPERATION_BODY_PART, "<delete operation body part>");
    r = operationPath(b, l + 1);
    if (!r) r = operationDeleteProjection(b, l + 1);
    if (!r) r = operationOutputType(b, l + 1);
    if (!r) r = operationOutputProjection(b, l + 1);
    if (!r) r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, operationBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // operationName? 'DELETE' deleteOperationBody
  public static boolean deleteOperationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteOperationDef")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_DELETE_OPERATION_DEF, "<delete operation def>");
    r = deleteOperationDef_0(b, l + 1);
    r = r && consumeToken(b, I_DELETE_OP);
    p = r; // pin = 2
    r = r && deleteOperationBody(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // operationName?
  private static boolean deleteOperationDef_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteOperationDef_0")) return false;
    operationName(b, l + 1);
    return true;
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
  // namespaceDeclRecover
  static boolean importRecover(PsiBuilder b, int l) {
    return namespaceDeclRecover(b, l + 1);
  }

  /* ********************************************************** */
  // 'import' qn
  public static boolean importStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_IMPORT_STATEMENT, "<import statement>");
    r = consumeToken(b, I_IMPORT);
    p = r; // pin = 1
    r = r && qn(b, l + 1);
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
  // 'namespace' qn namespaceBody?
  public static boolean namespaceDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDecl")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_NAMESPACE_DECL, "<namespace decl>");
    r = consumeToken(b, I_NAMESPACE);
    p = r; // pin = 1
    r = r && report_error_(b, qn(b, l + 1));
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
  // ! ('import' | 'resource' )
  static boolean namespaceDeclRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDeclRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !namespaceDeclRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'import' | 'resource'
  private static boolean namespaceDeclRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDeclRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_IMPORT);
    if (!r) r = consumeToken(b, I_RESOURCE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (dataTypeSpec '@')? 'null'
  public static boolean nullDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nullDatum")) return false;
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
  // '{' (opFieldPathBodyPart ','? )* opVarPath? '}'
  static boolean opComplexFieldPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opComplexFieldPath")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_CURLY_LEFT);
    r = r && opComplexFieldPath_1(b, l + 1);
    r = r && opComplexFieldPath_2(b, l + 1);
    r = r && consumeToken(b, I_CURLY_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (opFieldPathBodyPart ','? )*
  private static boolean opComplexFieldPath_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opComplexFieldPath_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opComplexFieldPath_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opComplexFieldPath_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opFieldPathBodyPart ','?
  private static boolean opComplexFieldPath_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opComplexFieldPath_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opFieldPathBodyPart(b, l + 1);
    r = r && opComplexFieldPath_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opComplexFieldPath_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opComplexFieldPath_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // opVarPath?
  private static boolean opComplexFieldPath_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opComplexFieldPath_2")) return false;
    opVarPath(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (opModelPathProperty ','?)* opModelPath '}'
  static boolean opComplexTagPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opComplexTagPath")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opComplexTagPath_1(b, l + 1));
    r = p && report_error_(b, opModelPath(b, l + 1)) && r;
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opModelPathProperty ','?)*
  private static boolean opComplexTagPath_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opComplexTagPath_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opComplexTagPath_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opComplexTagPath_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opModelPathProperty ','?
  private static boolean opComplexTagPath_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opComplexTagPath_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opModelPathProperty(b, l + 1);
    r = r && opComplexTagPath_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opComplexTagPath_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opComplexTagPath_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '{' (opDeleteFieldProjectionBodyPart ','? )* opDeleteVarProjection? '}'
  static boolean opDeleteComplexFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteComplexFieldProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_CURLY_LEFT);
    r = r && opDeleteComplexFieldProjection_1(b, l + 1);
    r = r && opDeleteComplexFieldProjection_2(b, l + 1);
    r = r && consumeToken(b, I_CURLY_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (opDeleteFieldProjectionBodyPart ','? )*
  private static boolean opDeleteComplexFieldProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteComplexFieldProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opDeleteComplexFieldProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opDeleteComplexFieldProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opDeleteFieldProjectionBodyPart ','?
  private static boolean opDeleteComplexFieldProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteComplexFieldProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteFieldProjectionBodyPart(b, l + 1);
    r = r && opDeleteComplexFieldProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opDeleteComplexFieldProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteComplexFieldProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // opDeleteVarProjection?
  private static boolean opDeleteComplexFieldProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteComplexFieldProjection_2")) return false;
    opDeleteVarProjection(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (opDeleteModelProperty ','?)* opDeleteModelProjection '}'
  static boolean opDeleteComplexTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteComplexTagProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opDeleteComplexTagProjection_1(b, l + 1));
    r = p && report_error_(b, opDeleteModelProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opDeleteModelProperty ','?)*
  private static boolean opDeleteComplexTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteComplexTagProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opDeleteComplexTagProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opDeleteComplexTagProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opDeleteModelProperty ','?
  private static boolean opDeleteComplexTagProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteComplexTagProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteModelProperty(b, l + 1);
    r = r && opDeleteComplexTagProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opDeleteComplexTagProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteComplexTagProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // qid (opDeleteComplexFieldProjection | opDeleteSimpleFieldProjection)
  public static boolean opDeleteFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteFieldProjection")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && opDeleteFieldProjection_1(b, l + 1);
    exit_section_(b, m, I_OP_DELETE_FIELD_PROJECTION, r);
    return r;
  }

  // opDeleteComplexFieldProjection | opDeleteSimpleFieldProjection
  private static boolean opDeleteFieldProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteFieldProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteComplexFieldProjection(b, l + 1);
    if (!r) r = opDeleteSimpleFieldProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // opParam | annotation
  public static boolean opDeleteFieldProjectionBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteFieldProjectionBodyPart")) return false;
    if (!nextTokenIs(b, "<op delete field projection body part>", I_SEMICOLON, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_FIELD_PROJECTION_BODY_PART, "<op delete field projection body part>");
    r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' opDeleteKeyProjectionInt ']'
  public static boolean opDeleteKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_KEY_PROJECTION, null);
    r = consumeToken(b, I_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opDeleteKeyProjectionInt(b, l + 1));
    r = p && consumeToken(b, I_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ('required' ','?| 'forbidden' ','?)? (opDeleteKeyProjectionPart ','?)*
  static boolean opDeleteKeyProjectionInt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionInt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = opDeleteKeyProjectionInt_0(b, l + 1);
    r = r && opDeleteKeyProjectionInt_1(b, l + 1);
    exit_section_(b, l, m, r, false, opDeleteKeyProjectionRecover_parser_);
    return r;
  }

  // ('required' ','?| 'forbidden' ','?)?
  private static boolean opDeleteKeyProjectionInt_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionInt_0")) return false;
    opDeleteKeyProjectionInt_0_0(b, l + 1);
    return true;
  }

  // 'required' ','?| 'forbidden' ','?
  private static boolean opDeleteKeyProjectionInt_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionInt_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteKeyProjectionInt_0_0_0(b, l + 1);
    if (!r) r = opDeleteKeyProjectionInt_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'required' ','?
  private static boolean opDeleteKeyProjectionInt_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionInt_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_REQUIRED);
    r = r && opDeleteKeyProjectionInt_0_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opDeleteKeyProjectionInt_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionInt_0_0_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // 'forbidden' ','?
  private static boolean opDeleteKeyProjectionInt_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionInt_0_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_FORBIDDEN);
    r = r && opDeleteKeyProjectionInt_0_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opDeleteKeyProjectionInt_0_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionInt_0_0_1_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  // (opDeleteKeyProjectionPart ','?)*
  private static boolean opDeleteKeyProjectionInt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionInt_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opDeleteKeyProjectionInt_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opDeleteKeyProjectionInt_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opDeleteKeyProjectionPart ','?
  private static boolean opDeleteKeyProjectionInt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionInt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteKeyProjectionPart(b, l + 1);
    r = r && opDeleteKeyProjectionInt_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opDeleteKeyProjectionInt_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionInt_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opParam | annotation
  public static boolean opDeleteKeyProjectionPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionPart")) return false;
    if (!nextTokenIs(b, "<op delete key projection part>", I_SEMICOLON, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_KEY_PROJECTION_PART, "<op delete key projection part>");
    r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ! ( ']' )
  static boolean opDeleteKeyProjectionRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !opDeleteKeyProjectionRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ']' )
  private static boolean opDeleteKeyProjectionRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteKeyProjectionRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_BRACKET_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '*' '(' opDeleteVarProjection ')'
  public static boolean opDeleteListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteListModelProjection")) return false;
    if (!nextTokenIs(b, I_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, I_STAR);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_PAREN_LEFT));
    r = p && report_error_(b, opDeleteVarProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // opDeleteKeyProjection '(' opDeleteVarProjection ')'
  public static boolean opDeleteMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteMapModelProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_MAP_MODEL_PROJECTION, null);
    r = opDeleteKeyProjection(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_PAREN_LEFT));
    r = p && report_error_(b, opDeleteVarProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ( opDeleteRecordModelProjection
  //                             | opDeleteListModelProjection
  //                             | opDeleteMapModelProjection
  //                             )?
  public static boolean opDeleteModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_MODEL_PROJECTION, "<op delete model projection>");
    opDeleteModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // opDeleteRecordModelProjection
  //                             | opDeleteListModelProjection
  //                             | opDeleteMapModelProjection
  private static boolean opDeleteModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteRecordModelProjection(b, l + 1);
    if (!r) r = opDeleteListModelProjection(b, l + 1);
    if (!r) r = opDeleteMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // opParam | annotation
  public static boolean opDeleteModelProperty(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteModelProperty")) return false;
    if (!nextTokenIs(b, "<op delete model property>", I_SEMICOLON, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_MODEL_PROPERTY, "<op delete model property>");
    r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ':' '(' (opDeleteMultiTagProjectionItem ','?)* ')'
  public static boolean opDeleteMultiTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteMultiTagProjection")) return false;
    if (!nextTokenIs(b, I_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_MULTI_TAG_PROJECTION, null);
    r = consumeToken(b, I_COLON);
    r = r && consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, opDeleteMultiTagProjection_2(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opDeleteMultiTagProjectionItem ','?)*
  private static boolean opDeleteMultiTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteMultiTagProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opDeleteMultiTagProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opDeleteMultiTagProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opDeleteMultiTagProjectionItem ','?
  private static boolean opDeleteMultiTagProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteMultiTagProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteMultiTagProjectionItem(b, l + 1);
    r = r && opDeleteMultiTagProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opDeleteMultiTagProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteMultiTagProjection_2_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // tagName ( opDeleteComplexTagProjection | opDeleteSimpleTagProjection )
  public static boolean opDeleteMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteMultiTagProjectionItem")) return false;
    if (!nextTokenIs(b, "<op delete multi tag projection item>", I_UNDERSCORE, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_MULTI_TAG_PROJECTION_ITEM, "<op delete multi tag projection item>");
    r = tagName(b, l + 1);
    r = r && opDeleteMultiTagProjectionItem_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // opDeleteComplexTagProjection | opDeleteSimpleTagProjection
  private static boolean opDeleteMultiTagProjectionItem_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteMultiTagProjectionItem_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteComplexTagProjection(b, l + 1);
    if (!r) r = opDeleteSimpleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (opDeleteFieldProjection ','?)* ')'
  public static boolean opDeleteRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteRecordModelProjection")) return false;
    if (!nextTokenIs(b, I_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opDeleteRecordModelProjection_1(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opDeleteFieldProjection ','?)*
  private static boolean opDeleteRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteRecordModelProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opDeleteRecordModelProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opDeleteRecordModelProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opDeleteFieldProjection ','?
  private static boolean opDeleteRecordModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteRecordModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteFieldProjection(b, l + 1);
    r = r && opDeleteRecordModelProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opDeleteRecordModelProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteRecordModelProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opDeleteVarProjection
  static boolean opDeleteSimpleFieldProjection(PsiBuilder b, int l) {
    return opDeleteVarProjection(b, l + 1);
  }

  /* ********************************************************** */
  // opDeleteModelProjection
  static boolean opDeleteSimpleTagProjection(PsiBuilder b, int l) {
    return opDeleteModelProjection(b, l + 1);
  }

  /* ********************************************************** */
  // ( ':' tagName)? (opDeleteComplexTagProjection | opDeleteSimpleTagProjection )
  public static boolean opDeleteSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_SINGLE_TAG_PROJECTION, "<op delete single tag projection>");
    r = opDeleteSingleTagProjection_0(b, l + 1);
    r = r && opDeleteSingleTagProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' tagName)?
  private static boolean opDeleteSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteSingleTagProjection_0")) return false;
    opDeleteSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' tagName
  private static boolean opDeleteSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_COLON);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opDeleteComplexTagProjection | opDeleteSimpleTagProjection
  private static boolean opDeleteSingleTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteSingleTagProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteComplexTagProjection(b, l + 1);
    if (!r) r = opDeleteSimpleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '~' '(' (opDeleteVarMultiTailItem ','?)* ')'
  public static boolean opDeleteVarMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteVarMultiTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_VAR_MULTI_TAIL, null);
    r = consumeToken(b, I_TILDA);
    r = r && consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, opDeleteVarMultiTail_2(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opDeleteVarMultiTailItem ','?)*
  private static boolean opDeleteVarMultiTail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteVarMultiTail_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opDeleteVarMultiTail_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opDeleteVarMultiTail_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opDeleteVarMultiTailItem ','?
  private static boolean opDeleteVarMultiTail_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteVarMultiTail_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteVarMultiTailItem(b, l + 1);
    r = r && opDeleteVarMultiTail_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opDeleteVarMultiTail_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteVarMultiTail_2_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeRef opDeleteVarProjection
  public static boolean opDeleteVarMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteVarMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_VAR_MULTI_TAIL_ITEM, "<op delete var multi tail item>");
    r = typeRef(b, l + 1);
    r = r && opDeleteVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // opDeleteVarSingleTail | opDeleteVarMultiTail
  public static boolean opDeleteVarPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteVarPolymorphicTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteVarSingleTail(b, l + 1);
    if (!r) r = opDeleteVarMultiTail(b, l + 1);
    exit_section_(b, m, I_OP_DELETE_VAR_POLYMORPHIC_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // '+'? ( opDeleteMultiTagProjection | opDeleteSingleTagProjection ) opDeleteVarPolymorphicTail?
  public static boolean opDeleteVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteVarProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_DELETE_VAR_PROJECTION, "<op delete var projection>");
    r = opDeleteVarProjection_0(b, l + 1);
    r = r && opDeleteVarProjection_1(b, l + 1);
    r = r && opDeleteVarProjection_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opDeleteVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteVarProjection_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opDeleteMultiTagProjection | opDeleteSingleTagProjection
  private static boolean opDeleteVarProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteVarProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opDeleteMultiTagProjection(b, l + 1);
    if (!r) r = opDeleteSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opDeleteVarPolymorphicTail?
  private static boolean opDeleteVarProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteVarProjection_2")) return false;
    opDeleteVarPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '~' typeRef opDeleteVarProjection
  public static boolean opDeleteVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDeleteVarSingleTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_TILDA);
    r = r && typeRef(b, l + 1);
    r = r && opDeleteVarProjection(b, l + 1);
    exit_section_(b, m, I_OP_DELETE_VAR_SINGLE_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // qid (opComplexFieldPath | opSimpleFieldPath)
  public static boolean opFieldPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opFieldPath")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_FIELD_PATH, null);
    r = qid(b, l + 1);
    p = r; // pin = 1
    r = r && opFieldPath_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // opComplexFieldPath | opSimpleFieldPath
  private static boolean opFieldPath_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opFieldPath_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opComplexFieldPath(b, l + 1);
    if (!r) r = opSimpleFieldPath(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // opParam | annotation
  public static boolean opFieldPathBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opFieldPathBodyPart")) return false;
    if (!nextTokenIs(b, "<op field path body part>", I_SEMICOLON, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_FIELD_PATH_BODY_PART, "<op field path body part>");
    r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  static boolean opInputComplexTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexTagProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opInputComplexTagProjection_1(b, l + 1));
    r = p && report_error_(b, opInputModelProjection(b, l + 1)) && r;
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opInputModelProperty ','?)*
  private static boolean opInputComplexTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexTagProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opInputComplexTagProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opInputComplexTagProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opInputModelProperty ','?
  private static boolean opInputComplexTagProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexTagProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputModelProperty(b, l + 1);
    r = r && opInputComplexTagProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opInputComplexTagProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputComplexTagProjection_1_0_1")) return false;
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
  // '[' ']'
  public static boolean opInputKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputKeyProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_KEY_PROJECTION, null);
    r = consumeToken(b, I_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && consumeToken(b, I_BRACKET_RIGHT);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
    boolean r;
    Marker m = enter_section_(b);
    r = opInputKeyProjection(b, l + 1);
    r = r && consumeToken(b, I_PAREN_LEFT);
    r = r && opInputVarProjection(b, l + 1);
    r = r && consumeToken(b, I_PAREN_RIGHT);
    exit_section_(b, m, I_OP_INPUT_MAP_MODEL_PROJECTION, r);
    return r;
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
  //                                | opInputListModelProjection
  //                                | opInputMapModelProjection
  //                                )?
  public static boolean opInputModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelProjection")) return false;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_MODEL_PROJECTION, "<op input model projection>");
    opInputModelProjection_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // opInputRecordModelProjection
  //                                | opInputListModelProjection
  //                                | opInputMapModelProjection
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
  // '+'? tagName ( opInputComplexTagProjection | opInputSimpleTagProjection )
  public static boolean opInputMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputMultiTagProjectionItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_MULTI_TAG_PROJECTION_ITEM, "<op input multi tag projection item>");
    r = opInputMultiTagProjectionItem_0(b, l + 1);
    r = r && tagName(b, l + 1);
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

  // opInputComplexTagProjection | opInputSimpleTagProjection
  private static boolean opInputMultiTagProjectionItem_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputMultiTagProjectionItem_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComplexTagProjection(b, l + 1);
    if (!r) r = opInputSimpleTagProjection(b, l + 1);
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
  static boolean opInputSimpleTagProjection(PsiBuilder b, int l) {
    return opInputModelProjection(b, l + 1);
  }

  /* ********************************************************** */
  // ( ':' '+'? tagName)? (opInputComplexTagProjection | opInputSimpleTagProjection )
  public static boolean opInputSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_SINGLE_TAG_PROJECTION, "<op input single tag projection>");
    r = opInputSingleTagProjection_0(b, l + 1);
    r = r && opInputSingleTagProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' '+'? tagName)?
  private static boolean opInputSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputSingleTagProjection_0")) return false;
    opInputSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' '+'? tagName
  private static boolean opInputSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_COLON);
    r = r && opInputSingleTagProjection_0_0_1(b, l + 1);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean opInputSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputSingleTagProjection_0_0_1")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opInputComplexTagProjection | opInputSimpleTagProjection
  private static boolean opInputSingleTagProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputSingleTagProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opInputComplexTagProjection(b, l + 1);
    if (!r) r = opInputSimpleTagProjection(b, l + 1);
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
  // typeRef opInputVarProjection
  public static boolean opInputVarMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_INPUT_VAR_MULTI_TAIL_ITEM, "<op input var multi tail item>");
    r = typeRef(b, l + 1);
    r = r && opInputVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // '~' typeRef opInputVarProjection
  public static boolean opInputVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputVarSingleTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_TILDA);
    r = r && typeRef(b, l + 1);
    r = r && opInputVarProjection(b, l + 1);
    exit_section_(b, m, I_OP_INPUT_VAR_SINGLE_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // '/' '*' opPathKeyProjection? opVarPath
  public static boolean opMapModelPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMapModelPath")) return false;
    if (!nextTokenIs(b, I_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_SLASH);
    r = r && consumeToken(b, I_STAR);
    r = r && opMapModelPath_2(b, l + 1);
    r = r && opVarPath(b, l + 1);
    exit_section_(b, m, I_OP_MAP_MODEL_PATH, r);
    return r;
  }

  // opPathKeyProjection?
  private static boolean opMapModelPath_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMapModelPath_2")) return false;
    opPathKeyProjection(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ( opRecordModelPath
  //                 | opMapModelPath
  //                 )?
  public static boolean opModelPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelPath")) return false;
    Marker m = enter_section_(b, l, _NONE_, I_OP_MODEL_PATH, "<op model path>");
    opModelPath_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // opRecordModelPath
  //                 | opMapModelPath
  private static boolean opModelPath_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelPath_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opRecordModelPath(b, l + 1);
    if (!r) r = opMapModelPath(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // opParam | annotation
  public static boolean opModelPathProperty(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelPathProperty")) return false;
    if (!nextTokenIs(b, "<op model path property>", I_SEMICOLON, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_MODEL_PATH_PROPERTY, "<op model path property>");
    r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
    r = consumeToken(b, I_REQUIRED);
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
  // typeRef opOutputVarProjection
  public static boolean opOutputVarMultiTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarMultiTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_VAR_MULTI_TAIL_ITEM, "<op output var multi tail item>");
    r = typeRef(b, l + 1);
    r = r && opOutputVarProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // '~' typeRef opOutputVarProjection
  public static boolean opOutputVarSingleTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarSingleTail")) return false;
    if (!nextTokenIs(b, I_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_TILDA);
    r = r && typeRef(b, l + 1);
    r = r && opOutputVarProjection(b, l + 1);
    exit_section_(b, m, I_OP_OUTPUT_VAR_SINGLE_TAIL, r);
    return r;
  }

  /* ********************************************************** */
  // ';' '+'? qid ':' typeRef opInputModelProjection opParamDefault? opParamBody?
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
    r = p && report_error_(b, typeRef(b, l + 1)) && r;
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
  // '{' (opPathKeyProjectionPart ','?)*  '}'
  public static boolean opPathKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjection")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_PATH_KEY_PROJECTION, null);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opPathKeyProjection_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opPathKeyProjectionPart ','?)*
  private static boolean opPathKeyProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opPathKeyProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opPathKeyProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opPathKeyProjectionPart ','?
  private static boolean opPathKeyProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opPathKeyProjectionPart(b, l + 1);
    r = r && opPathKeyProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opPathKeyProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opParam | annotation
  public static boolean opPathKeyProjectionPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjectionPart")) return false;
    if (!nextTokenIs(b, "<op path key projection part>", I_SEMICOLON, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_PATH_KEY_PROJECTION_PART, "<op path key projection part>");
    r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '/' opFieldPath
  public static boolean opRecordModelPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opRecordModelPath")) return false;
    if (!nextTokenIs(b, I_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_SLASH);
    r = r && opFieldPath(b, l + 1);
    exit_section_(b, m, I_OP_RECORD_MODEL_PATH, r);
    return r;
  }

  /* ********************************************************** */
  // opVarPath
  static boolean opSimpleFieldPath(PsiBuilder b, int l) {
    return opVarPath(b, l + 1);
  }

  /* ********************************************************** */
  // opModelPath
  static boolean opSimpleTagPath(PsiBuilder b, int l) {
    return opModelPath(b, l + 1);
  }

  /* ********************************************************** */
  // ( ':' tagName)? (opComplexTagPath | opSimpleTagPath )
  public static boolean opVarPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opVarPath")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_VAR_PATH, "<op var path>");
    r = opVarPath_0(b, l + 1);
    r = r && opVarPath_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' tagName)?
  private static boolean opVarPath_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opVarPath_0")) return false;
    opVarPath_0_0(b, l + 1);
    return true;
  }

  // ':' tagName
  private static boolean opVarPath_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opVarPath_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_COLON);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opComplexTagPath | opSimpleTagPath
  private static boolean opVarPath_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opVarPath_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opComplexTagPath(b, l + 1);
    if (!r) r = opSimpleTagPath(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ! ( '}' | ',' | ';' | 'input' | 'output' | 'path' | (id '=') |
  //   (id? ('READ' | 'CREATE' | 'UPDATE' | 'DELETE') ) )
  static boolean operationBodyRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationBodyRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !operationBodyRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}' | ',' | ';' | 'input' | 'output' | 'path' | (id '=') |
  //   (id? ('READ' | 'CREATE' | 'UPDATE' | 'DELETE') )
  private static boolean operationBodyRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationBodyRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_CURLY_RIGHT);
    if (!r) r = consumeToken(b, I_COMMA);
    if (!r) r = consumeToken(b, I_SEMICOLON);
    if (!r) r = consumeToken(b, "input");
    if (!r) r = consumeToken(b, "output");
    if (!r) r = consumeToken(b, I_PATH);
    if (!r) r = operationBodyRecover_0_6(b, l + 1);
    if (!r) r = operationBodyRecover_0_7(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // id '='
  private static boolean operationBodyRecover_0_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationBodyRecover_0_6")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_ID);
    r = r && consumeToken(b, I_EQ);
    exit_section_(b, m, null, r);
    return r;
  }

  // id? ('READ' | 'CREATE' | 'UPDATE' | 'DELETE')
  private static boolean operationBodyRecover_0_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationBodyRecover_0_7")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = operationBodyRecover_0_7_0(b, l + 1);
    r = r && operationBodyRecover_0_7_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // id?
  private static boolean operationBodyRecover_0_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationBodyRecover_0_7_0")) return false;
    consumeToken(b, I_ID);
    return true;
  }

  // 'READ' | 'CREATE' | 'UPDATE' | 'DELETE'
  private static boolean operationBodyRecover_0_7_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationBodyRecover_0_7_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_READ);
    if (!r) r = consumeToken(b, I_CREATE_OP);
    if (!r) r = consumeToken(b, I_UPDATE_OP);
    if (!r) r = consumeToken(b, I_DELETE_OP);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // createOperationDef | readOperationDef | updateOperationDef | deleteOperationDef | customOperationDef
  public static boolean operationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OPERATION_DEF, "<operation def>");
    r = createOperationDef(b, l + 1);
    if (!r) r = readOperationDef(b, l + 1);
    if (!r) r = updateOperationDef(b, l + 1);
    if (!r) r = deleteOperationDef(b, l + 1);
    if (!r) r = customOperationDef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'deleteProjection' opDeleteVarProjection
  public static boolean operationDeleteProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationDeleteProjection")) return false;
    if (!nextTokenIs(b, I_DELETE_PROJECTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_DELETE_PROJECTION);
    r = r && opDeleteVarProjection(b, l + 1);
    exit_section_(b, m, I_OPERATION_DELETE_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // 'inputProjection' opInputModelProjection
  public static boolean operationInputProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationInputProjection")) return false;
    if (!nextTokenIs(b, I_INPUT_PROJECTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_INPUT_PROJECTION);
    r = r && opInputModelProjection(b, l + 1);
    exit_section_(b, m, I_OPERATION_INPUT_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // 'inputType' typeRef
  public static boolean operationInputType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationInputType")) return false;
    if (!nextTokenIs(b, I_INPUT_TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_INPUT_TYPE);
    r = r && typeRef(b, l + 1);
    exit_section_(b, m, I_OPERATION_INPUT_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // 'default' | qid
  public static boolean operationName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationName")) return false;
    if (!nextTokenIs(b, "<operation name>", I_DEFAULT, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OPERATION_NAME, "<operation name>");
    r = consumeToken(b, I_DEFAULT);
    if (!r) r = qid(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'outputProjection' opOutputVarProjection
  public static boolean operationOutputProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationOutputProjection")) return false;
    if (!nextTokenIs(b, I_OUTPUT_PROJECTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_OUTPUT_PROJECTION);
    r = r && opOutputVarProjection(b, l + 1);
    exit_section_(b, m, I_OPERATION_OUTPUT_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // 'outputType' valueTypeRef
  public static boolean operationOutputType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationOutputType")) return false;
    if (!nextTokenIs(b, I_OUTPUT_TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_OUTPUT_TYPE);
    r = r && valueTypeRef(b, l + 1);
    exit_section_(b, m, I_OPERATION_OUTPUT_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // 'path' opVarPath
  public static boolean operationPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationPath")) return false;
    if (!nextTokenIs(b, I_PATH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OPERATION_PATH, null);
    r = consumeToken(b, I_PATH);
    p = r; // pin = 1
    r = r && opVarPath(b, l + 1);
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
  // qnSegment ('.' qnSegment)*
  public static boolean qn(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qn")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qnSegment(b, l + 1);
    r = r && qn_1(b, l + 1);
    exit_section_(b, m, I_QN, r);
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
    r = consumeToken(b, I_DOT);
    r = r && qnSegment(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qid {
  // //  implements="com.intellij.psi.PsiNameIdentifierOwner"
  // //  methods=[getName setName getNameIdentifier getSchemaFqn getSchemaFqnTypeRef isLast getReference getFqn]
  // }
  public static boolean qnSegment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qnSegment")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && qnSegment_1(b, l + 1);
    exit_section_(b, m, I_QN_SEGMENT, r);
    return r;
  }

  // {
  // //  implements="com.intellij.psi.PsiNameIdentifierOwner"
  // //  methods=[getName setName getNameIdentifier getSchemaFqn getSchemaFqnTypeRef isLast getReference getFqn]
  // }
  private static boolean qnSegment_1(PsiBuilder b, int l) {
    return true;
  }

  /* ********************************************************** */
  // qn
  public static boolean qnTypeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qnTypeRef")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qn(b, l + 1);
    exit_section_(b, m, I_QN_TYPE_REF, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (readOperationBodyPart ','?)* '}'
  static boolean readOperationBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOperationBody")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, readOperationBody_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (readOperationBodyPart ','?)*
  private static boolean readOperationBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOperationBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!readOperationBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "readOperationBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // readOperationBodyPart ','?
  private static boolean readOperationBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOperationBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = readOperationBodyPart(b, l + 1);
    r = r && readOperationBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean readOperationBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOperationBody_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // operationPath |
  //                           operationOutputProjection |
  //                           opParam |
  //                           annotation
  public static boolean readOperationBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOperationBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_READ_OPERATION_BODY_PART, "<read operation body part>");
    r = operationPath(b, l + 1);
    if (!r) r = operationOutputProjection(b, l + 1);
    if (!r) r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, operationBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // operationName? 'READ' readOperationBody
  public static boolean readOperationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOperationDef")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_READ_OPERATION_DEF, "<read operation def>");
    r = readOperationDef_0(b, l + 1);
    r = r && consumeToken(b, I_READ);
    p = r; // pin = 2
    r = r && readOperationBody(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // operationName?
  private static boolean readOperationDef_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOperationDef_0")) return false;
    operationName(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // dataTypeSpec? '{' recordDatumEntry* '}'
  public static boolean recordDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordDatum")) return false;
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
  // 'resource' resourceName resourceType resourceDefBody
  public static boolean resourceDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDef")) return false;
    if (!nextTokenIs(b, I_RESOURCE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_RESOURCE);
    r = r && resourceName(b, l + 1);
    r = r && resourceType(b, l + 1);
    r = r && resourceDefBody(b, l + 1);
    exit_section_(b, m, I_RESOURCE_DEF, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (resourceDefPart ','?)* '}'
  static boolean resourceDefBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBody")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, resourceDefBody_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (resourceDefPart ','?)*
  private static boolean resourceDefBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!resourceDefBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "resourceDefBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // resourceDefPart ','?
  private static boolean resourceDefBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = resourceDefPart(b, l + 1);
    r = r && resourceDefBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean resourceDefBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBody_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // operationDef
  static boolean resourceDefPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = operationDef(b, l + 1);
    exit_section_(b, l, m, r, false, operationBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // (resourceDef ','?)*
  static boolean resourceDefs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefs")) return false;
    int c = current_position_(b);
    while (true) {
      if (!resourceDefs_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "resourceDefs", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // resourceDef ','?
  private static boolean resourceDefs_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefs_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = resourceDef(b, l + 1);
    r = r && resourceDefs_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean resourceDefs_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefs_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // qid
  public static boolean resourceName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceName")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, I_RESOURCE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // ':' valueTypeRef
  public static boolean resourceType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceType")) return false;
    if (!nextTokenIs(b, I_COLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_COLON);
    r = r && valueTypeRef(b, l + 1);
    exit_section_(b, m, I_RESOURCE_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // namespaceDecl imports resourceDefs
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    if (!nextTokenIs(b, I_NAMESPACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespaceDecl(b, l + 1);
    r = r && imports(b, l + 1);
    r = r && resourceDefs(b, l + 1);
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

  /* ********************************************************** */
  // qnTypeRef | anonList | anonMap
  public static boolean typeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, I_TYPE_REF, "<type>");
    r = qnTypeRef(b, l + 1);
    if (!r) r = anonList(b, l + 1);
    if (!r) r = anonMap(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{' (updateOperationBodyPart ','?)* '}'
  static boolean updateOperationBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateOperationBody")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, updateOperationBody_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (updateOperationBodyPart ','?)*
  private static boolean updateOperationBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateOperationBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!updateOperationBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "updateOperationBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // updateOperationBodyPart ','?
  private static boolean updateOperationBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateOperationBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = updateOperationBodyPart(b, l + 1);
    r = r && updateOperationBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean updateOperationBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateOperationBody_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // operationPath |
  //                             operationInputType |
  //                             operationInputProjection |
  //                             operationOutputType |
  //                             operationOutputProjection | 
  //                             opParam | 
  //                             annotation
  public static boolean updateOperationBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateOperationBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_UPDATE_OPERATION_BODY_PART, "<update operation body part>");
    r = operationPath(b, l + 1);
    if (!r) r = operationInputType(b, l + 1);
    if (!r) r = operationInputProjection(b, l + 1);
    if (!r) r = operationOutputType(b, l + 1);
    if (!r) r = operationOutputProjection(b, l + 1);
    if (!r) r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, operationBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // operationName? 'UPDATE' updateOperationBody
  public static boolean updateOperationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateOperationDef")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_UPDATE_OPERATION_DEF, "<update operation def>");
    r = updateOperationDef_0(b, l + 1);
    r = r && consumeToken(b, I_UPDATE_OP);
    p = r; // pin = 2
    r = r && updateOperationBody(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // operationName?
  private static boolean updateOperationDef_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateOperationDef_0")) return false;
    operationName(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // typeRef defaultOverride?
  public static boolean valueTypeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueTypeRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_VALUE_TYPE_REF, "<value type ref>");
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
  // qid {
  // //  implements="com.intellij.psi.PsiNameIdentifierOwner"
  // //  methods=[setName getNameIdentifier getReference]
  // }
  public static boolean varTagRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTagRef")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && varTagRef_1(b, l + 1);
    exit_section_(b, m, I_VAR_TAG_REF, r);
    return r;
  }

  // {
  // //  implements="com.intellij.psi.PsiNameIdentifierOwner"
  // //  methods=[setName getNameIdentifier getReference]
  // }
  private static boolean varTagRef_1(PsiBuilder b, int l) {
    return true;
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
  final static Parser opDeleteKeyProjectionRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return opDeleteKeyProjectionRecover(b, l + 1);
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
  final static Parser operationBodyRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return operationBodyRecover(b, l + 1);
    }
  };
}
