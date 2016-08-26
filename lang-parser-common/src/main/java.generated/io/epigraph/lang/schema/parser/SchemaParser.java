// This is a generated file. Not intended for manual editing.
package io.epigraph.lang.schema.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class SchemaParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == E_ANON_LIST) {
      r = anonList(b, 0);
    }
    else if (t == E_ANON_MAP) {
      r = anonMap(b, 0);
    }
    else if (t == E_CUSTOM_PARAM) {
      r = customParam(b, 0);
    }
    else if (t == E_DATA_ENUM) {
      r = dataEnum(b, 0);
    }
    else if (t == E_DATA_LIST) {
      r = dataList(b, 0);
    }
    else if (t == E_DATA_MAP) {
      r = dataMap(b, 0);
    }
    else if (t == E_DATA_MAP_ENTRY) {
      r = dataMapEntry(b, 0);
    }
    else if (t == E_DATA_PRIMITIVE) {
      r = dataPrimitive(b, 0);
    }
    else if (t == E_DATA_RECORD) {
      r = dataRecord(b, 0);
    }
    else if (t == E_DATA_RECORD_ENTRY) {
      r = dataRecordEntry(b, 0);
    }
    else if (t == E_DATA_VALUE) {
      r = dataValue(b, 0);
    }
    else if (t == E_DATA_VAR) {
      r = dataVar(b, 0);
    }
    else if (t == E_DATA_VAR_ENTRY) {
      r = dataVarEntry(b, 0);
    }
    else if (t == E_DEFAULT_OVERRIDE) {
      r = defaultOverride(b, 0);
    }
    else if (t == E_DEFS) {
      r = defs(b, 0);
    }
    else if (t == E_ENUM_MEMBER_DECL) {
      r = enumMemberDecl(b, 0);
    }
    else if (t == E_ENUM_TYPE_BODY) {
      r = enumTypeBody(b, 0);
    }
    else if (t == E_ENUM_TYPE_DEF) {
      r = enumTypeDef(b, 0);
    }
    else if (t == E_EXTENDS_DECL) {
      r = extendsDecl(b, 0);
    }
    else if (t == E_FIELD_DECL) {
      r = fieldDecl(b, 0);
    }
    else if (t == E_FQN) {
      r = fqn(b, 0);
    }
    else if (t == E_FQN_SEGMENT) {
      r = fqnSegment(b, 0);
    }
    else if (t == E_FQN_TYPE_REF) {
      r = fqnTypeRef(b, 0);
    }
    else if (t == E_IMPORT_STATEMENT) {
      r = importStatement(b, 0);
    }
    else if (t == E_IMPORTS) {
      r = imports(b, 0);
    }
    else if (t == E_LIST_TYPE_BODY) {
      r = listTypeBody(b, 0);
    }
    else if (t == E_LIST_TYPE_DEF) {
      r = listTypeDef(b, 0);
    }
    else if (t == E_MAP_TYPE_BODY) {
      r = mapTypeBody(b, 0);
    }
    else if (t == E_MAP_TYPE_DEF) {
      r = mapTypeDef(b, 0);
    }
    else if (t == E_META_DECL) {
      r = metaDecl(b, 0);
    }
    else if (t == E_NAMESPACE_DECL) {
      r = namespaceDecl(b, 0);
    }
    else if (t == E_OP_INPUT_MODEL_PROJECTION) {
      r = opInputModelProjection(b, 0);
    }
    else if (t == E_OP_OUTPUT_ENUM_MODEL_PROJECTION) {
      r = opOutputEnumModelProjection(b, 0);
    }
    else if (t == E_OP_OUTPUT_FIELD_PROJECTION) {
      r = opOutputFieldProjection(b, 0);
    }
    else if (t == E_OP_OUTPUT_FIELD_PROJECTION_BODY) {
      r = opOutputFieldProjectionBody(b, 0);
    }
    else if (t == E_OP_OUTPUT_FIELD_PROJECTION_BODY_PART) {
      r = opOutputFieldProjectionBodyPart(b, 0);
    }
    else if (t == E_OP_OUTPUT_KEY_PROJECTION) {
      r = opOutputKeyProjection(b, 0);
    }
    else if (t == E_OP_OUTPUT_KEY_PROJECTION_PART) {
      r = opOutputKeyProjectionPart(b, 0);
    }
    else if (t == E_OP_OUTPUT_LIST_MODEL_PROJECTION) {
      r = opOutputListModelProjection(b, 0);
    }
    else if (t == E_OP_OUTPUT_LIST_POLY_BRANCH) {
      r = opOutputListPolyBranch(b, 0);
    }
    else if (t == E_OP_OUTPUT_MAP_MODEL_PROJECTION) {
      r = opOutputMapModelProjection(b, 0);
    }
    else if (t == E_OP_OUTPUT_MAP_POLY_BRANCH) {
      r = opOutputMapPolyBranch(b, 0);
    }
    else if (t == E_OP_OUTPUT_MODEL_PROJECTION) {
      r = opOutputModelProjection(b, 0);
    }
    else if (t == E_OP_OUTPUT_MODEL_PROJECTION_BODY) {
      r = opOutputModelProjectionBody(b, 0);
    }
    else if (t == E_OP_OUTPUT_MODEL_PROJECTION_BODY_PART) {
      r = opOutputModelProjectionBodyPart(b, 0);
    }
    else if (t == E_OP_OUTPUT_PRIMITIVE_MODEL_PROJECTION) {
      r = opOutputPrimitiveModelProjection(b, 0);
    }
    else if (t == E_OP_OUTPUT_RECORD_MODEL_PROJECTION) {
      r = opOutputRecordModelProjection(b, 0);
    }
    else if (t == E_OP_OUTPUT_RECORD_POLY_BRANCH) {
      r = opOutputRecordPolyBranch(b, 0);
    }
    else if (t == E_OP_OUTPUT_TAG_PROJECTION) {
      r = opOutputTagProjection(b, 0);
    }
    else if (t == E_OP_OUTPUT_VAR_PROJECTION) {
      r = opOutputVarProjection(b, 0);
    }
    else if (t == E_OP_PARAM_PROJECTION) {
      r = opParamProjection(b, 0);
    }
    else if (t == E_OP_PARAMETERS) {
      r = opParameters(b, 0);
    }
    else if (t == E_PRIMITIVE_TYPE_BODY) {
      r = primitiveTypeBody(b, 0);
    }
    else if (t == E_PRIMITIVE_TYPE_DEF) {
      r = primitiveTypeDef(b, 0);
    }
    else if (t == E_QID) {
      r = qid(b, 0);
    }
    else if (t == E_RECORD_TYPE_BODY) {
      r = recordTypeBody(b, 0);
    }
    else if (t == E_RECORD_TYPE_DEF) {
      r = recordTypeDef(b, 0);
    }
    else if (t == E_SUPPLEMENT_DEF) {
      r = supplementDef(b, 0);
    }
    else if (t == E_SUPPLEMENTS_DECL) {
      r = supplementsDecl(b, 0);
    }
    else if (t == E_TYPE_DEF_WRAPPER) {
      r = typeDefWrapper(b, 0);
    }
    else if (t == E_TYPE_REF) {
      r = typeRef(b, 0);
    }
    else if (t == E_VALUE_TYPE_REF) {
      r = valueTypeRef(b, 0);
    }
    else if (t == E_VAR_TAG_DECL) {
      r = varTagDecl(b, 0);
    }
    else if (t == E_VAR_TAG_REF) {
      r = varTagRef(b, 0);
    }
    else if (t == E_VAR_TYPE_BODY) {
      r = varTypeBody(b, 0);
    }
    else if (t == E_VAR_TYPE_DEF) {
      r = varTypeDef(b, 0);
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
    create_token_set_(E_ANON_LIST, E_ANON_MAP, E_FQN_TYPE_REF, E_TYPE_REF),
    create_token_set_(E_DATA_ENUM, E_DATA_LIST, E_DATA_MAP, E_DATA_RECORD,
      E_DATA_VALUE, E_DATA_VAR),
  };

  /* ********************************************************** */
  // 'list' '[' valueTypeRef ']'
  public static boolean anonList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonList")) return false;
    if (!nextTokenIs(b, E_LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_ANON_LIST, null);
    r = consumeToken(b, E_LIST);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, E_BRACKET_LEFT));
    r = p && report_error_(b, valueTypeRef(b, l + 1)) && r;
    r = p && consumeToken(b, E_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'map' '[' typeRef ',' valueTypeRef ']'
  public static boolean anonMap(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonMap")) return false;
    if (!nextTokenIs(b, E_MAP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_ANON_MAP, null);
    r = consumeToken(b, E_MAP);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, E_BRACKET_LEFT));
    r = p && report_error_(b, typeRef(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, E_COMMA)) && r;
    r = p && report_error_(b, valueTypeRef(b, l + 1)) && r;
    r = p && consumeToken(b, E_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // qid '=' dataValue
  public static boolean customParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customParam")) return false;
    if (!nextTokenIs(b, E_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_CUSTOM_PARAM, "<custom attribute>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, E_EQ);
    p = r; // pin = 2
    r = r && dataValue(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // qid
  public static boolean dataEnum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataEnum")) return false;
    if (!nextTokenIs(b, E_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, E_DATA_ENUM, r);
    return r;
  }

  /* ********************************************************** */
  // '[' (dataValue ','?)* ']'
  public static boolean dataList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataList")) return false;
    if (!nextTokenIs(b, E_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_DATA_LIST, null);
    r = consumeToken(b, E_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, dataList_1(b, l + 1));
    r = p && consumeToken(b, E_BRACKET_RIGHT) && r;
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
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '(' dataMapEntry* ')'
  public static boolean dataMap(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataMap")) return false;
    if (!nextTokenIs(b, E_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_DATA_MAP, null);
    r = consumeToken(b, E_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, dataMap_1(b, l + 1));
    r = p && consumeToken(b, E_PAREN_RIGHT) && r;
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
    Marker m = enter_section_(b, l, _NONE_, E_DATA_MAP_ENTRY, "<data map entry>");
    r = dataVarValue(b, l + 1);
    r = r && consumeToken(b, E_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataValue(b, l + 1));
    r = p && dataMapEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean dataMapEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataMapEntry_3")) return false;
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // string | number | boolean
  public static boolean dataPrimitive(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataPrimitive")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_DATA_PRIMITIVE, "<data primitive>");
    r = consumeToken(b, E_STRING);
    if (!r) r = consumeToken(b, E_NUMBER);
    if (!r) r = consumeToken(b, E_BOOLEAN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{' dataRecordEntry* '}'
  public static boolean dataRecord(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataRecord")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_DATA_RECORD, null);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, dataRecord_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
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
    Marker m = enter_section_(b, l, _NONE_, E_DATA_RECORD_ENTRY, "<data record entry>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, E_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataValue(b, l + 1));
    r = p && dataRecordEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean dataRecordEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataRecordEntry_3")) return false;
    consumeToken(b, E_COMMA);
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
    r = r && consumeToken(b, E_SLASH);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // dataTypeSpec? (dataVar | varValue)
  public static boolean dataValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, E_DATA_VALUE, "<data value>");
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
  // ! ( qid | dataPrimitive | '}' | ')' | '>' | ']' | 'abstract' | 'override' | ',' )
  static boolean dataValueRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !dataValueRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // qid | dataPrimitive | '}' | ')' | '>' | ']' | 'abstract' | 'override' | ','
  private static boolean dataValueRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    if (!r) r = dataPrimitive(b, l + 1);
    if (!r) r = consumeToken(b, E_CURLY_RIGHT);
    if (!r) r = consumeToken(b, E_PAREN_RIGHT);
    if (!r) r = consumeToken(b, E_ANGLE_RIGHT);
    if (!r) r = consumeToken(b, E_BRACKET_RIGHT);
    if (!r) r = consumeToken(b, E_ABSTRACT);
    if (!r) r = consumeToken(b, E_OVERRIDE);
    if (!r) r = consumeToken(b, E_COMMA);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '<' dataVarEntry* '>'
  public static boolean dataVar(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataVar")) return false;
    if (!nextTokenIs(b, E_ANGLE_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_DATA_VAR, null);
    r = consumeToken(b, E_ANGLE_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, dataVar_1(b, l + 1));
    r = p && consumeToken(b, E_ANGLE_RIGHT) && r;
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
    Marker m = enter_section_(b, l, _NONE_, E_DATA_VAR_ENTRY, "<data var entry>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, E_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataVarValue(b, l + 1));
    r = p && dataVarEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean dataVarEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataVarEntry_3")) return false;
    consumeToken(b, E_COMMA);
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
  // ! ('import' | 'namespace' | 'polymorphic' | 'abstract' | 'record' | ',' |
  //                            'map' | 'list' | 'vartype' | 'enum' | 'supplement'|
  //                            'string' | 'integer' | 'long' | 'double' | 'boolean')
  static boolean declRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !declRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'import' | 'namespace' | 'polymorphic' | 'abstract' | 'record' | ',' |
  //                            'map' | 'list' | 'vartype' | 'enum' | 'supplement'|
  //                            'string' | 'integer' | 'long' | 'double' | 'boolean'
  private static boolean declRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_IMPORT);
    if (!r) r = consumeToken(b, E_NAMESPACE);
    if (!r) r = consumeToken(b, E_POLYMORPHIC);
    if (!r) r = consumeToken(b, E_ABSTRACT);
    if (!r) r = consumeToken(b, E_RECORD);
    if (!r) r = consumeToken(b, E_COMMA);
    if (!r) r = consumeToken(b, E_MAP);
    if (!r) r = consumeToken(b, E_LIST);
    if (!r) r = consumeToken(b, E_VARTYPE);
    if (!r) r = consumeToken(b, E_ENUM);
    if (!r) r = consumeToken(b, E_SUPPLEMENT);
    if (!r) r = consumeToken(b, E_STRING_T);
    if (!r) r = consumeToken(b, E_INTEGER_T);
    if (!r) r = consumeToken(b, E_LONG_T);
    if (!r) r = consumeToken(b, E_DOUBLE_T);
    if (!r) r = consumeToken(b, E_BOOLEAN_T);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // typeDefWrapper | supplementDef
  static boolean def(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "def")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = typeDefWrapper(b, l + 1);
    if (!r) r = supplementDef(b, l + 1);
    exit_section_(b, l, m, r, false, declRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'nodefault' | 'default' varTagRef
  public static boolean defaultOverride(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defaultOverride")) return false;
    if (!nextTokenIs(b, "<default override>", E_DEFAULT, E_NODEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_DEFAULT_OVERRIDE, "<default override>");
    r = consumeToken(b, E_NODEFAULT);
    if (!r) r = defaultOverride_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'default' varTagRef
  private static boolean defaultOverride_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defaultOverride_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_DEFAULT);
    r = r && varTagRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // def*
  public static boolean defs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defs")) return false;
    Marker m = enter_section_(b, l, _NONE_, E_DEFS, "<defs>");
    int c = current_position_(b);
    while (true) {
      if (!def(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "defs", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // '{' (enumMemberBodyPart ','?)* '}'
  static boolean enumMemberBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, enumMemberBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (enumMemberBodyPart ','?)*
  private static boolean enumMemberBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!enumMemberBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "enumMemberBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // enumMemberBodyPart ','?
  private static boolean enumMemberBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = enumMemberBodyPart(b, l + 1);
    r = r && enumMemberBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean enumMemberBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberBody_1_0_1")) return false;
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // customParam
  static boolean enumMemberBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // qid enumMemberBody?
  public static boolean enumMemberDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberDecl")) return false;
    if (!nextTokenIs(b, E_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_ENUM_MEMBER_DECL, null);
    r = qid(b, l + 1);
    p = r; // pin = 1
    r = r && enumMemberDecl_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // enumMemberBody?
  private static boolean enumMemberDecl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberDecl_1")) return false;
    enumMemberBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (enumTypeBodyPart ','?)* '}'
  public static boolean enumTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_ENUM_TYPE_BODY, null);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, enumTypeBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (enumTypeBodyPart ','?)*
  private static boolean enumTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!enumTypeBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "enumTypeBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // enumTypeBodyPart ','?
  private static boolean enumTypeBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = enumTypeBodyPart(b, l + 1);
    r = r && enumTypeBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean enumTypeBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeBody_1_0_1")) return false;
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // customParam | enumMemberDecl
  static boolean enumTypeBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = customParam(b, l + 1);
    if (!r) r = enumMemberDecl(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'enum' typeName metaDecl? enumTypeBody
  public static boolean enumTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeDef")) return false;
    if (!nextTokenIs(b, E_ENUM)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_ENUM_TYPE_DEF, null);
    r = consumeToken(b, E_ENUM);
    p = r; // pin = 1
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, enumTypeDef_2(b, l + 1)) && r;
    r = p && enumTypeBody(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // metaDecl?
  private static boolean enumTypeDef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeDef_2")) return false;
    metaDecl(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'extends' fqnTypeRef (',' fqnTypeRef)*
  public static boolean extendsDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendsDecl")) return false;
    if (!nextTokenIs(b, E_EXTENDS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_EXTENDS_DECL, null);
    r = consumeToken(b, E_EXTENDS);
    p = r; // pin = 1
    r = r && report_error_(b, fqnTypeRef(b, l + 1));
    r = p && extendsDecl_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (',' fqnTypeRef)*
  private static boolean extendsDecl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendsDecl_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!extendsDecl_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extendsDecl_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' fqnTypeRef
  private static boolean extendsDecl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendsDecl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_COMMA);
    r = r && fqnTypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (fieldBodyPart ','?)* '}'
  static boolean fieldBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, fieldBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (fieldBodyPart ','?)*
  private static boolean fieldBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!fieldBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "fieldBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // fieldBodyPart ','?
  private static boolean fieldBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fieldBodyPart(b, l + 1);
    r = r && fieldBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean fieldBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldBody_1_0_1")) return false;
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // customParam
  static boolean fieldBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // typeMemberModifiers qid ':' valueTypeRef fieldBody?
  public static boolean fieldDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldDecl")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_FIELD_DECL, "<field decl>");
    r = typeMemberModifiers(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && consumeToken(b, E_COLON);
    p = r; // pin = 3
    r = r && report_error_(b, valueTypeRef(b, l + 1));
    r = p && fieldDecl_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // fieldBody?
  private static boolean fieldDecl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldDecl_4")) return false;
    fieldBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // fqnSegment ('.' fqnSegment)*
  public static boolean fqn(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fqn")) return false;
    if (!nextTokenIs(b, E_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fqnSegment(b, l + 1);
    r = r && fqn_1(b, l + 1);
    exit_section_(b, m, E_FQN, r);
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
    r = consumeToken(b, E_DOT);
    r = r && fqnSegment(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qid
  public static boolean fqnSegment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fqnSegment")) return false;
    if (!nextTokenIs(b, E_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, E_FQN_SEGMENT, r);
    return r;
  }

  /* ********************************************************** */
  // fqn
  public static boolean fqnTypeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fqnTypeRef")) return false;
    if (!nextTokenIs(b, E_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fqn(b, l + 1);
    exit_section_(b, m, E_FQN_TYPE_REF, r);
    return r;
  }

  /* ********************************************************** */
  // opOutputVarProjection
  static boolean idlRoot(PsiBuilder b, int l) {
    return opOutputVarProjection(b, l + 1);
  }

  /* ********************************************************** */
  // 'import' fqn
  public static boolean importStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_IMPORT_STATEMENT, "<import statement>");
    r = consumeToken(b, E_IMPORT);
    p = r; // pin = 1
    r = r && fqn(b, l + 1);
    exit_section_(b, l, m, r, p, declRecover_parser_);
    return r || p;
  }

  /* ********************************************************** */
  // importStatement*
  public static boolean imports(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "imports")) return false;
    Marker m = enter_section_(b, l, _NONE_, E_IMPORTS, "<imports>");
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
  // '{' (listTypeBodyPart ','?)* '}'
  public static boolean listTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_LIST_TYPE_BODY, null);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, listTypeBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (listTypeBodyPart ','?)*
  private static boolean listTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!listTypeBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "listTypeBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // listTypeBodyPart ','?
  private static boolean listTypeBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = listTypeBodyPart(b, l + 1);
    r = r && listTypeBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean listTypeBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeBody_1_0_1")) return false;
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // customParam
  static boolean listTypeBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // typeDefModifiers anonList typeName extendsDecl? metaDecl? supplementsDecl? listTypeBody?
  public static boolean listTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeDef")) return false;
    if (!nextTokenIs(b, "<list type def>", E_ABSTRACT, E_LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_LIST_TYPE_DEF, "<list type def>");
    r = typeDefModifiers(b, l + 1);
    r = r && anonList(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, listTypeDef_3(b, l + 1)) && r;
    r = p && report_error_(b, listTypeDef_4(b, l + 1)) && r;
    r = p && report_error_(b, listTypeDef_5(b, l + 1)) && r;
    r = p && listTypeDef_6(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // extendsDecl?
  private static boolean listTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeDef_3")) return false;
    extendsDecl(b, l + 1);
    return true;
  }

  // metaDecl?
  private static boolean listTypeDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeDef_4")) return false;
    metaDecl(b, l + 1);
    return true;
  }

  // supplementsDecl?
  private static boolean listTypeDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeDef_5")) return false;
    supplementsDecl(b, l + 1);
    return true;
  }

  // listTypeBody?
  private static boolean listTypeDef_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeDef_6")) return false;
    listTypeBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (mapTypeBodyPart ','?)* '}'
  public static boolean mapTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_MAP_TYPE_BODY, null);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, mapTypeBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (mapTypeBodyPart ','?)*
  private static boolean mapTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!mapTypeBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "mapTypeBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // mapTypeBodyPart ','?
  private static boolean mapTypeBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = mapTypeBodyPart(b, l + 1);
    r = r && mapTypeBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean mapTypeBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeBody_1_0_1")) return false;
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // customParam
  static boolean mapTypeBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // typeDefModifiers anonMap typeName extendsDecl? metaDecl? supplementsDecl? mapTypeBody?
  public static boolean mapTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeDef")) return false;
    if (!nextTokenIs(b, "<map type def>", E_ABSTRACT, E_MAP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_MAP_TYPE_DEF, "<map type def>");
    r = typeDefModifiers(b, l + 1);
    r = r && anonMap(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, mapTypeDef_3(b, l + 1)) && r;
    r = p && report_error_(b, mapTypeDef_4(b, l + 1)) && r;
    r = p && report_error_(b, mapTypeDef_5(b, l + 1)) && r;
    r = p && mapTypeDef_6(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // extendsDecl?
  private static boolean mapTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeDef_3")) return false;
    extendsDecl(b, l + 1);
    return true;
  }

  // metaDecl?
  private static boolean mapTypeDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeDef_4")) return false;
    metaDecl(b, l + 1);
    return true;
  }

  // supplementsDecl?
  private static boolean mapTypeDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeDef_5")) return false;
    supplementsDecl(b, l + 1);
    return true;
  }

  // mapTypeBody?
  private static boolean mapTypeDef_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeDef_6")) return false;
    mapTypeBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'meta' fqnTypeRef
  public static boolean metaDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaDecl")) return false;
    if (!nextTokenIs(b, E_META)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_META_DECL, null);
    r = consumeToken(b, E_META);
    p = r; // pin = 1
    r = r && fqnTypeRef(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' namespaceBodyPart* '}'
  static boolean namespaceBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, namespaceBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
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
    if (!recursion_guard_(b, l, "namespaceBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'namespace' fqn namespaceBody?
  public static boolean namespaceDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDecl")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_NAMESPACE_DECL, "<namespace decl>");
    r = consumeToken(b, E_NAMESPACE);
    p = r; // pin = 1
    r = r && report_error_(b, fqn(b, l + 1));
    r = p && namespaceDecl_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, declRecover_parser_);
    return r || p;
  }

  // namespaceBody?
  private static boolean namespaceDecl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDecl_2")) return false;
    namespaceBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'ip'
  public static boolean opInputModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opInputModelProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_INPUT_MODEL_PROJECTION, "<op input model projection>");
    r = consumeToken(b, "ip");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'enum'
  public static boolean opOutputEnumModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputEnumModelProjection")) return false;
    if (!nextTokenIs(b, E_ENUM)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_ENUM);
    exit_section_(b, m, E_OP_OUTPUT_ENUM_MODEL_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // qid opOutputFieldProjectionBody? (':' opOutputVarProjection)?
  public static boolean opOutputFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjection")) return false;
    if (!nextTokenIs(b, E_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && opOutputFieldProjection_1(b, l + 1);
    r = r && opOutputFieldProjection_2(b, l + 1);
    exit_section_(b, m, E_OP_OUTPUT_FIELD_PROJECTION, r);
    return r;
  }

  // opOutputFieldProjectionBody?
  private static boolean opOutputFieldProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjection_1")) return false;
    opOutputFieldProjectionBody(b, l + 1);
    return true;
  }

  // (':' opOutputVarProjection)?
  private static boolean opOutputFieldProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjection_2")) return false;
    opOutputFieldProjection_2_0(b, l + 1);
    return true;
  }

  // ':' opOutputVarProjection
  private static boolean opOutputFieldProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_COLON);
    r = r && opOutputVarProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (opOutputFieldProjectionBodyPart ','?)* '}'
  public static boolean opOutputFieldProjectionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjectionBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_FIELD_PROJECTION_BODY, null);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opOutputFieldProjectionBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
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
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opParameters | customParam
  public static boolean opOutputFieldProjectionBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputFieldProjectionBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_FIELD_PROJECTION_BODY_PART, "<op output field projection body part>");
    r = opParameters(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // '[' (opOutputKeyProjectionPart ','?)* ']'
  public static boolean opOutputKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjection")) return false;
    if (!nextTokenIs(b, E_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_BRACKET_LEFT);
    r = r && opOutputKeyProjection_1(b, l + 1);
    r = r && consumeToken(b, E_BRACKET_RIGHT);
    exit_section_(b, m, E_OP_OUTPUT_KEY_PROJECTION, r);
    return r;
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
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // 'forbidden' | 'required' | opParameters | customParam
  public static boolean opOutputKeyProjectionPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputKeyProjectionPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_KEY_PROJECTION_PART, "<op output key projection part>");
    r = consumeToken(b, "forbidden");
    if (!r) r = consumeToken(b, "required");
    if (!r) r = opParameters(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // '*' opOutputVarProjection opOutputListPolyBranch*
  public static boolean opOutputListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputListModelProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_LIST_MODEL_PROJECTION, "<op output list model projection>");
    r = consumeToken(b, "*");
    r = r && opOutputVarProjection(b, l + 1);
    r = r && opOutputListModelProjection_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // opOutputListPolyBranch*
  private static boolean opOutputListModelProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputListModelProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputListPolyBranch(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputListModelProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // '~' fqnTypeRef '<' opOutputListModelProjection '>'
  public static boolean opOutputListPolyBranch(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputListPolyBranch")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_LIST_POLY_BRANCH, "<op output list poly branch>");
    r = consumeToken(b, "~");
    r = r && fqnTypeRef(b, l + 1);
    r = r && consumeToken(b, E_ANGLE_LEFT);
    r = r && opOutputListModelProjection(b, l + 1);
    r = r && consumeToken(b, E_ANGLE_RIGHT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // opOutputKeyProjection '*' opOutputVarProjection opOutputMapPolyBranch
  public static boolean opOutputMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMapModelProjection")) return false;
    if (!nextTokenIs(b, E_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputKeyProjection(b, l + 1);
    r = r && consumeToken(b, "*");
    r = r && opOutputVarProjection(b, l + 1);
    r = r && opOutputMapPolyBranch(b, l + 1);
    exit_section_(b, m, E_OP_OUTPUT_MAP_MODEL_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // '~' fqnTypeRef '<' opOutputMapModelProjection '>'
  public static boolean opOutputMapPolyBranch(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputMapPolyBranch")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_MAP_POLY_BRANCH, "<op output map poly branch>");
    r = consumeToken(b, "~");
    r = r && fqnTypeRef(b, l + 1);
    r = r && consumeToken(b, E_ANGLE_LEFT);
    r = r && opOutputMapModelProjection(b, l + 1);
    r = r && consumeToken(b, E_ANGLE_RIGHT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (opOutputRecordModelProjection | opOutputListModelProjection |
  //                              opOutputMapModelProjection | opOutputEnumModelProjection |
  //                              opOutputPrimitiveModelProjection ) opOutputModelProjectionBody
  public static boolean opOutputModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_MODEL_PROJECTION, "<op output model projection>");
    r = opOutputModelProjection_0(b, l + 1);
    r = r && opOutputModelProjectionBody(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // opOutputRecordModelProjection | opOutputListModelProjection |
  //                              opOutputMapModelProjection | opOutputEnumModelProjection |
  //                              opOutputPrimitiveModelProjection
  private static boolean opOutputModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opOutputRecordModelProjection(b, l + 1);
    if (!r) r = opOutputListModelProjection(b, l + 1);
    if (!r) r = opOutputMapModelProjection(b, l + 1);
    if (!r) r = opOutputEnumModelProjection(b, l + 1);
    if (!r) r = opOutputPrimitiveModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (opOutputModelProjectionBodyPart ','?)* '}'
  public static boolean opOutputModelProjectionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjectionBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_MODEL_PROJECTION_BODY, null);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opOutputModelProjectionBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
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
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // 'required' | opParameters | customParam
  public static boolean opOutputModelProjectionBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputModelProjectionBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_MODEL_PROJECTION_BODY_PART, "<op output model projection body part>");
    r = consumeToken(b, "required");
    if (!r) r = opParameters(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'primitive'
  public static boolean opOutputPrimitiveModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputPrimitiveModelProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_PRIMITIVE_MODEL_PROJECTION, "<op output primitive model projection>");
    r = consumeToken(b, "primitive");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '(' (opOutputFieldProjection ','?)* ')' opOutputRecordPolyBranch*
  public static boolean opOutputRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputRecordModelProjection")) return false;
    if (!nextTokenIs(b, E_PAREN_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_PAREN_LEFT);
    r = r && opOutputRecordModelProjection_1(b, l + 1);
    r = r && consumeToken(b, E_PAREN_RIGHT);
    r = r && opOutputRecordModelProjection_3(b, l + 1);
    exit_section_(b, m, E_OP_OUTPUT_RECORD_MODEL_PROJECTION, r);
    return r;
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
    consumeToken(b, E_COMMA);
    return true;
  }

  // opOutputRecordPolyBranch*
  private static boolean opOutputRecordModelProjection_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputRecordModelProjection_3")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputRecordPolyBranch(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputRecordModelProjection_3", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // '~' fqnTypeRef '<' opOutputRecordModelProjection '>'
  public static boolean opOutputRecordPolyBranch(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputRecordPolyBranch")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_RECORD_POLY_BRANCH, "<op output record poly branch>");
    r = consumeToken(b, "~");
    r = r && fqnTypeRef(b, l + 1);
    r = r && consumeToken(b, E_ANGLE_LEFT);
    r = r && opOutputRecordModelProjection(b, l + 1);
    r = r && consumeToken(b, E_ANGLE_RIGHT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // qid ':' opOutputModelProjection
  public static boolean opOutputTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputTagProjection")) return false;
    if (!nextTokenIs(b, E_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && consumeToken(b, E_COLON);
    r = r && opOutputModelProjection(b, l + 1);
    exit_section_(b, m, E_OP_OUTPUT_TAG_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // ('{' opOutputTagProjection* '}') | 'default' opOutputModelProjection
  public static boolean opOutputVarProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarProjection")) return false;
    if (!nextTokenIs(b, "<op output var projection>", E_DEFAULT, E_CURLY_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_OUTPUT_VAR_PROJECTION, "<op output var projection>");
    r = opOutputVarProjection_0(b, l + 1);
    if (!r) r = opOutputVarProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '{' opOutputTagProjection* '}'
  private static boolean opOutputVarProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_CURLY_LEFT);
    r = r && opOutputVarProjection_0_1(b, l + 1);
    r = r && consumeToken(b, E_CURLY_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  // opOutputTagProjection*
  private static boolean opOutputVarProjection_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarProjection_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opOutputTagProjection(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opOutputVarProjection_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // 'default' opOutputModelProjection
  private static boolean opOutputVarProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opOutputVarProjection_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_DEFAULT);
    r = r && opOutputModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qid ':' opInputModelProjection
  public static boolean opParamProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParamProjection")) return false;
    if (!nextTokenIs(b, E_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && consumeToken(b, E_COLON);
    r = r && opInputModelProjection(b, l + 1);
    exit_section_(b, m, E_OP_PARAM_PROJECTION, r);
    return r;
  }

  /* ********************************************************** */
  // 'parameters' ':' '{' (opParamProjection ','?)* '}'
  public static boolean opParameters(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParameters")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_OP_PARAMETERS, "<op parameters>");
    r = consumeToken(b, "parameters");
    r = r && consumeToken(b, E_COLON);
    r = r && consumeToken(b, E_CURLY_LEFT);
    r = r && opParameters_3(b, l + 1);
    r = r && consumeToken(b, E_CURLY_RIGHT);
    exit_section_(b, l, m, r, false, null);
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
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ! ('}' | qid | 'abstract' | 'override' | ',' )
  static boolean partRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !partRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}' | qid | 'abstract' | 'override' | ','
  private static boolean partRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_CURLY_RIGHT);
    if (!r) r = qid(b, l + 1);
    if (!r) r = consumeToken(b, E_ABSTRACT);
    if (!r) r = consumeToken(b, E_OVERRIDE);
    if (!r) r = consumeToken(b, E_COMMA);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // customParam
  static boolean primitiveBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'string' | 'integer' | 'long' | 'double' | 'boolean'
  static boolean primitiveKind(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveKind")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_STRING_T);
    if (!r) r = consumeToken(b, E_INTEGER_T);
    if (!r) r = consumeToken(b, E_LONG_T);
    if (!r) r = consumeToken(b, E_DOUBLE_T);
    if (!r) r = consumeToken(b, E_BOOLEAN_T);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (primitiveBodyPart ','?)* '}'
  public static boolean primitiveTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_PRIMITIVE_TYPE_BODY, null);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, primitiveTypeBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (primitiveBodyPart ','?)*
  private static boolean primitiveTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!primitiveTypeBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "primitiveTypeBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // primitiveBodyPart ','?
  private static boolean primitiveTypeBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = primitiveBodyPart(b, l + 1);
    r = r && primitiveTypeBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean primitiveTypeBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeBody_1_0_1")) return false;
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeDefModifiers primitiveKind typeName extendsDecl? metaDecl? supplementsDecl? primitiveTypeBody?
  public static boolean primitiveTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeDef")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_PRIMITIVE_TYPE_DEF, "<primitive type def>");
    r = typeDefModifiers(b, l + 1);
    r = r && primitiveKind(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, primitiveTypeDef_3(b, l + 1)) && r;
    r = p && report_error_(b, primitiveTypeDef_4(b, l + 1)) && r;
    r = p && report_error_(b, primitiveTypeDef_5(b, l + 1)) && r;
    r = p && primitiveTypeDef_6(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // extendsDecl?
  private static boolean primitiveTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeDef_3")) return false;
    extendsDecl(b, l + 1);
    return true;
  }

  // metaDecl?
  private static boolean primitiveTypeDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeDef_4")) return false;
    metaDecl(b, l + 1);
    return true;
  }

  // supplementsDecl?
  private static boolean primitiveTypeDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeDef_5")) return false;
    supplementsDecl(b, l + 1);
    return true;
  }

  // primitiveTypeBody?
  private static boolean primitiveTypeDef_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeDef_6")) return false;
    primitiveTypeBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // id
  public static boolean qid(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qid")) return false;
    if (!nextTokenIs(b, E_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_ID);
    exit_section_(b, m, E_QID, r);
    return r;
  }

  /* ********************************************************** */
  // fieldDecl | customParam
  static boolean recordBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = fieldDecl(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // '{' (recordBodyPart ','?)* '}'
  public static boolean recordTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_RECORD_TYPE_BODY, null);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, recordTypeBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (recordBodyPart ','?)*
  private static boolean recordTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!recordTypeBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "recordTypeBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // recordBodyPart ','?
  private static boolean recordTypeBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = recordBodyPart(b, l + 1);
    r = r && recordTypeBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean recordTypeBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeBody_1_0_1")) return false;
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeDefModifiers 'record' typeName extendsDecl? metaDecl? supplementsDecl? recordTypeBody?
  public static boolean recordTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeDef")) return false;
    if (!nextTokenIs(b, "<record type def>", E_ABSTRACT, E_RECORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_RECORD_TYPE_DEF, "<record type def>");
    r = typeDefModifiers(b, l + 1);
    r = r && consumeToken(b, E_RECORD);
    p = r; // pin = 2
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, recordTypeDef_3(b, l + 1)) && r;
    r = p && report_error_(b, recordTypeDef_4(b, l + 1)) && r;
    r = p && report_error_(b, recordTypeDef_5(b, l + 1)) && r;
    r = p && recordTypeDef_6(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // extendsDecl?
  private static boolean recordTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeDef_3")) return false;
    extendsDecl(b, l + 1);
    return true;
  }

  // metaDecl?
  private static boolean recordTypeDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeDef_4")) return false;
    metaDecl(b, l + 1);
    return true;
  }

  // supplementsDecl?
  private static boolean recordTypeDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeDef_5")) return false;
    supplementsDecl(b, l + 1);
    return true;
  }

  // recordTypeBody?
  private static boolean recordTypeDef_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeDef_6")) return false;
    recordTypeBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // schemaRoot | idlRoot
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = schemaRoot(b, l + 1);
    if (!r) r = idlRoot(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // namespaceDecl imports defs
  static boolean schemaRoot(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "schemaRoot")) return false;
    if (!nextTokenIs(b, E_NAMESPACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespaceDecl(b, l + 1);
    r = r && imports(b, l + 1);
    r = r && defs(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'supplement' fqnTypeRef (',' fqnTypeRef)* 'with' fqnTypeRef
  public static boolean supplementDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementDef")) return false;
    if (!nextTokenIs(b, E_SUPPLEMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_SUPPLEMENT_DEF, null);
    r = consumeToken(b, E_SUPPLEMENT);
    p = r; // pin = 1
    r = r && report_error_(b, fqnTypeRef(b, l + 1));
    r = p && report_error_(b, supplementDef_2(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, E_WITH)) && r;
    r = p && fqnTypeRef(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (',' fqnTypeRef)*
  private static boolean supplementDef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementDef_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!supplementDef_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "supplementDef_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' fqnTypeRef
  private static boolean supplementDef_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementDef_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_COMMA);
    r = r && fqnTypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'supplements' fqnTypeRef (',' fqnTypeRef)*
  public static boolean supplementsDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementsDecl")) return false;
    if (!nextTokenIs(b, E_SUPPLEMENTS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_SUPPLEMENTS_DECL, null);
    r = consumeToken(b, E_SUPPLEMENTS);
    p = r; // pin = 1
    r = r && report_error_(b, fqnTypeRef(b, l + 1));
    r = p && supplementsDecl_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (',' fqnTypeRef)*
  private static boolean supplementsDecl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementsDecl_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!supplementsDecl_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "supplementsDecl_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' fqnTypeRef
  private static boolean supplementsDecl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementsDecl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, E_COMMA);
    r = r && fqnTypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'abstract'?
  static boolean typeDefModifiers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeDefModifiers")) return false;
    consumeToken(b, E_ABSTRACT);
    return true;
  }

  /* ********************************************************** */
  // varTypeDef | recordTypeDef | mapTypeDef | listTypeDef | primitiveTypeDef | enumTypeDef
  public static boolean typeDefWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeDefWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_TYPE_DEF_WRAPPER, "<type definition>");
    r = varTypeDef(b, l + 1);
    if (!r) r = recordTypeDef(b, l + 1);
    if (!r) r = mapTypeDef(b, l + 1);
    if (!r) r = listTypeDef(b, l + 1);
    if (!r) r = primitiveTypeDef(b, l + 1);
    if (!r) r = enumTypeDef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'override'? 'abstract'?
  static boolean typeMemberModifiers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeMemberModifiers")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = typeMemberModifiers_0(b, l + 1);
    r = r && typeMemberModifiers_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'override'?
  private static boolean typeMemberModifiers_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeMemberModifiers_0")) return false;
    consumeToken(b, E_OVERRIDE);
    return true;
  }

  // 'abstract'?
  private static boolean typeMemberModifiers_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeMemberModifiers_1")) return false;
    consumeToken(b, E_ABSTRACT);
    return true;
  }

  /* ********************************************************** */
  // qid
  static boolean typeName(PsiBuilder b, int l) {
    return qid(b, l + 1);
  }

  /* ********************************************************** */
  // fqnTypeRef | anonList | anonMap
  public static boolean typeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, E_TYPE_REF, "<type>");
    r = fqnTypeRef(b, l + 1);
    if (!r) r = anonList(b, l + 1);
    if (!r) r = anonMap(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'polymorphic'? typeRef defaultOverride?
  public static boolean valueTypeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueTypeRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, E_VALUE_TYPE_REF, "<value type ref>");
    r = valueTypeRef_0(b, l + 1);
    r = r && typeRef(b, l + 1);
    r = r && valueTypeRef_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'polymorphic'?
  private static boolean valueTypeRef_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueTypeRef_0")) return false;
    consumeToken(b, E_POLYMORPHIC);
    return true;
  }

  // defaultOverride?
  private static boolean valueTypeRef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueTypeRef_2")) return false;
    defaultOverride(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // typeMemberModifiers qid ':' typeRef varTypeMemberBody?
  public static boolean varTagDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTagDecl")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_VAR_TAG_DECL, "<var tag decl>");
    r = typeMemberModifiers(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && consumeToken(b, E_COLON);
    p = r; // pin = 3
    r = r && report_error_(b, typeRef(b, l + 1));
    r = p && varTagDecl_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // varTypeMemberBody?
  private static boolean varTagDecl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTagDecl_4")) return false;
    varTypeMemberBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // qid
  public static boolean varTagRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTagRef")) return false;
    if (!nextTokenIs(b, E_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, E_VAR_TAG_REF, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (varTypeBodyPart ','?)* '}'
  public static boolean varTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_VAR_TYPE_BODY, null);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, varTypeBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (varTypeBodyPart ','?)*
  private static boolean varTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!varTypeBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "varTypeBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // varTypeBodyPart ','?
  private static boolean varTypeBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = varTypeBodyPart(b, l + 1);
    r = r && varTypeBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean varTypeBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeBody_1_0_1")) return false;
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // varTagDecl | customParam
  static boolean varTypeBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = varTagDecl(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // typeDefModifiers 'vartype' typeName extendsDecl? supplementsDecl? defaultOverride? varTypeBody?
  public static boolean varTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeDef")) return false;
    if (!nextTokenIs(b, "<var type def>", E_ABSTRACT, E_VARTYPE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, E_VAR_TYPE_DEF, "<var type def>");
    r = typeDefModifiers(b, l + 1);
    r = r && consumeToken(b, E_VARTYPE);
    p = r; // pin = 2
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, varTypeDef_3(b, l + 1)) && r;
    r = p && report_error_(b, varTypeDef_4(b, l + 1)) && r;
    r = p && report_error_(b, varTypeDef_5(b, l + 1)) && r;
    r = p && varTypeDef_6(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // extendsDecl?
  private static boolean varTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeDef_3")) return false;
    extendsDecl(b, l + 1);
    return true;
  }

  // supplementsDecl?
  private static boolean varTypeDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeDef_4")) return false;
    supplementsDecl(b, l + 1);
    return true;
  }

  // defaultOverride?
  private static boolean varTypeDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeDef_5")) return false;
    defaultOverride(b, l + 1);
    return true;
  }

  // varTypeBody?
  private static boolean varTypeDef_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeDef_6")) return false;
    varTypeBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (varTypeMemberBodyPart ','?)* '}'
  static boolean varTypeMemberBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeMemberBody")) return false;
    if (!nextTokenIs(b, E_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, E_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, varTypeMemberBody_1(b, l + 1));
    r = p && consumeToken(b, E_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (varTypeMemberBodyPart ','?)*
  private static boolean varTypeMemberBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeMemberBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!varTypeMemberBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "varTypeMemberBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // varTypeMemberBodyPart ','?
  private static boolean varTypeMemberBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeMemberBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = varTypeMemberBodyPart(b, l + 1);
    r = r && varTypeMemberBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean varTypeMemberBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeMemberBody_1_0_1")) return false;
    consumeToken(b, E_COMMA);
    return true;
  }

  /* ********************************************************** */
  // customParam
  static boolean varTypeMemberBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeMemberBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
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
    if (!r) r = consumeToken(b, E_NULL);
    exit_section_(b, m, null, r);
    return r;
  }

  final static Parser dataValueRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return dataValueRecover(b, l + 1);
    }
  };
  final static Parser declRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return declRecover(b, l + 1);
    }
  };
  final static Parser partRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return partRecover(b, l + 1);
    }
  };
}
