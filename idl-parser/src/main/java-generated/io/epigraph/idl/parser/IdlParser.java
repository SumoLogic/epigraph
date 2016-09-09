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
    else if (t == I_DATA_ENUM) {
      r = dataEnum(b, 0);
    }
    else if (t == I_DATA_LIST) {
      r = dataList(b, 0);
    }
    else if (t == I_DATA_MAP) {
      r = dataMap(b, 0);
    }
    else if (t == I_DATA_MAP_ENTRY) {
      r = dataMapEntry(b, 0);
    }
    else if (t == I_DATA_PRIMITIVE) {
      r = dataPrimitive(b, 0);
    }
    else if (t == I_DATA_RECORD) {
      r = dataRecord(b, 0);
    }
    else if (t == I_DATA_RECORD_ENTRY) {
      r = dataRecordEntry(b, 0);
    }
    else if (t == I_DATA_VALUE) {
      r = dataValue(b, 0);
    }
    else if (t == I_DATA_VAR) {
      r = dataVar(b, 0);
    }
    else if (t == I_DATA_VAR_ENTRY) {
      r = dataVarEntry(b, 0);
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
    else if (t == I_NAMESPACE_DECL) {
      r = namespaceDecl(b, 0);
    }
    else if (t == I_OP_INPUT_MODEL_PROJECTION) {
      r = opInputModelProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_FIELD_PROJECTION) {
      r = opOutputFieldProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_FIELD_PROJECTION_BODY) {
      r = opOutputFieldProjectionBody(b, 0);
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
    else if (t == I_OP_OUTPUT_MODEL_PROJECTION) {
      r = opOutputModelProjection(b, 0);
    }
    else if (t == I_OP_OUTPUT_MODEL_PROJECTION_BODY) {
      r = opOutputModelProjectionBody(b, 0);
    }
    else if (t == I_OP_OUTPUT_MODEL_PROJECTION_BODY_PART) {
      r = opOutputModelProjectionBodyPart(b, 0);
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
    else if (t == I_OP_PARAM_PROJECTION) {
      r = opParamProjection(b, 0);
    }
    else if (t == I_OP_PARAMETERS) {
      r = opParameters(b, 0);
    }
    else if (t == I_QID) {
      r = qid(b, 0);
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
    create_token_set_(I_OP_OUTPUT_LIST_MODEL_PROJECTION, I_OP_OUTPUT_MAP_MODEL_PROJECTION, I_OP_OUTPUT_MODEL_PROJECTION, I_OP_OUTPUT_RECORD_MODEL_PROJECTION),
    create_token_set_(I_DATA_ENUM, I_DATA_LIST, I_DATA_MAP, I_DATA_RECORD,
      I_DATA_VALUE, I_DATA_VAR),
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
  // qid
  public static boolean dataEnum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataEnum")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, I_DATA_ENUM, r);
    return r;
  }

  /* ********************************************************** */
  // '[' (dataValue ','?)* ']'
  public static boolean dataList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataList")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_DATA_LIST, null);
    r = consumeToken(b, I_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, dataList_1(b, l + 1));
    r = p && consumeToken(b, I_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (dataValue ','?)*
  private static boolean dataList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataList_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!dataList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "dataList_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // dataValue ','?
  private static boolean dataList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dataValue(b, l + 1);
    r = r && dataList_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean dataList_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataList_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '(' dataMapEntry* ')'
  public static boolean dataMap(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataMap")) return false;
    if (!nextTokenIs(b, I_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_DATA_MAP, null);
    r = consumeToken(b, I_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, dataMap_1(b, l + 1));
    r = p && consumeToken(b, I_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // dataMapEntry*
  private static boolean dataMap_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataMap_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!dataMapEntry(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "dataMap_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // dataVarValue ':' dataValue ','?
  public static boolean dataMapEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataMapEntry")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_DATA_MAP_ENTRY, "<data map entry>");
    r = dataVarValue(b, l + 1);
    r = r && consumeToken(b, I_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataValue(b, l + 1));
    r = p && dataMapEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean dataMapEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataMapEntry_3")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // string | number | boolean
  public static boolean dataPrimitive(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataPrimitive")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_DATA_PRIMITIVE, "<data primitive>");
    r = consumeToken(b, I_STRING);
    if (!r) r = consumeToken(b, I_NUMBER);
    if (!r) r = consumeToken(b, I_BOOLEAN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{' dataRecordEntry* '}'
  public static boolean dataRecord(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataRecord")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_DATA_RECORD, null);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, dataRecord_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // dataRecordEntry*
  private static boolean dataRecord_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataRecord_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!dataRecordEntry(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "dataRecord_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // qid ':' dataValue ','?
  public static boolean dataRecordEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataRecordEntry")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_DATA_RECORD_ENTRY, "<data record entry>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, I_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataValue(b, l + 1));
    r = p && dataRecordEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean dataRecordEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataRecordEntry_3")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ( fqnTypeRef '/' )*
  static boolean dataTypeSpec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataTypeSpec")) return false;
    int c = current_position_(b);
    while (true) {
      if (!dataTypeSpec_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "dataTypeSpec", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // fqnTypeRef '/'
  private static boolean dataTypeSpec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataTypeSpec_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fqnTypeRef(b, l + 1);
    r = r && consumeToken(b, I_SLASH);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // dataTypeSpec? (dataVar | varValue)
  public static boolean dataValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, I_DATA_VALUE, "<data value>");
    r = dataValue_0(b, l + 1);
    r = r && dataValue_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // dataTypeSpec?
  private static boolean dataValue_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValue_0")) return false;
    dataTypeSpec(b, l + 1);
    return true;
  }

  // dataVar | varValue
  private static boolean dataValue_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValue_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dataVar(b, l + 1);
    if (!r) r = varValue(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ! ( qid | dataPrimitive | '}' | ')' | '>' | ']' | ',' )
  static boolean dataValueRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !dataValueRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // qid | dataPrimitive | '}' | ')' | '>' | ']' | ','
  private static boolean dataValueRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    if (!r) r = dataPrimitive(b, l + 1);
    if (!r) r = consumeToken(b, I_CURLY_RIGHT);
    if (!r) r = consumeToken(b, I_PAREN_RIGHT);
    if (!r) r = consumeToken(b, I_ANGLE_RIGHT);
    if (!r) r = consumeToken(b, I_BRACKET_RIGHT);
    if (!r) r = consumeToken(b, I_COMMA);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '<' dataVarEntry* '>'
  public static boolean dataVar(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataVar")) return false;
    if (!nextTokenIs(b, I_ANGLE_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_DATA_VAR, null);
    r = consumeToken(b, I_ANGLE_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, dataVar_1(b, l + 1));
    r = p && consumeToken(b, I_ANGLE_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // dataVarEntry*
  private static boolean dataVar_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataVar_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!dataVarEntry(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "dataVar_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // qid ':' dataVarValue ','?
  public static boolean dataVarEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataVarEntry")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_DATA_VAR_ENTRY, "<data var entry>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, I_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataVarValue(b, l + 1));
    r = p && dataVarEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean dataVarEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataVarEntry_3")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // dataTypeSpec? varValue
  static boolean dataVarValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataVarValue")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dataVarValue_0(b, l + 1);
    r = r && varValue(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // dataTypeSpec?
  private static boolean dataVarValue_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataVarValue_0")) return false;
    dataTypeSpec(b, l + 1);
    return true;
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
  // id
  public static boolean opInputModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelProjection")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_ID);
    exit_section_(b, m, I_OP_INPUT_MODEL_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // '+'? qid opOutputFieldProjectionBody? opOutputVarProjection?
  public static boolean opOutputFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjection")) return false;
    if (!nextTokenIs(b, "<op output field projection>", I_PLUS, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_FIELD_PROJECTION, "<op output field projection>");
    r = opOutputFieldProjection_0(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && opOutputFieldProjection_2(b, l + 1);
    r = r && opOutputFieldProjection_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opOutputFieldProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjection_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // opOutputFieldProjectionBody?
  private static boolean opOutputFieldProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjection_2")) return false;
    opOutputFieldProjectionBody(b, l + 1);
    return true;
  }

  // opOutputVarProjection?
  private static boolean opOutputFieldProjection_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjection_3")) return false;
    opOutputVarProjection(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (opOutputFieldProjectionBodyPart ','?)* '}'
  public static boolean opOutputFieldProjectionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjectionBody")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_FIELD_PROJECTION_BODY, null);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opOutputFieldProjectionBody_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opOutputFieldProjectionBodyPart ','?)*
  private static boolean opOutputFieldProjectionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjectionBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputFieldProjectionBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputFieldProjectionBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputFieldProjectionBodyPart ','?
  private static boolean opOutputFieldProjectionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjectionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputFieldProjectionBodyPart(b, l + 1);
    r = r && opOutputFieldProjectionBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputFieldProjectionBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjectionBody_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opParameters | customParam
  public static boolean opOutputFieldProjectionBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjectionBodyPart")) return false;
    if (!nextTokenIs(b, "<op output field projection body part>", I_PARAMETERS, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_FIELD_PROJECTION_BODY_PART, "<op output field projection body part>");
    r = opParameters(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' (opOutputKeyProjectionPart ','?)* ']'
  public static boolean opOutputKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_KEY_PROJECTION, null);
    r = consumeToken(b, I_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opOutputKeyProjection_1(b, l + 1));
    r = p && consumeToken(b, I_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opOutputKeyProjectionPart ','?)*
  private static boolean opOutputKeyProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputKeyProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputKeyProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputKeyProjectionPart ','?
  private static boolean opOutputKeyProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputKeyProjectionPart(b, l + 1);
    r = r && opOutputKeyProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputKeyProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // 'forbidden' | 'required' | opParameters | customParam
  public static boolean opOutputKeyProjectionPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_KEY_PROJECTION_PART, "<op output key projection part>");
    r = consumeToken(b, I_FORBIDDEN);
    if (!r) r = consumeToken(b, I_REQURIED);
    if (!r) r = opParameters(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '*' opOutputVarProjection
  public static boolean opOutputListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputListModelProjection")) return false;
    if (!nextTokenIs(b, I_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, I_STAR);
    p = r; // pin = 1
    r = r && opOutputVarProjection(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // opOutputKeyProjection '*' opOutputVarProjection
  public static boolean opOutputMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMapModelProjection")) return false;
    if (!nextTokenIs(b, I_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_MAP_MODEL_PROJECTION, null);
    r = opOutputKeyProjection(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, I_STAR));
    r = p && opOutputVarProjection(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '+'? ( opOutputRecordModelProjection
  //                                  | opOutputListModelProjection
  //                                  | opOutputMapModelProjection
  // //                               | opOutputEnumModelProjection
  // //                               | opOutputPrimitiveModelProjection
  //                                  )? opOutputModelProjectionBody?
  public static boolean opOutputModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, I_OP_OUTPUT_MODEL_PROJECTION, "<op output model projection>");
    r = opOutputModelProjection_0(b, l + 1);
    r = r && opOutputModelProjection_1(b, l + 1);
    r = r && opOutputModelProjection_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opOutputModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjection_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
  }

  // ( opOutputRecordModelProjection
  //                                  | opOutputListModelProjection
  //                                  | opOutputMapModelProjection
  // //                               | opOutputEnumModelProjection
  // //                               | opOutputPrimitiveModelProjection
  //                                  )?
  private static boolean opOutputModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjection_1")) return false;
    opOutputModelProjection_1_0(b, l + 1);
    return true;
  }

  // opOutputRecordModelProjection
  //                                  | opOutputListModelProjection
  //                                  | opOutputMapModelProjection
  private static boolean opOutputModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputRecordModelProjection(b, l + 1);
    if (!r) r = opOutputListModelProjection(b, l + 1);
    if (!r) r = opOutputMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opOutputModelProjectionBody?
  private static boolean opOutputModelProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjection_2")) return false;
    opOutputModelProjectionBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (opOutputModelProjectionBodyPart ','?)* '}'
  public static boolean opOutputModelProjectionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjectionBody")) return false;
    if (!nextTokenIs(b, I_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_MODEL_PROJECTION_BODY, null);
    r = consumeToken(b, I_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opOutputModelProjectionBody_1(b, l + 1));
    r = p && consumeToken(b, I_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opOutputModelProjectionBodyPart ','?)*
  private static boolean opOutputModelProjectionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjectionBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputModelProjectionBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputModelProjectionBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opOutputModelProjectionBodyPart ','?
  private static boolean opOutputModelProjectionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjectionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputModelProjectionBodyPart(b, l + 1);
    r = r && opOutputModelProjectionBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opOutputModelProjectionBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjectionBody_1_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opParameters | customParam
  public static boolean opOutputModelProjectionBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjectionBodyPart")) return false;
    if (!nextTokenIs(b, "<op output model projection body part>", I_PARAMETERS, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_MODEL_PROJECTION_BODY_PART, "<op output model projection body part>");
    r = opParameters(b, l + 1);
    if (!r) r = customParam(b, l + 1);
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
  // '+'? qid opOutputModelProjection
  public static boolean opOutputMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMultiTagProjectionItem")) return false;
    if (!nextTokenIs(b, "<op output multi tag projection item>", I_PLUS, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_MULTI_TAG_PROJECTION_ITEM, "<op output multi tag projection item>");
    r = opOutputMultiTagProjectionItem_0(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && opOutputModelProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opOutputMultiTagProjectionItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMultiTagProjectionItem_0")) return false;
    consumeToken(b, I_PLUS);
    return true;
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
  // ( ':' '+'? qid )? opOutputModelProjection
  public static boolean opOutputSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, I_OP_OUTPUT_SINGLE_TAG_PROJECTION, "<op output single tag projection>");
    r = opOutputSingleTagProjection_0(b, l + 1);
    r = r && opOutputModelProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ':' '+'? qid )?
  private static boolean opOutputSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection_0")) return false;
    opOutputSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ':' '+'? qid
  private static boolean opOutputSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_COLON);
    r = r && opOutputSingleTagProjection_0_0_1(b, l + 1);
    r = r && qid(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean opOutputSingleTagProjection_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputSingleTagProjection_0_0_1")) return false;
    consumeToken(b, I_PLUS);
    return true;
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
  // qid ':' opInputModelProjection
  public static boolean opParamProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParamProjection")) return false;
    if (!nextTokenIs(b, I_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && consumeToken(b, I_COLON);
    r = r && opInputModelProjection(b, l + 1);
    exit_section_(b, m, I_OP_PARAM_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // 'parameters' ':' '{' (opParamProjection ','?)* '}'
  public static boolean opParameters(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParameters")) return false;
    if (!nextTokenIs(b, I_PARAMETERS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, I_PARAMETERS);
    r = r && consumeToken(b, I_COLON);
    r = r && consumeToken(b, I_CURLY_LEFT);
    r = r && opParameters_3(b, l + 1);
    r = r && consumeToken(b, I_CURLY_RIGHT);
    exit_section_(b, m, I_OP_PARAMETERS, r);
    return r;
  }

  // (opParamProjection ','?)*
  private static boolean opParameters_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParameters_3")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opParameters_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opParameters_3", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opParamProjection ','?
  private static boolean opParameters_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParameters_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opParamProjection(b, l + 1);
    r = r && opParameters_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opParameters_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParameters_3_0_1")) return false;
    consumeToken(b, I_COMMA);
    return true;
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

  /* ********************************************************** */
  // dataRecord | dataMap | dataList | dataEnum | dataPrimitive | 'null'
  static boolean varValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varValue")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = dataRecord(b, l + 1);
    if (!r) r = dataMap(b, l + 1);
    if (!r) r = dataList(b, l + 1);
    if (!r) r = dataEnum(b, l + 1);
    if (!r) r = dataPrimitive(b, l + 1);
    if (!r) r = consumeToken(b, I_NULL);
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
