// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;
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
    else if (t == S_ANON_UNION) {
      r = anonUnion(b, 0);
    }
    else if (t == S_COMBINED_FQNS) {
      r = combinedFqns(b, 0);
    }
    else if (t == S_CUSTOM_PARAM) {
      r = customParam(b, 0);
    }
    else if (t == S_DEFAULT_OVERRIDE) {
      r = defaultOverride(b, 0);
    }
    else if (t == S_DEFS) {
      r = defs(b, 0);
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
    else if (t == S_LIST_TYPE_BODY) {
      r = listTypeBody(b, 0);
    }
    else if (t == S_LIST_TYPE_DEF) {
      r = listTypeDef(b, 0);
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
    else if (t == S_MULTI_MEMBER_DECL) {
      r = multiMemberDecl(b, 0);
    }
    else if (t == S_MULTI_SUPPLEMENTS_DECL) {
      r = multiSupplementsDecl(b, 0);
    }
    else if (t == S_MULTI_TYPE_BODY) {
      r = multiTypeBody(b, 0);
    }
    else if (t == S_MULTI_TYPE_DEF) {
      r = multiTypeDef(b, 0);
    }
    else if (t == S_NAMESPACE_DECL) {
      r = namespaceDecl(b, 0);
    }
    else if (t == S_PRIMITIVE_KIND) {
      r = primitiveKind(b, 0);
    }
    else if (t == S_PRIMITIVE_TYPE_BODY) {
      r = primitiveTypeBody(b, 0);
    }
    else if (t == S_PRIMITIVE_TYPE_DEF) {
      r = primitiveTypeDef(b, 0);
    }
    else if (t == S_RECORD_SUPPLEMENTS_DECL) {
      r = recordSupplementsDecl(b, 0);
    }
    else if (t == S_RECORD_TYPE_BODY) {
      r = recordTypeBody(b, 0);
    }
    else if (t == S_RECORD_TYPE_DEF) {
      r = recordTypeDef(b, 0);
    }
    else if (t == S_STAR_IMPORT_SUFFIX) {
      r = starImportSuffix(b, 0);
    }
    else if (t == S_SUPPLEMENT_DEF) {
      r = supplementDef(b, 0);
    }
    else if (t == S_TAG_COMMON_TYPE) {
      r = tagCommonType(b, 0);
    }
    else if (t == S_TAG_DECL) {
      r = tagDecl(b, 0);
    }
    else if (t == S_TYPE_DEF) {
      r = typeDef(b, 0);
    }
    else if (t == S_TYPE_REF) {
      r = typeRef(b, 0);
    }
    else if (t == S_UNION_TYPE_BODY) {
      r = unionTypeBody(b, 0);
    }
    else if (t == S_UNION_TYPE_DEF) {
      r = unionTypeDef(b, 0);
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
    create_token_set_(S_ENUM_TYPE_DEF, S_LIST_TYPE_DEF, S_MAP_TYPE_DEF, S_MULTI_TYPE_DEF,
      S_PRIMITIVE_TYPE_DEF, S_RECORD_TYPE_DEF, S_TYPE_DEF, S_UNION_TYPE_DEF),
  };

  /* ********************************************************** */
  // 'list' '[' typeRef defaultOverride? ']'
  public static boolean anonList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonList")) return false;
    if (!nextTokenIs(b, S_LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ANON_LIST, null);
    r = consumeToken(b, S_LIST);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, S_BRACKET_LEFT));
    r = p && report_error_(b, typeRef(b, l + 1)) && r;
    r = p && report_error_(b, anonList_3(b, l + 1)) && r;
    r = p && consumeToken(b, S_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // defaultOverride?
  private static boolean anonList_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonList_3")) return false;
    defaultOverride(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'map' '[' typeRef ',' typeRef defaultOverride? ']'
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
    r = p && report_error_(b, typeRef(b, l + 1)) && r;
    r = p && report_error_(b, anonMap_5(b, l + 1)) && r;
    r = p && consumeToken(b, S_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // defaultOverride?
  private static boolean anonMap_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonMap_5")) return false;
    defaultOverride(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'union' '[' typeRef defaultOverride? ']'
  public static boolean anonUnion(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonUnion")) return false;
    if (!nextTokenIs(b, S_UNION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_UNION);
    r = r && consumeToken(b, S_BRACKET_LEFT);
    r = r && typeRef(b, l + 1);
    r = r && anonUnion_3(b, l + 1);
    r = r && consumeToken(b, S_BRACKET_RIGHT);
    exit_section_(b, m, S_ANON_UNION, r);
    return r;
  }

  // defaultOverride?
  private static boolean anonUnion_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonUnion_3")) return false;
    defaultOverride(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // fqnTypeRef ('+' fqnTypeRef)*
  public static boolean combinedFqns(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "combinedFqns")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fqnTypeRef(b, l + 1);
    r = r && combinedFqns_1(b, l + 1);
    exit_section_(b, m, S_COMBINED_FQNS, r);
    return r;
  }

  // ('+' fqnTypeRef)*
  private static boolean combinedFqns_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "combinedFqns_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!combinedFqns_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "combinedFqns_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // '+' fqnTypeRef
  private static boolean combinedFqns_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "combinedFqns_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_PLUS);
    r = r && fqnTypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // id '=' data_value ';'
  public static boolean customParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customParam")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_CUSTOM_PARAM, "<custom attribute>");
    r = consumeToken(b, S_ID);
    r = r && consumeToken(b, S_EQ);
    p = r; // pin = 2
    r = r && report_error_(b, consumeToken(b, S_DATA_VALUE));
    r = p && consumeToken(b, S_SEMI_COLON) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ! ('import' | 'namespace' | 'record' | 'union' | 'map' | 'list' | 'multi' | 'enum' | 'supplement'| primitiveKind)
  static boolean declRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !declRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'import' | 'namespace' | 'record' | 'union' | 'map' | 'list' | 'multi' | 'enum' | 'supplement'| primitiveKind
  private static boolean declRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_IMPORT);
    if (!r) r = consumeToken(b, S_NAMESPACE);
    if (!r) r = consumeToken(b, S_RECORD);
    if (!r) r = consumeToken(b, S_UNION);
    if (!r) r = consumeToken(b, S_MAP);
    if (!r) r = consumeToken(b, S_LIST);
    if (!r) r = consumeToken(b, S_MULTI);
    if (!r) r = consumeToken(b, S_ENUM);
    if (!r) r = consumeToken(b, S_SUPPLEMENT);
    if (!r) r = primitiveKind(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // typeDef | supplementDef
  static boolean def(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "def")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = typeDef(b, l + 1);
    if (!r) r = supplementDef(b, l + 1);
    exit_section_(b, l, m, r, false, declRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'default' id
  public static boolean defaultOverride(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defaultOverride")) return false;
    if (!nextTokenIs(b, S_DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_DEFAULT);
    r = r && consumeToken(b, S_ID);
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
  // '{' enumMemberBodyPart* '}'
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

  // enumMemberBodyPart*
  private static boolean enumMemberBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!enumMemberBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "enumMemberBody_1", c)) break;
      c = current_position_(b);
    }
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
  // id enumMemberBody?
  public static boolean enumMemberDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberDecl")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ENUM_MEMBER_DECL, null);
    r = consumeToken(b, S_ID);
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
  // '{' enumTypeBodyPart* '}'
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

  // enumTypeBodyPart*
  private static boolean enumTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!enumTypeBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "enumTypeBody_1", c)) break;
      c = current_position_(b);
    }
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
  // 'extends' typeRef (',' typeRef)*
  public static boolean extendsDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendsDecl")) return false;
    if (!nextTokenIs(b, S_EXTENDS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_EXTENDS);
    r = r && typeRef(b, l + 1);
    r = r && extendsDecl_2(b, l + 1);
    exit_section_(b, m, S_EXTENDS_DECL, r);
    return r;
  }

  // (',' typeRef)*
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

  // ',' typeRef
  private static boolean extendsDecl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendsDecl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COMMA);
    r = r && typeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' fieldBodyPart* '}'
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

  // fieldBodyPart*
  private static boolean fieldBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!fieldBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "fieldBody_1", c)) break;
      c = current_position_(b);
    }
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
  // id ':' typeRef defaultOverride? fieldBody?
  public static boolean fieldDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldDecl")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_FIELD_DECL, null);
    r = consumeToken(b, S_ID);
    r = r && consumeToken(b, S_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, typeRef(b, l + 1));
    r = p && report_error_(b, fieldDecl_3(b, l + 1)) && r;
    r = p && fieldDecl_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // defaultOverride?
  private static boolean fieldDecl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldDecl_3")) return false;
    defaultOverride(b, l + 1);
    return true;
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
  // id
  public static boolean fqnSegment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fqnSegment")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_ID);
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
  // 'import' fqn starImportSuffix?
  public static boolean importStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_IMPORT_STATEMENT, "<import statement>");
    r = consumeToken(b, S_IMPORT);
    p = r; // pin = 1
    r = r && report_error_(b, fqn(b, l + 1));
    r = p && importStatement_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, declRecover_parser_);
    return r || p;
  }

  // starImportSuffix?
  private static boolean importStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importStatement_2")) return false;
    starImportSuffix(b, l + 1);
    return true;
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
  // '{' listTypeBodyPart* '}'
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

  // listTypeBodyPart*
  private static boolean listTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!listTypeBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "listTypeBody_1", c)) break;
      c = current_position_(b);
    }
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
  // anonList typeName extendsDecl? metaDecl? listTypeBody?
  public static boolean listTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeDef")) return false;
    if (!nextTokenIs(b, S_LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_LIST_TYPE_DEF, null);
    r = anonList(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, listTypeDef_2(b, l + 1)) && r;
    r = p && report_error_(b, listTypeDef_3(b, l + 1)) && r;
    r = p && listTypeDef_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // extendsDecl?
  private static boolean listTypeDef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeDef_2")) return false;
    extendsDecl(b, l + 1);
    return true;
  }

  // metaDecl?
  private static boolean listTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeDef_3")) return false;
    metaDecl(b, l + 1);
    return true;
  }

  // listTypeBody?
  private static boolean listTypeDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeDef_4")) return false;
    listTypeBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' mapTypeBodyPart* '}'
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

  // mapTypeBodyPart*
  private static boolean mapTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!mapTypeBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "mapTypeBody_1", c)) break;
      c = current_position_(b);
    }
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
  // anonMap typeName extendsDecl? metaDecl? mapTypeBody?
  public static boolean mapTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeDef")) return false;
    if (!nextTokenIs(b, S_MAP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_MAP_TYPE_DEF, null);
    r = anonMap(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, mapTypeDef_2(b, l + 1)) && r;
    r = p && report_error_(b, mapTypeDef_3(b, l + 1)) && r;
    r = p && mapTypeDef_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // extendsDecl?
  private static boolean mapTypeDef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeDef_2")) return false;
    extendsDecl(b, l + 1);
    return true;
  }

  // metaDecl?
  private static boolean mapTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeDef_3")) return false;
    metaDecl(b, l + 1);
    return true;
  }

  // mapTypeBody?
  private static boolean mapTypeDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeDef_4")) return false;
    mapTypeBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' memberBodyPart* '}'
  static boolean memberBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, memberBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // memberBodyPart*
  private static boolean memberBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!memberBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "memberBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // customParam
  static boolean memberBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'meta' fqnTypeRef
  public static boolean metaDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaDecl")) return false;
    if (!nextTokenIs(b, S_META)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_META);
    r = r && fqnTypeRef(b, l + 1);
    exit_section_(b, m, S_META_DECL, r);
    return r;
  }

  /* ********************************************************** */
  // multiMemberDecl | customParam
  static boolean multiBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = multiMemberDecl(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'default'? id ':' typeRef memberBody?
  public static boolean multiMemberDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiMemberDecl")) return false;
    if (!nextTokenIs(b, "<multi member decl>", S_DEFAULT, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_MULTI_MEMBER_DECL, "<multi member decl>");
    r = multiMemberDecl_0(b, l + 1);
    r = r && consumeToken(b, S_ID);
    r = r && consumeToken(b, S_COLON);
    p = r; // pin = 3
    r = r && report_error_(b, typeRef(b, l + 1));
    r = p && multiMemberDecl_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // 'default'?
  private static boolean multiMemberDecl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiMemberDecl_0")) return false;
    consumeToken(b, S_DEFAULT);
    return true;
  }

  // memberBody?
  private static boolean multiMemberDecl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiMemberDecl_4")) return false;
    memberBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'supplements' fqnTypeRef (',' fqnTypeRef)*
  public static boolean multiSupplementsDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiSupplementsDecl")) return false;
    if (!nextTokenIs(b, S_SUPPLEMENTS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_SUPPLEMENTS);
    r = r && fqnTypeRef(b, l + 1);
    r = r && multiSupplementsDecl_2(b, l + 1);
    exit_section_(b, m, S_MULTI_SUPPLEMENTS_DECL, r);
    return r;
  }

  // (',' fqnTypeRef)*
  private static boolean multiSupplementsDecl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiSupplementsDecl_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!multiSupplementsDecl_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "multiSupplementsDecl_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' fqnTypeRef
  private static boolean multiSupplementsDecl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiSupplementsDecl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COMMA);
    r = r && fqnTypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' multiBodyPart* '}'
  public static boolean multiTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_MULTI_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, multiTypeBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // multiBodyPart*
  private static boolean multiTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!multiBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "multiTypeBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // 'multi' typeName extendsDecl? multiSupplementsDecl? multiTypeBody?
  public static boolean multiTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiTypeDef")) return false;
    if (!nextTokenIs(b, S_MULTI)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_MULTI_TYPE_DEF, null);
    r = consumeToken(b, S_MULTI);
    p = r; // pin = 1
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, multiTypeDef_2(b, l + 1)) && r;
    r = p && report_error_(b, multiTypeDef_3(b, l + 1)) && r;
    r = p && multiTypeDef_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // extendsDecl?
  private static boolean multiTypeDef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiTypeDef_2")) return false;
    extendsDecl(b, l + 1);
    return true;
  }

  // multiSupplementsDecl?
  private static boolean multiTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiTypeDef_3")) return false;
    multiSupplementsDecl(b, l + 1);
    return true;
  }

  // multiTypeBody?
  private static boolean multiTypeDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiTypeDef_4")) return false;
    multiTypeBody(b, l + 1);
    return true;
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
  // ! ('}' | id | 'default' )
  static boolean partRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !partRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}' | id | 'default'
  private static boolean partRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_CURLY_RIGHT);
    if (!r) r = consumeToken(b, S_ID);
    if (!r) r = consumeToken(b, S_DEFAULT);
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
  public static boolean primitiveKind(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveKind")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_PRIMITIVE_KIND, "<primitive kind>");
    r = consumeToken(b, S_STRING_T);
    if (!r) r = consumeToken(b, S_INTEGER_T);
    if (!r) r = consumeToken(b, S_LONG_T);
    if (!r) r = consumeToken(b, S_DOUBLE_T);
    if (!r) r = consumeToken(b, S_BOOLEAN_T);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{' primitiveBodyPart* '}'
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

  // primitiveBodyPart*
  private static boolean primitiveTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!primitiveBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "primitiveTypeBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // primitiveKind typeName extendsDecl? metaDecl? primitiveTypeBody?
  public static boolean primitiveTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeDef")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_PRIMITIVE_TYPE_DEF, "<primitive type def>");
    r = primitiveKind(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, primitiveTypeDef_2(b, l + 1)) && r;
    r = p && report_error_(b, primitiveTypeDef_3(b, l + 1)) && r;
    r = p && primitiveTypeDef_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // extendsDecl?
  private static boolean primitiveTypeDef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeDef_2")) return false;
    extendsDecl(b, l + 1);
    return true;
  }

  // metaDecl?
  private static boolean primitiveTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeDef_3")) return false;
    metaDecl(b, l + 1);
    return true;
  }

  // primitiveTypeBody?
  private static boolean primitiveTypeDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeDef_4")) return false;
    primitiveTypeBody(b, l + 1);
    return true;
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
  // 'supplements' combinedFqns (',' combinedFqns)*
  public static boolean recordSupplementsDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordSupplementsDecl")) return false;
    if (!nextTokenIs(b, S_SUPPLEMENTS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_SUPPLEMENTS);
    r = r && combinedFqns(b, l + 1);
    r = r && recordSupplementsDecl_2(b, l + 1);
    exit_section_(b, m, S_RECORD_SUPPLEMENTS_DECL, r);
    return r;
  }

  // (',' combinedFqns)*
  private static boolean recordSupplementsDecl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordSupplementsDecl_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!recordSupplementsDecl_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "recordSupplementsDecl_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' combinedFqns
  private static boolean recordSupplementsDecl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordSupplementsDecl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COMMA);
    r = r && combinedFqns(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' recordBodyPart* '}'
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

  // recordBodyPart*
  private static boolean recordTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!recordBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "recordTypeBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // 'record' typeName extendsDecl? metaDecl? recordSupplementsDecl? recordTypeBody?
  public static boolean recordTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeDef")) return false;
    if (!nextTokenIs(b, S_RECORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_RECORD_TYPE_DEF, null);
    r = consumeToken(b, S_RECORD);
    p = r; // pin = 1
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, recordTypeDef_2(b, l + 1)) && r;
    r = p && report_error_(b, recordTypeDef_3(b, l + 1)) && r;
    r = p && report_error_(b, recordTypeDef_4(b, l + 1)) && r;
    r = p && recordTypeDef_5(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // extendsDecl?
  private static boolean recordTypeDef_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeDef_2")) return false;
    extendsDecl(b, l + 1);
    return true;
  }

  // metaDecl?
  private static boolean recordTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeDef_3")) return false;
    metaDecl(b, l + 1);
    return true;
  }

  // recordSupplementsDecl?
  private static boolean recordTypeDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeDef_4")) return false;
    recordSupplementsDecl(b, l + 1);
    return true;
  }

  // recordTypeBody?
  private static boolean recordTypeDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeDef_5")) return false;
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
  // '.' '*'
  public static boolean starImportSuffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "starImportSuffix")) return false;
    if (!nextTokenIs(b, S_DOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_DOT);
    r = r && consumeToken(b, S_STAR);
    exit_section_(b, m, S_STAR_IMPORT_SUFFIX, r);
    return r;
  }

  /* ********************************************************** */
  // 'supplement' combinedFqns (',' combinedFqns)* 'with' fqnTypeRef
  public static boolean supplementDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementDef")) return false;
    if (!nextTokenIs(b, S_SUPPLEMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_SUPPLEMENT_DEF, null);
    r = consumeToken(b, S_SUPPLEMENT);
    p = r; // pin = 1
    r = r && report_error_(b, combinedFqns(b, l + 1));
    r = p && report_error_(b, supplementDef_2(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, S_WITH)) && r;
    r = p && fqnTypeRef(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (',' combinedFqns)*
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

  // ',' combinedFqns
  private static boolean supplementDef_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementDef_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COMMA);
    r = r && combinedFqns(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' tagBodyPart* '}'
  static boolean tagBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tagBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, tagBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // tagBodyPart*
  private static boolean tagBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tagBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!tagBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tagBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // customParam
  static boolean tagBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tagBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // '[' fqnTypeRef defaultOverride? ']'
  public static boolean tagCommonType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tagCommonType")) return false;
    if (!nextTokenIs(b, S_BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_BRACKET_LEFT);
    r = r && fqnTypeRef(b, l + 1);
    r = r && tagCommonType_2(b, l + 1);
    r = r && consumeToken(b, S_BRACKET_RIGHT);
    exit_section_(b, m, S_TAG_COMMON_TYPE, r);
    return r;
  }

  // defaultOverride?
  private static boolean tagCommonType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tagCommonType_2")) return false;
    defaultOverride(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // id ':' typeRef defaultOverride? tagBody?
  public static boolean tagDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tagDecl")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_TAG_DECL, null);
    r = consumeToken(b, S_ID);
    r = r && consumeToken(b, S_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, typeRef(b, l + 1));
    r = p && report_error_(b, tagDecl_3(b, l + 1)) && r;
    r = p && tagDecl_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // defaultOverride?
  private static boolean tagDecl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tagDecl_3")) return false;
    defaultOverride(b, l + 1);
    return true;
  }

  // tagBody?
  private static boolean tagDecl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tagDecl_4")) return false;
    tagBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // multiTypeDef | recordTypeDef | unionTypeDef | mapTypeDef | listTypeDef |
  //             primitiveTypeDef | enumTypeDef
  public static boolean typeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, S_TYPE_DEF, "<type definition>");
    r = multiTypeDef(b, l + 1);
    if (!r) r = recordTypeDef(b, l + 1);
    if (!r) r = unionTypeDef(b, l + 1);
    if (!r) r = mapTypeDef(b, l + 1);
    if (!r) r = listTypeDef(b, l + 1);
    if (!r) r = primitiveTypeDef(b, l + 1);
    if (!r) r = enumTypeDef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // id
  static boolean typeName(PsiBuilder b, int l) {
    return consumeToken(b, S_ID);
  }

  /* ********************************************************** */
  // fqnTypeRef | anonList | anonMap | anonUnion
  public static boolean typeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_TYPE_REF, "<type>");
    r = fqnTypeRef(b, l + 1);
    if (!r) r = anonList(b, l + 1);
    if (!r) r = anonMap(b, l + 1);
    if (!r) r = anonUnion(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // tagDecl | customParam
  static boolean unionBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unionBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = tagDecl(b, l + 1);
    if (!r) r = customParam(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // '{' unionBodyPart* '}'
  public static boolean unionTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unionTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_UNION_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, unionTypeBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // unionBodyPart*
  private static boolean unionTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unionTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!unionBodyPart(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unionTypeBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // 'union' tagCommonType? typeName metaDecl? unionTypeBody
  public static boolean unionTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unionTypeDef")) return false;
    if (!nextTokenIs(b, S_UNION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_UNION_TYPE_DEF, null);
    r = consumeToken(b, S_UNION);
    p = r; // pin = 1
    r = r && report_error_(b, unionTypeDef_1(b, l + 1));
    r = p && report_error_(b, typeName(b, l + 1)) && r;
    r = p && report_error_(b, unionTypeDef_3(b, l + 1)) && r;
    r = p && unionTypeBody(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // tagCommonType?
  private static boolean unionTypeDef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unionTypeDef_1")) return false;
    tagCommonType(b, l + 1);
    return true;
  }

  // metaDecl?
  private static boolean unionTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unionTypeDef_3")) return false;
    metaDecl(b, l + 1);
    return true;
  }

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
