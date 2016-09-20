// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static io.epigraph.schema.lexer.SchemaElementTypes.*;
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
    if (t == S_ANON_LIST) {
      r = anonList(b, 0);
    }
    else if (t == S_ANON_MAP) {
      r = anonMap(b, 0);
    }
    else if (t == S_CUSTOM_PARAM) {
      r = customParam(b, 0);
    }
    else if (t == S_DATA) {
      r = data(b, 0);
    }
    else if (t == S_DATA_ENTRY) {
      r = dataEntry(b, 0);
    }
    else if (t == S_DATA_VALUE) {
      r = dataValue(b, 0);
    }
    else if (t == S_DATUM) {
      r = datum(b, 0);
    }
    else if (t == S_DEFAULT_OVERRIDE) {
      r = defaultOverride(b, 0);
    }
    else if (t == S_DEFS) {
      r = defs(b, 0);
    }
    else if (t == S_ENUM_DATUM) {
      r = enumDatum(b, 0);
    }
    else if (t == S_ENUM_MEMBER_DECL) {
      r = enumMemberDecl(b, 0);
    }
    else if (t == S_ENUM_TYPE_BODY) {
      r = enumTypeBody(b, 0);
    }
    else if (t == S_ENUM_TYPE_DEF) {
      r = enumTypeDef(b, 0);
    }
    else if (t == S_EXTENDS_DECL) {
      r = extendsDecl(b, 0);
    }
    else if (t == S_FIELD_DECL) {
      r = fieldDecl(b, 0);
    }
    else if (t == S_FQN) {
      r = fqn(b, 0);
    }
    else if (t == S_FQN_SEGMENT) {
      r = fqnSegment(b, 0);
    }
    else if (t == S_FQN_TYPE_REF) {
      r = fqnTypeRef(b, 0);
    }
    else if (t == S_IMPORT_STATEMENT) {
      r = importStatement(b, 0);
    }
    else if (t == S_IMPORTS) {
      r = imports(b, 0);
    }
    else if (t == S_LIST_DATUM) {
      r = listDatum(b, 0);
    }
    else if (t == S_LIST_TYPE_BODY) {
      r = listTypeBody(b, 0);
    }
    else if (t == S_LIST_TYPE_DEF) {
      r = listTypeDef(b, 0);
    }
    else if (t == S_MAP_DATUM) {
      r = mapDatum(b, 0);
    }
    else if (t == S_MAP_DATUM_ENTRY) {
      r = mapDatumEntry(b, 0);
    }
    else if (t == S_MAP_TYPE_BODY) {
      r = mapTypeBody(b, 0);
    }
    else if (t == S_MAP_TYPE_DEF) {
      r = mapTypeDef(b, 0);
    }
    else if (t == S_META_DECL) {
      r = metaDecl(b, 0);
    }
    else if (t == S_NAMESPACE_DECL) {
      r = namespaceDecl(b, 0);
    }
    else if (t == S_NULL_DATUM) {
      r = nullDatum(b, 0);
    }
    else if (t == S_PRIMITIVE_DATUM) {
      r = primitiveDatum(b, 0);
    }
    else if (t == S_PRIMITIVE_TYPE_BODY) {
      r = primitiveTypeBody(b, 0);
    }
    else if (t == S_PRIMITIVE_TYPE_DEF) {
      r = primitiveTypeDef(b, 0);
    }
    else if (t == S_QID) {
      r = qid(b, 0);
    }
    else if (t == S_RECORD_DATUM) {
      r = recordDatum(b, 0);
    }
    else if (t == S_RECORD_DATUM_ENTRY) {
      r = recordDatumEntry(b, 0);
    }
    else if (t == S_RECORD_TYPE_BODY) {
      r = recordTypeBody(b, 0);
    }
    else if (t == S_RECORD_TYPE_DEF) {
      r = recordTypeDef(b, 0);
    }
    else if (t == S_SUPPLEMENT_DEF) {
      r = supplementDef(b, 0);
    }
    else if (t == S_SUPPLEMENTS_DECL) {
      r = supplementsDecl(b, 0);
    }
    else if (t == S_TYPE_DEF_WRAPPER) {
      r = typeDefWrapper(b, 0);
    }
    else if (t == S_TYPE_REF) {
      r = typeRef(b, 0);
    }
    else if (t == S_VALUE_TYPE_REF) {
      r = valueTypeRef(b, 0);
    }
    else if (t == S_VAR_TAG_DECL) {
      r = varTagDecl(b, 0);
    }
    else if (t == S_VAR_TAG_REF) {
      r = varTagRef(b, 0);
    }
    else if (t == S_VAR_TYPE_BODY) {
      r = varTypeBody(b, 0);
    }
    else if (t == S_VAR_TYPE_DEF) {
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
    create_token_set_(S_ANON_LIST, S_ANON_MAP, S_FQN_TYPE_REF, S_TYPE_REF),
    create_token_set_(S_DATUM, S_ENUM_DATUM, S_LIST_DATUM, S_MAP_DATUM,
      S_NULL_DATUM, S_PRIMITIVE_DATUM, S_RECORD_DATUM),
  };

  /* ********************************************************** */
  // 'list' '[' valueTypeRef ']'
  public static boolean anonList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonList")) return false;
    if (!nextTokenIs(b, S_LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ANON_LIST, null);
    r = consumeToken(b, S_LIST);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, S_BRACKET_LEFT));
    r = p && report_error_(b, valueTypeRef(b, l + 1)) && r;
    r = p && consumeToken(b, S_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'map' '[' typeRef ',' valueTypeRef ']'
  public static boolean anonMap(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonMap")) return false;
    if (!nextTokenIs(b, S_MAP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ANON_MAP, null);
    r = consumeToken(b, S_MAP);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, S_BRACKET_LEFT));
    r = p && report_error_(b, typeRef(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, S_COMMA)) && r;
    r = p && report_error_(b, valueTypeRef(b, l + 1)) && r;
    r = p && consumeToken(b, S_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // qid '=' dataValue
  public static boolean customParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customParam")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_CUSTOM_PARAM, "<custom attribute>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, S_EQ);
    p = r; // pin = 2
    r = r && dataValue(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // dataTypeSpec? '<' dataEntry* '>'
  public static boolean data(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "data")) return false;
    if (!nextTokenIs(b, "<data>", S_ANGLE_LEFT, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_DATA, "<data>");
    r = data_0(b, l + 1);
    r = r && consumeToken(b, S_ANGLE_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, data_2(b, l + 1));
    r = p && consumeToken(b, S_ANGLE_RIGHT) && r;
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
    Marker m = enter_section_(b, l, _NONE_, S_DATA_ENTRY, "<data entry>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, S_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, datum(b, l + 1));
    r = p && dataEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean dataEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataEntry_3")) return false;
    consumeToken(b, S_COMMA);
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
    Marker m = enter_section_(b, l, _NONE_, S_DATA_VALUE, "<data value>");
    r = data(b, l + 1);
    if (!r) r = datum(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ! ( qid | primitiveDatum | '}' | ')' | '>' | ']' | 'abstract' | 'override' | ',' )
  static boolean dataValueRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !dataValueRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // qid | primitiveDatum | '}' | ')' | '>' | ']' | 'abstract' | 'override' | ','
  private static boolean dataValueRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    if (!r) r = primitiveDatum(b, l + 1);
    if (!r) r = consumeToken(b, S_CURLY_RIGHT);
    if (!r) r = consumeToken(b, S_PAREN_RIGHT);
    if (!r) r = consumeToken(b, S_ANGLE_RIGHT);
    if (!r) r = consumeToken(b, S_BRACKET_RIGHT);
    if (!r) r = consumeToken(b, S_ABSTRACT);
    if (!r) r = consumeToken(b, S_OVERRIDE);
    if (!r) r = consumeToken(b, S_COMMA);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // recordDatum | mapDatum | listDatum | primitiveDatum | enumDatum | nullDatum
  public static boolean datum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "datum")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, S_DATUM, "<datum>");
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
  // namespaceDeclRecover
  static boolean declRecover(PsiBuilder b, int l) {
    return namespaceDeclRecover(b, l + 1);
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
  // 'default' varTagRef
  public static boolean defaultOverride(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defaultOverride")) return false;
    if (!nextTokenIs(b, S_DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_DEFAULT);
    r = r && varTagRef(b, l + 1);
    exit_section_(b, m, S_DEFAULT_OVERRIDE, r);
    return r;
  }

  /* ********************************************************** */
  // def*
  public static boolean defs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defs")) return false;
    Marker m = enter_section_(b, l, _NONE_, S_DEFS, "<defs>");
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
  // qid
  public static boolean enumDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumDatum")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, S_ENUM_DATUM, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (enumMemberBodyPar ','?)* '}'
  static boolean enumMemberBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, enumMemberBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (enumMemberBodyPar ','?)*
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

  // enumMemberBodyPar ','?
  private static boolean enumMemberBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = enumMemberBodyPar(b, l + 1);
    r = r && enumMemberBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean enumMemberBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberBody_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // customParam
  static boolean enumMemberBodyPar(PsiBuilder b, int l) {
    return customParam(b, l + 1);
  }

  /* ********************************************************** */
  // qid enumMemberBody?
  public static boolean enumMemberDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberDecl")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ENUM_MEMBER_DECL, null);
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
  // ! (',' | qid | '}')
  static boolean enumPartRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumPartRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !enumPartRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ',' | qid | '}'
  private static boolean enumPartRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumPartRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COMMA);
    if (!r) r = qid(b, l + 1);
    if (!r) r = consumeToken(b, S_CURLY_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (enumTypeBodyPart ','?)* '}'
  public static boolean enumTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ENUM_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, enumTypeBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
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
    exit_section_(b, l, m, r, false, enumPartRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'enum' typeName metaDecl? enumTypeBody
  public static boolean enumTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeDef")) return false;
    if (!nextTokenIs(b, S_ENUM)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ENUM_TYPE_DEF, null);
    r = consumeToken(b, S_ENUM);
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
    if (!nextTokenIs(b, S_EXTENDS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_EXTENDS_DECL, null);
    r = consumeToken(b, S_EXTENDS);
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
    r = consumeToken(b, S_COMMA);
    r = r && fqnTypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (fieldBodyPart ','?)* '}'
  static boolean fieldBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, fieldBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
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
    Marker m = enter_section_(b, l, _NONE_, S_FIELD_DECL, "<field decl>");
    r = typeMemberModifiers(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && consumeToken(b, S_COLON);
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
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fqnSegment(b, l + 1);
    r = r && fqn_1(b, l + 1);
    exit_section_(b, m, S_FQN, r);
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
    r = consumeToken(b, S_DOT);
    r = r && fqnSegment(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qid
  public static boolean fqnSegment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fqnSegment")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, S_FQN_SEGMENT, r);
    return r;
  }

  /* ********************************************************** */
  // fqn
  public static boolean fqnTypeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fqnTypeRef")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fqn(b, l + 1);
    exit_section_(b, m, S_FQN_TYPE_REF, r);
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
    Marker m = enter_section_(b, l, _NONE_, S_IMPORT_STATEMENT, "<import statement>");
    r = consumeToken(b, S_IMPORT);
    p = r; // pin = 1
    r = r && fqn(b, l + 1);
    exit_section_(b, l, m, r, p, importRecover_parser_);
    return r || p;
  }

  /* ********************************************************** */
  // importStatement*
  public static boolean imports(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "imports")) return false;
    Marker m = enter_section_(b, l, _NONE_, S_IMPORTS, "<imports>");
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
    if (!nextTokenIs(b, "<list datum>", S_BRACKET_LEFT, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_LIST_DATUM, "<list datum>");
    r = listDatum_0(b, l + 1);
    r = r && consumeToken(b, S_BRACKET_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, listDatum_2(b, l + 1));
    r = p && consumeToken(b, S_BRACKET_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '{' (listTypeBodyPart ','?)* '}'
  public static boolean listTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_LIST_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, listTypeBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
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
    if (!nextTokenIs(b, "<list type def>", S_ABSTRACT, S_LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_LIST_TYPE_DEF, "<list type def>");
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
  // dataTypeSpec? '(' mapDatumEntry* ')'
  public static boolean mapDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapDatum")) return false;
    if (!nextTokenIs(b, "<map datum>", S_PAREN_LEFT, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_MAP_DATUM, "<map datum>");
    r = mapDatum_0(b, l + 1);
    r = r && consumeToken(b, S_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, mapDatum_2(b, l + 1));
    r = p && consumeToken(b, S_PAREN_RIGHT) && r;
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
    Marker m = enter_section_(b, l, _NONE_, S_MAP_DATUM_ENTRY, "<map datum entry>");
    r = datum(b, l + 1);
    r = r && consumeToken(b, S_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataValue(b, l + 1));
    r = p && mapDatumEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean mapDatumEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapDatumEntry_3")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '{' (mapTypeBodyPart ','?)* '}'
  public static boolean mapTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_MAP_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, mapTypeBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
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
    if (!nextTokenIs(b, "<map type def>", S_ABSTRACT, S_MAP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_MAP_TYPE_DEF, "<map type def>");
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
    if (!nextTokenIs(b, S_META)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_META_DECL, null);
    r = consumeToken(b, S_META);
    p = r; // pin = 1
    r = r && fqnTypeRef(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' namespaceBodyPart* '}'
  static boolean namespaceBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, namespaceBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    Marker m = enter_section_(b, l, _NONE_, S_NAMESPACE_DECL, "<namespace decl>");
    r = consumeToken(b, S_NAMESPACE);
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
  // ! ('import' | 'namespace' | 'polymorphic' | 'abstract' | 'record' | ',' |
  //                            'map' | 'list' | 'vartype' | 'enum' | 'supplement'|
  //                            'string' | 'integer' | 'long' | 'double' | 'boolean')
  static boolean namespaceDeclRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDeclRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !namespaceDeclRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'import' | 'namespace' | 'polymorphic' | 'abstract' | 'record' | ',' |
  //                            'map' | 'list' | 'vartype' | 'enum' | 'supplement'|
  //                            'string' | 'integer' | 'long' | 'double' | 'boolean'
  private static boolean namespaceDeclRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDeclRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_IMPORT);
    if (!r) r = consumeToken(b, S_NAMESPACE);
    if (!r) r = consumeToken(b, S_POLYMORPHIC);
    if (!r) r = consumeToken(b, S_ABSTRACT);
    if (!r) r = consumeToken(b, S_RECORD);
    if (!r) r = consumeToken(b, S_COMMA);
    if (!r) r = consumeToken(b, S_MAP);
    if (!r) r = consumeToken(b, S_LIST);
    if (!r) r = consumeToken(b, S_VARTYPE);
    if (!r) r = consumeToken(b, S_ENUM);
    if (!r) r = consumeToken(b, S_SUPPLEMENT);
    if (!r) r = consumeToken(b, S_STRING_T);
    if (!r) r = consumeToken(b, S_INTEGER_T);
    if (!r) r = consumeToken(b, S_LONG_T);
    if (!r) r = consumeToken(b, S_DOUBLE_T);
    if (!r) r = consumeToken(b, S_BOOLEAN_T);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (dataTypeSpec '@')? 'null'
  public static boolean nullDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nullDatum")) return false;
    if (!nextTokenIs(b, "<null datum>", S_NULL, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_NULL_DATUM, "<null datum>");
    r = nullDatum_0(b, l + 1);
    r = r && consumeToken(b, S_NULL);
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
    r = r && consumeToken(b, S_AT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ! ('}' | qid '=' | qid ':' | 'abstract' | 'override' | ',' )
  static boolean partRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !partRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}' | qid '=' | qid ':' | 'abstract' | 'override' | ','
  private static boolean partRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_CURLY_RIGHT);
    if (!r) r = partRecover_0_1(b, l + 1);
    if (!r) r = partRecover_0_2(b, l + 1);
    if (!r) r = consumeToken(b, S_ABSTRACT);
    if (!r) r = consumeToken(b, S_OVERRIDE);
    if (!r) r = consumeToken(b, S_COMMA);
    exit_section_(b, m, null, r);
    return r;
  }

  // qid '='
  private static boolean partRecover_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partRecover_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && consumeToken(b, S_EQ);
    exit_section_(b, m, null, r);
    return r;
  }

  // qid ':'
  private static boolean partRecover_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partRecover_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && consumeToken(b, S_COLON);
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
  // (dataTypeSpec '@')? (string | number | boolean)
  public static boolean primitiveDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveDatum")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_PRIMITIVE_DATUM, "<primitive datum>");
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
    r = r && consumeToken(b, S_AT);
    exit_section_(b, m, null, r);
    return r;
  }

  // string | number | boolean
  private static boolean primitiveDatum_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveDatum_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_STRING);
    if (!r) r = consumeToken(b, S_NUMBER);
    if (!r) r = consumeToken(b, S_BOOLEAN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'string' | 'integer' | 'long' | 'double' | 'boolean'
  static boolean primitiveKind(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveKind")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_STRING_T);
    if (!r) r = consumeToken(b, S_INTEGER_T);
    if (!r) r = consumeToken(b, S_LONG_T);
    if (!r) r = consumeToken(b, S_DOUBLE_T);
    if (!r) r = consumeToken(b, S_BOOLEAN_T);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (primitiveBodyPart ','?)* '}'
  public static boolean primitiveTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_PRIMITIVE_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, primitiveTypeBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeDefModifiers primitiveKind typeName extendsDecl? metaDecl? supplementsDecl? primitiveTypeBody?
  public static boolean primitiveTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeDef")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_PRIMITIVE_TYPE_DEF, "<primitive type def>");
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
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_ID);
    exit_section_(b, m, S_QID, r);
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
  // dataTypeSpec? '{' recordDatumEntry* '}'
  public static boolean recordDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordDatum")) return false;
    if (!nextTokenIs(b, "<record datum>", S_CURLY_LEFT, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_RECORD_DATUM, "<record datum>");
    r = recordDatum_0(b, l + 1);
    r = r && consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, recordDatum_2(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    Marker m = enter_section_(b, l, _NONE_, S_RECORD_DATUM_ENTRY, "<record datum entry>");
    r = qid(b, l + 1);
    r = r && consumeToken(b, S_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataValue(b, l + 1));
    r = p && recordDatumEntry_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, dataValueRecover_parser_);
    return r || p;
  }

  // ','?
  private static boolean recordDatumEntry_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordDatumEntry_3")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '{' (recordBodyPart ','?)* '}'
  public static boolean recordTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_RECORD_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, recordTypeBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // typeDefModifiers 'record' typeName extendsDecl? metaDecl? supplementsDecl? recordTypeBody?
  public static boolean recordTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeDef")) return false;
    if (!nextTokenIs(b, "<record type def>", S_ABSTRACT, S_RECORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_RECORD_TYPE_DEF, "<record type def>");
    r = typeDefModifiers(b, l + 1);
    r = r && consumeToken(b, S_RECORD);
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
  // namespaceDecl imports defs
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    if (!nextTokenIs(b, S_NAMESPACE)) return false;
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
    if (!nextTokenIs(b, S_SUPPLEMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_SUPPLEMENT_DEF, null);
    r = consumeToken(b, S_SUPPLEMENT);
    p = r; // pin = 1
    r = r && report_error_(b, fqnTypeRef(b, l + 1));
    r = p && report_error_(b, supplementDef_2(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, S_WITH)) && r;
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
    r = consumeToken(b, S_COMMA);
    r = r && fqnTypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'supplements' fqnTypeRef (',' fqnTypeRef)*
  public static boolean supplementsDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementsDecl")) return false;
    if (!nextTokenIs(b, S_SUPPLEMENTS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_SUPPLEMENTS_DECL, null);
    r = consumeToken(b, S_SUPPLEMENTS);
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
    r = consumeToken(b, S_COMMA);
    r = r && fqnTypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'abstract'?
  static boolean typeDefModifiers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeDefModifiers")) return false;
    consumeToken(b, S_ABSTRACT);
    return true;
  }

  /* ********************************************************** */
  // varTypeDef | recordTypeDef | mapTypeDef | listTypeDef | primitiveTypeDef | enumTypeDef
  public static boolean typeDefWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeDefWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_TYPE_DEF_WRAPPER, "<type definition>");
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
    consumeToken(b, S_OVERRIDE);
    return true;
  }

  // 'abstract'?
  private static boolean typeMemberModifiers_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeMemberModifiers_1")) return false;
    consumeToken(b, S_ABSTRACT);
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
    Marker m = enter_section_(b, l, _COLLAPSE_, S_TYPE_REF, "<type>");
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
    Marker m = enter_section_(b, l, _NONE_, S_VALUE_TYPE_REF, "<value type ref>");
    r = valueTypeRef_0(b, l + 1);
    r = r && typeRef(b, l + 1);
    r = r && valueTypeRef_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'polymorphic'?
  private static boolean valueTypeRef_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueTypeRef_0")) return false;
    consumeToken(b, S_POLYMORPHIC);
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
    Marker m = enter_section_(b, l, _NONE_, S_VAR_TAG_DECL, "<var tag decl>");
    r = typeMemberModifiers(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && consumeToken(b, S_COLON);
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
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, S_VAR_TAG_REF, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (varTypeBodyPart ','?)* '}'
  public static boolean varTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_VAR_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, varTypeBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
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
  // typeDefModifiers 'vartype' typeName extendsDecl? supplementsDecl? varTypeBody?
  public static boolean varTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeDef")) return false;
    if (!nextTokenIs(b, "<var type def>", S_ABSTRACT, S_VARTYPE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_VAR_TYPE_DEF, "<var type def>");
    r = typeDefModifiers(b, l + 1);
    r = r && consumeToken(b, S_VARTYPE);
    p = r; // pin = 2
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, varTypeDef_3(b, l + 1)) && r;
    r = p && report_error_(b, varTypeDef_4(b, l + 1)) && r;
    r = p && varTypeDef_5(b, l + 1) && r;
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

  // varTypeBody?
  private static boolean varTypeDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeDef_5")) return false;
    varTypeBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (varTypeMemberBodyPart ','?)* '}'
  static boolean varTypeMemberBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "varTypeMemberBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, varTypeMemberBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
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
  final static Parser enumPartRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return enumPartRecover(b, l + 1);
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
  final static Parser partRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return partRecover(b, l + 1);
    }
  };
}
