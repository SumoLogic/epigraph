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
package ws.epigraph.schema.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static ws.epigraph.schema.lexer.SchemaElementTypes.*;
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
    if (t == S_ANNOTATION) {
      r = annotation(b, 0);
    }
    else if (t == S_ANON_LIST) {
      r = anonList(b, 0);
    }
    else if (t == S_ANON_MAP) {
      r = anonMap(b, 0);
    }
    else if (t == S_CREATE_OPERATION_BODY_PART) {
      r = createOperationBodyPart(b, 0);
    }
    else if (t == S_CREATE_OPERATION_DEF) {
      r = createOperationDef(b, 0);
    }
    else if (t == S_CUSTOM_OPERATION_BODY_PART) {
      r = customOperationBodyPart(b, 0);
    }
    else if (t == S_CUSTOM_OPERATION_DEF) {
      r = customOperationDef(b, 0);
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
    else if (t == S_DEFS) {
      r = defs(b, 0);
    }
    else if (t == S_DELETE_OPERATION_BODY_PART) {
      r = deleteOperationBodyPart(b, 0);
    }
    else if (t == S_DELETE_OPERATION_DEF) {
      r = deleteOperationDef(b, 0);
    }
    else if (t == S_DELETE_PROJECTION) {
      r = deleteProjection(b, 0);
    }
    else if (t == S_DELETE_PROJECTION_DEF) {
      r = deleteProjectionDef(b, 0);
    }
    else if (t == S_ENTITY_TAG_DECL) {
      r = entityTagDecl(b, 0);
    }
    else if (t == S_ENTITY_TAG_REF) {
      r = entityTagRef(b, 0);
    }
    else if (t == S_ENTITY_TYPE_BODY) {
      r = entityTypeBody(b, 0);
    }
    else if (t == S_ENTITY_TYPE_DEF) {
      r = entityTypeDef(b, 0);
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
    else if (t == S_IMPORT_STATEMENT) {
      r = importStatement(b, 0);
    }
    else if (t == S_IMPORTS) {
      r = imports(b, 0);
    }
    else if (t == S_INPUT_PROJECTION) {
      r = inputProjection(b, 0);
    }
    else if (t == S_INPUT_PROJECTION_DEF) {
      r = inputProjectionDef(b, 0);
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
    else if (t == S_OP_DEFAULT_VALUE) {
      r = opDefaultValue(b, 0);
    }
    else if (t == S_OP_ENTITY_MULTI_TAIL) {
      r = opEntityMultiTail(b, 0);
    }
    else if (t == S_OP_ENTITY_POLYMORPHIC_TAIL) {
      r = opEntityPolymorphicTail(b, 0);
    }
    else if (t == S_OP_ENTITY_PROJECTION) {
      r = opEntityProjection(b, 0);
    }
    else if (t == S_OP_ENTITY_PROJECTION_REF) {
      r = opEntityProjectionRef(b, 0);
    }
    else if (t == S_OP_ENTITY_TAIL_ITEM) {
      r = opEntityTailItem(b, 0);
    }
    else if (t == S_OP_FIELD_PATH) {
      r = opFieldPath(b, 0);
    }
    else if (t == S_OP_FIELD_PATH_ENTRY) {
      r = opFieldPathEntry(b, 0);
    }
    else if (t == S_OP_FIELD_PROJECTION) {
      r = opFieldProjection(b, 0);
    }
    else if (t == S_OP_FIELD_PROJECTION_ENTRY) {
      r = opFieldProjectionEntry(b, 0);
    }
    else if (t == S_OP_KEY_PROJECTION) {
      r = opKeyProjection(b, 0);
    }
    else if (t == S_OP_KEY_SPEC) {
      r = opKeySpec(b, 0);
    }
    else if (t == S_OP_KEY_SPEC_PART) {
      r = opKeySpecPart(b, 0);
    }
    else if (t == S_OP_LIST_MODEL_PROJECTION) {
      r = opListModelProjection(b, 0);
    }
    else if (t == S_OP_MAP_MODEL_PATH) {
      r = opMapModelPath(b, 0);
    }
    else if (t == S_OP_MAP_MODEL_PROJECTION) {
      r = opMapModelProjection(b, 0);
    }
    else if (t == S_OP_MODEL_META) {
      r = opModelMeta(b, 0);
    }
    else if (t == S_OP_MODEL_MULTI_TAIL) {
      r = opModelMultiTail(b, 0);
    }
    else if (t == S_OP_MODEL_PATH) {
      r = opModelPath(b, 0);
    }
    else if (t == S_OP_MODEL_PATH_PROPERTY) {
      r = opModelPathProperty(b, 0);
    }
    else if (t == S_OP_MODEL_POLYMORPHIC_TAIL) {
      r = opModelPolymorphicTail(b, 0);
    }
    else if (t == S_OP_MODEL_PROJECTION) {
      r = opModelProjection(b, 0);
    }
    else if (t == S_OP_MODEL_PROJECTION_REF) {
      r = opModelProjectionRef(b, 0);
    }
    else if (t == S_OP_MODEL_PROPERTY) {
      r = opModelProperty(b, 0);
    }
    else if (t == S_OP_MODEL_TAIL_ITEM) {
      r = opModelTailItem(b, 0);
    }
    else if (t == S_OP_MULTI_TAG_PROJECTION) {
      r = opMultiTagProjection(b, 0);
    }
    else if (t == S_OP_MULTI_TAG_PROJECTION_ITEM) {
      r = opMultiTagProjectionItem(b, 0);
    }
    else if (t == S_OP_NAMED_ENTITY_PROJECTION) {
      r = opNamedEntityProjection(b, 0);
    }
    else if (t == S_OP_NAMED_MODEL_PROJECTION) {
      r = opNamedModelProjection(b, 0);
    }
    else if (t == S_OP_PARAM) {
      r = opParam(b, 0);
    }
    else if (t == S_OP_PATH_KEY_PROJECTION) {
      r = opPathKeyProjection(b, 0);
    }
    else if (t == S_OP_PATH_KEY_PROJECTION_BODY) {
      r = opPathKeyProjectionBody(b, 0);
    }
    else if (t == S_OP_PATH_KEY_PROJECTION_PART) {
      r = opPathKeyProjectionPart(b, 0);
    }
    else if (t == S_OP_RECORD_MODEL_PATH) {
      r = opRecordModelPath(b, 0);
    }
    else if (t == S_OP_RECORD_MODEL_PROJECTION) {
      r = opRecordModelProjection(b, 0);
    }
    else if (t == S_OP_SINGLE_TAG_PROJECTION) {
      r = opSingleTagProjection(b, 0);
    }
    else if (t == S_OP_UNNAMED_ENTITY_PROJECTION) {
      r = opUnnamedEntityProjection(b, 0);
    }
    else if (t == S_OP_UNNAMED_MODEL_PROJECTION) {
      r = opUnnamedModelProjection(b, 0);
    }
    else if (t == S_OP_UNNAMED_OR_REF_ENTITY_PROJECTION) {
      r = opUnnamedOrRefEntityProjection(b, 0);
    }
    else if (t == S_OP_UNNAMED_OR_REF_MODEL_PROJECTION) {
      r = opUnnamedOrRefModelProjection(b, 0);
    }
    else if (t == S_OP_VAR_PATH) {
      r = opVarPath(b, 0);
    }
    else if (t == S_OPERATION_DEF) {
      r = operationDef(b, 0);
    }
    else if (t == S_OPERATION_INPUT_TYPE) {
      r = operationInputType(b, 0);
    }
    else if (t == S_OPERATION_METHOD) {
      r = operationMethod(b, 0);
    }
    else if (t == S_OPERATION_NAME) {
      r = operationName(b, 0);
    }
    else if (t == S_OPERATION_OUTPUT_TYPE) {
      r = operationOutputType(b, 0);
    }
    else if (t == S_OPERATION_PATH) {
      r = operationPath(b, 0);
    }
    else if (t == S_OUTPUT_PROJECTION) {
      r = outputProjection(b, 0);
    }
    else if (t == S_OUTPUT_PROJECTION_DEF) {
      r = outputProjectionDef(b, 0);
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
    else if (t == S_PROJECTION_DEF) {
      r = projectionDef(b, 0);
    }
    else if (t == S_QID) {
      r = qid(b, 0);
    }
    else if (t == S_QN) {
      r = qn(b, 0);
    }
    else if (t == S_QN_SEGMENT) {
      r = qnSegment(b, 0);
    }
    else if (t == S_QN_TYPE_REF) {
      r = qnTypeRef(b, 0);
    }
    else if (t == S_READ_OPERATION_BODY_PART) {
      r = readOperationBodyPart(b, 0);
    }
    else if (t == S_READ_OPERATION_DEF) {
      r = readOperationDef(b, 0);
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
    else if (t == S_RESOURCE_DEF) {
      r = resourceDef(b, 0);
    }
    else if (t == S_RESOURCE_NAME) {
      r = resourceName(b, 0);
    }
    else if (t == S_RESOURCE_TYPE) {
      r = resourceType(b, 0);
    }
    else if (t == S_RETRO_DECL) {
      r = retroDecl(b, 0);
    }
    else if (t == S_SUPPLEMENT_DEF) {
      r = supplementDef(b, 0);
    }
    else if (t == S_SUPPLEMENTS_DECL) {
      r = supplementsDecl(b, 0);
    }
    else if (t == S_TAG_NAME) {
      r = tagName(b, 0);
    }
    else if (t == S_TRANSFORMER_BODY_PART) {
      r = transformerBodyPart(b, 0);
    }
    else if (t == S_TRANSFORMER_DEF) {
      r = transformerDef(b, 0);
    }
    else if (t == S_TRANSFORMER_NAME) {
      r = transformerName(b, 0);
    }
    else if (t == S_TRANSFORMER_TYPE) {
      r = transformerType(b, 0);
    }
    else if (t == S_TYPE_DEF_WRAPPER) {
      r = typeDefWrapper(b, 0);
    }
    else if (t == S_TYPE_REF) {
      r = typeRef(b, 0);
    }
    else if (t == S_UPDATE_OPERATION_BODY_PART) {
      r = updateOperationBodyPart(b, 0);
    }
    else if (t == S_UPDATE_OPERATION_DEF) {
      r = updateOperationDef(b, 0);
    }
    else if (t == S_VALUE_TYPE_REF) {
      r = valueTypeRef(b, 0);
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
    create_token_set_(S_ANON_LIST, S_ANON_MAP, S_QN_TYPE_REF, S_TYPE_REF),
    create_token_set_(S_DATUM, S_ENUM_DATUM, S_LIST_DATUM, S_MAP_DATUM,
      S_NULL_DATUM, S_PRIMITIVE_DATUM, S_RECORD_DATUM),
  };

  /* ********************************************************** */
  // '@' qnTypeRef datum?
  public static boolean annotation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotation")) return false;
    if (!nextTokenIs(b, "<annotation>", S_AT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ANNOTATION, "<annotation>");
    r = consumeToken(b, S_AT);
    p = r; // pin = 1
    r = r && report_error_(b, qnTypeRef(b, l + 1));
    r = p && annotation_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // datum?
  private static boolean annotation_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotation_2")) return false;
    datum(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'list' '[' valueTypeRef ']'
  public static boolean anonList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "anonList")) return false;
    if (!nextTokenIs(b, S_LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ANON_LIST, null);
    r = consumeTokens(b, 1, S_LIST, S_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, valueTypeRef(b, l + 1));
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
    r = consumeTokens(b, 1, S_MAP, S_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, typeRef(b, l + 1));
    r = p && report_error_(b, consumeToken(b, S_COMMA)) && r;
    r = p && report_error_(b, valueTypeRef(b, l + 1)) && r;
    r = p && consumeToken(b, S_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' (createOperationBodyPart ','?)* '}'
  static boolean createOperationBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createOperationBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, createOperationBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // operationPath |
  //                             operationInputType |
  //                             inputProjection |
  //                             operationOutputType |
  //                             outputProjection |
  //                             annotation
  public static boolean createOperationBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createOperationBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_CREATE_OPERATION_BODY_PART, "<create operation body part>");
    r = operationPath(b, l + 1);
    if (!r) r = operationInputType(b, l + 1);
    if (!r) r = inputProjection(b, l + 1);
    if (!r) r = operationOutputType(b, l + 1);
    if (!r) r = outputProjection(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, operationBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'create' operationName? createOperationBody
  public static boolean createOperationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createOperationDef")) return false;
    if (!nextTokenIs(b, S_OP_CREATE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_CREATE_OPERATION_DEF, null);
    r = consumeToken(b, S_OP_CREATE);
    p = r; // pin = 1
    r = r && report_error_(b, createOperationDef_1(b, l + 1));
    r = p && createOperationBody(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // operationName?
  private static boolean createOperationDef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "createOperationDef_1")) return false;
    operationName(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (customOperationBodyPart ','?)* '}'
  static boolean customOperationBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customOperationBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, customOperationBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // operationMethod |
  //                             operationPath |
  //                             operationInputType |
  //                             inputProjection |
  //                             operationOutputType |
  //                             outputProjection |
  //                             annotation
  public static boolean customOperationBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customOperationBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_CUSTOM_OPERATION_BODY_PART, "<custom operation body part>");
    r = operationMethod(b, l + 1);
    if (!r) r = operationPath(b, l + 1);
    if (!r) r = operationInputType(b, l + 1);
    if (!r) r = inputProjection(b, l + 1);
    if (!r) r = operationOutputType(b, l + 1);
    if (!r) r = outputProjection(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, operationBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'custom' operationName customOperationBody
  public static boolean customOperationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customOperationDef")) return false;
    if (!nextTokenIs(b, S_OP_CUSTOM)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_CUSTOM_OPERATION_DEF, null);
    r = consumeToken(b, S_OP_CUSTOM);
    p = r; // pin = 1
    r = r && report_error_(b, operationName(b, l + 1));
    r = p && customOperationBody(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // dataTypeSpec? '<' dataEntry* '>'
  public static boolean data(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "data")) return false;
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
  // typeRef
  static boolean dataTypeSpec(PsiBuilder b, int l) {
    return typeRef(b, l + 1);
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
  // ! ( '#' | qid | primitiveDatum | '}' | ')' | '>' | ']' | 'abstract' | 'override' | ',' )
  static boolean dataValueRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !dataValueRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '#' | qid | primitiveDatum | '}' | ')' | '>' | ']' | 'abstract' | 'override' | ','
  private static boolean dataValueRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataValueRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_HASH);
    if (!r) r = qid(b, l + 1);
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
  // typeDefWrapper | supplementDef | resourceDef | transformerDef | projectionDef
  static boolean def(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "def")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = typeDefWrapper(b, l + 1);
    if (!r) r = supplementDef(b, l + 1);
    if (!r) r = resourceDef(b, l + 1);
    if (!r) r = transformerDef(b, l + 1);
    if (!r) r = projectionDef(b, l + 1);
    exit_section_(b, l, m, r, false, defRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // ! ('import' | 'namespace' | 'abstract' | 'record' | ',' | '}' |
  //                            'map' | 'list' | 'entity' | 'enum' | 'supplement'|
  //                            'string' | 'integer' | 'long' | 'double' | 'boolean' | 'resource' |
  //                            'transformer' |
  //                            'outputProjection' | 'inputProjection' | 'deleteProjection' )
  static boolean defRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !defRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'import' | 'namespace' | 'abstract' | 'record' | ',' | '}' |
  //                            'map' | 'list' | 'entity' | 'enum' | 'supplement'|
  //                            'string' | 'integer' | 'long' | 'double' | 'boolean' | 'resource' |
  //                            'transformer' |
  //                            'outputProjection' | 'inputProjection' | 'deleteProjection'
  private static boolean defRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_IMPORT);
    if (!r) r = consumeToken(b, S_NAMESPACE);
    if (!r) r = consumeToken(b, S_ABSTRACT);
    if (!r) r = consumeToken(b, S_RECORD);
    if (!r) r = consumeToken(b, S_COMMA);
    if (!r) r = consumeToken(b, S_CURLY_RIGHT);
    if (!r) r = consumeToken(b, S_MAP);
    if (!r) r = consumeToken(b, S_LIST);
    if (!r) r = consumeToken(b, S_ENTITY);
    if (!r) r = consumeToken(b, S_ENUM);
    if (!r) r = consumeToken(b, S_SUPPLEMENT);
    if (!r) r = consumeToken(b, S_STRING_T);
    if (!r) r = consumeToken(b, S_INTEGER_T);
    if (!r) r = consumeToken(b, S_LONG_T);
    if (!r) r = consumeToken(b, S_DOUBLE_T);
    if (!r) r = consumeToken(b, S_BOOLEAN_T);
    if (!r) r = consumeToken(b, S_RESOURCE);
    if (!r) r = consumeToken(b, S_TRANSFORMER);
    if (!r) r = consumeToken(b, S_OUTPUT_PROJ);
    if (!r) r = consumeToken(b, S_INPUT_PROJ);
    if (!r) r = consumeToken(b, S_DELETE_PROJ);
    exit_section_(b, m, null, r);
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
  // '{' (deleteOperationBodyPart ','?)* '}'
  static boolean deleteOperationBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteOperationBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, deleteOperationBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // operationPath |
  //                             deleteProjection |
  //                             operationOutputType |
  //                             outputProjection |
  //                             annotation
  public static boolean deleteOperationBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteOperationBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_DELETE_OPERATION_BODY_PART, "<delete operation body part>");
    r = operationPath(b, l + 1);
    if (!r) r = deleteProjection(b, l + 1);
    if (!r) r = operationOutputType(b, l + 1);
    if (!r) r = outputProjection(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, operationBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'delete' operationName? deleteOperationBody
  public static boolean deleteOperationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteOperationDef")) return false;
    if (!nextTokenIs(b, S_OP_DELETE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_DELETE_OPERATION_DEF, null);
    r = consumeToken(b, S_OP_DELETE);
    p = r; // pin = 1
    r = r && report_error_(b, deleteOperationDef_1(b, l + 1));
    r = p && deleteOperationBody(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // operationName?
  private static boolean deleteOperationDef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteOperationDef_1")) return false;
    operationName(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'deleteProjection' '+'? opFieldProjection
  public static boolean deleteProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteProjection")) return false;
    if (!nextTokenIs(b, S_DELETE_PROJ)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_DELETE_PROJECTION, null);
    r = consumeToken(b, S_DELETE_PROJ);
    p = r; // pin = 1
    r = r && report_error_(b, deleteProjection_1(b, l + 1));
    r = p && opFieldProjection(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean deleteProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteProjection_1")) return false;
    consumeToken(b, S_PLUS);
    return true;
  }

  /* ********************************************************** */
  // 'deleteProjection' qid ':' typeRef '=' '+'? opUnnamedOrRefEntityProjection
  public static boolean deleteProjectionDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteProjectionDef")) return false;
    if (!nextTokenIs(b, S_DELETE_PROJ)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_DELETE_PROJECTION_DEF, null);
    r = consumeToken(b, S_DELETE_PROJ);
    p = r; // pin = 1
    r = r && report_error_(b, qid(b, l + 1));
    r = p && report_error_(b, consumeToken(b, S_COLON)) && r;
    r = p && report_error_(b, typeRef(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, S_EQ)) && r;
    r = p && report_error_(b, deleteProjectionDef_5(b, l + 1)) && r;
    r = p && opUnnamedOrRefEntityProjection(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean deleteProjectionDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteProjectionDef_5")) return false;
    consumeToken(b, S_PLUS);
    return true;
  }

  /* ********************************************************** */
  // entityTagDecl | annotation
  static boolean entityBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = entityTagDecl(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // '{' (entityMemberBodyPart ','?)* '}'
  static boolean entityMemberBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityMemberBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, entityMemberBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (entityMemberBodyPart ','?)*
  private static boolean entityMemberBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityMemberBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!entityMemberBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "entityMemberBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // entityMemberBodyPart ','?
  private static boolean entityMemberBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityMemberBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = entityMemberBodyPart(b, l + 1);
    r = r && entityMemberBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean entityMemberBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityMemberBody_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // annotation
  static boolean entityMemberBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityMemberBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // typeMemberModifiers qid ':' typeRef entityMemberBody?
  public static boolean entityTagDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityTagDecl")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ENTITY_TAG_DECL, "<entity tag decl>");
    r = typeMemberModifiers(b, l + 1);
    r = r && qid(b, l + 1);
    r = r && consumeToken(b, S_COLON);
    p = r; // pin = 3
    r = r && report_error_(b, typeRef(b, l + 1));
    r = p && entityTagDecl_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // entityMemberBody?
  private static boolean entityTagDecl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityTagDecl_4")) return false;
    entityMemberBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // qid
  public static boolean entityTagRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityTagRef")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, S_ENTITY_TAG_REF, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (entityBodyPart ','?)* '}' {
  // //  recoverWhile = defRecover
  // }
  public static boolean entityTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ENTITY_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, entityTypeBody_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, S_CURLY_RIGHT)) && r;
    r = p && entityTypeBody_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (entityBodyPart ','?)*
  private static boolean entityTypeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityTypeBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!entityTypeBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "entityTypeBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // entityBodyPart ','?
  private static boolean entityTypeBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityTypeBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = entityBodyPart(b, l + 1);
    r = r && entityTypeBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean entityTypeBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityTypeBody_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  // {
  // //  recoverWhile = defRecover
  // }
  private static boolean entityTypeBody_3(PsiBuilder b, int l) {
    return true;
  }

  /* ********************************************************** */
  // typeDefModifiers 'entity' typeName extendsDecl? supplementsDecl? entityTypeBody?
  public static boolean entityTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityTypeDef")) return false;
    if (!nextTokenIs(b, "<entity type def>", S_ABSTRACT, S_ENTITY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ENTITY_TYPE_DEF, "<entity type def>");
    r = typeDefModifiers(b, l + 1);
    r = r && consumeToken(b, S_ENTITY);
    p = r; // pin = 2
    r = r && report_error_(b, typeName(b, l + 1));
    r = p && report_error_(b, entityTypeDef_3(b, l + 1)) && r;
    r = p && report_error_(b, entityTypeDef_4(b, l + 1)) && r;
    r = p && entityTypeDef_5(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // extendsDecl?
  private static boolean entityTypeDef_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityTypeDef_3")) return false;
    extendsDecl(b, l + 1);
    return true;
  }

  // supplementsDecl?
  private static boolean entityTypeDef_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityTypeDef_4")) return false;
    supplementsDecl(b, l + 1);
    return true;
  }

  // entityTypeBody?
  private static boolean entityTypeDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entityTypeDef_5")) return false;
    entityTypeBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '#' qid
  public static boolean enumDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumDatum")) return false;
    if (!nextTokenIs(b, S_HASH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ENUM_DATUM, null);
    r = consumeToken(b, S_HASH);
    p = r; // pin = 1
    r = r && qid(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' (enumMemberBodyPart ','?)* '}'
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
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // annotation
  static boolean enumMemberBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // qid ':' dataValue enumMemberBody?
  public static boolean enumMemberDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberDecl")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ENUM_MEMBER_DECL, null);
    r = qid(b, l + 1);
    r = r && consumeToken(b, S_COLON);
    p = r; // pin = 2
    r = r && report_error_(b, dataValue(b, l + 1));
    r = p && enumMemberDecl_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // enumMemberBody?
  private static boolean enumMemberDecl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumMemberDecl_3")) return false;
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
  // '{' (enumTypeBodyPart ','?)* '}' {
  // //  recoverWhile = defRecover
  // }
  public static boolean enumTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ENUM_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, enumTypeBody_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, S_CURLY_RIGHT)) && r;
    r = p && enumTypeBody_3(b, l + 1) && r;
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

  // {
  // //  recoverWhile = defRecover
  // }
  private static boolean enumTypeBody_3(PsiBuilder b, int l) {
    return true;
  }

  /* ********************************************************** */
  // annotation | enumMemberDecl
  static boolean enumTypeBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = annotation(b, l + 1);
    if (!r) r = enumMemberDecl(b, l + 1);
    exit_section_(b, l, m, r, false, enumPartRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'enum' '[' valueTypeRef ']' typeName metaDecl? enumTypeBody
  public static boolean enumTypeDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeDef")) return false;
    if (!nextTokenIs(b, S_ENUM)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_ENUM_TYPE_DEF, null);
    r = consumeTokens(b, 1, S_ENUM, S_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, valueTypeRef(b, l + 1));
    r = p && report_error_(b, consumeToken(b, S_BRACKET_RIGHT)) && r;
    r = p && report_error_(b, typeName(b, l + 1)) && r;
    r = p && report_error_(b, enumTypeDef_5(b, l + 1)) && r;
    r = p && enumTypeBody(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // metaDecl?
  private static boolean enumTypeDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enumTypeDef_5")) return false;
    metaDecl(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'extends' qnTypeRef (',' qnTypeRef)*
  public static boolean extendsDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendsDecl")) return false;
    if (!nextTokenIs(b, S_EXTENDS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_EXTENDS_DECL, null);
    r = consumeToken(b, S_EXTENDS);
    p = r; // pin = 1
    r = r && report_error_(b, qnTypeRef(b, l + 1));
    r = p && extendsDecl_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (',' qnTypeRef)*
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

  // ',' qnTypeRef
  private static boolean extendsDecl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendsDecl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COMMA);
    r = r && qnTypeRef(b, l + 1);
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
  // annotation
  static boolean fieldBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = annotation(b, l + 1);
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
  // namespaceDeclRecover
  static boolean importRecover(PsiBuilder b, int l) {
    return namespaceDeclRecover(b, l + 1);
  }

  /* ********************************************************** */
  // 'import' qn
  public static boolean importStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_IMPORT_STATEMENT, "<import statement>");
    r = consumeToken(b, S_IMPORT);
    p = r; // pin = 1
    r = r && qn(b, l + 1);
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
  // 'inputProjection' '+'? opFieldProjection
  public static boolean inputProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputProjection")) return false;
    if (!nextTokenIs(b, S_INPUT_PROJ)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_INPUT_PROJECTION, null);
    r = consumeToken(b, S_INPUT_PROJ);
    p = r; // pin = 1
    r = r && report_error_(b, inputProjection_1(b, l + 1));
    r = p && opFieldProjection(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean inputProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputProjection_1")) return false;
    consumeToken(b, S_PLUS);
    return true;
  }

  /* ********************************************************** */
  // 'inputProjection' qid ':' typeRef '=' '+'? opUnnamedOrRefEntityProjection
  public static boolean inputProjectionDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputProjectionDef")) return false;
    if (!nextTokenIs(b, S_INPUT_PROJ)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_INPUT_PROJECTION_DEF, null);
    r = consumeToken(b, S_INPUT_PROJ);
    p = r; // pin = 1
    r = r && report_error_(b, qid(b, l + 1));
    r = p && report_error_(b, consumeToken(b, S_COLON)) && r;
    r = p && report_error_(b, typeRef(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, S_EQ)) && r;
    r = p && report_error_(b, inputProjectionDef_5(b, l + 1)) && r;
    r = p && opUnnamedOrRefEntityProjection(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean inputProjectionDef_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputProjectionDef_5")) return false;
    consumeToken(b, S_PLUS);
    return true;
  }

  /* ********************************************************** */
  // dataTypeSpec? '[' (dataValue ','?)* ']'
  public static boolean listDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listDatum")) return false;
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
  // '{' (listTypeBodyPart ','?)* '}' {
  // //  recoverWhile = defRecover
  // }
  public static boolean listTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_LIST_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, listTypeBody_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, S_CURLY_RIGHT)) && r;
    r = p && listTypeBody_3(b, l + 1) && r;
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

  // {
  // //  recoverWhile = defRecover
  // }
  private static boolean listTypeBody_3(PsiBuilder b, int l) {
    return true;
  }

  /* ********************************************************** */
  // annotation
  static boolean listTypeBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listTypeBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = annotation(b, l + 1);
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
  // '{' (mapTypeBodyPart ','?)* '}' {
  // //  recoverWhile = defRecover
  // }
  public static boolean mapTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_MAP_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, mapTypeBody_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, S_CURLY_RIGHT)) && r;
    r = p && mapTypeBody_3(b, l + 1) && r;
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

  // {
  // //  recoverWhile = defRecover
  // }
  private static boolean mapTypeBody_3(PsiBuilder b, int l) {
    return true;
  }

  /* ********************************************************** */
  // annotation
  static boolean mapTypeBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapTypeBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = annotation(b, l + 1);
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
  // 'meta' qnTypeRef
  public static boolean metaDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaDecl")) return false;
    if (!nextTokenIs(b, S_META)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_META_DECL, null);
    r = consumeToken(b, S_META);
    p = r; // pin = 1
    r = r && qnTypeRef(b, l + 1);
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
  // annotation
  static boolean namespaceBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'namespace' qn namespaceBody?
  public static boolean namespaceDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDecl")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_NAMESPACE_DECL, "<namespace decl>");
    r = consumeToken(b, S_NAMESPACE);
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
  // ! ('import' | 'namespace' | 'abstract' | 'record' | ',' |
  //                            'map' | 'list' | 'entity' | 'enum' | 'supplement'|
  //                            'string' | 'integer' | 'long' | 'double' | 'boolean' | 'resource' |
  //                            'transformer' |
  //                            'outputProjection' | 'inputProjection' | 'deleteProjection' )
  static boolean namespaceDeclRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDeclRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !namespaceDeclRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'import' | 'namespace' | 'abstract' | 'record' | ',' |
  //                            'map' | 'list' | 'entity' | 'enum' | 'supplement'|
  //                            'string' | 'integer' | 'long' | 'double' | 'boolean' | 'resource' |
  //                            'transformer' |
  //                            'outputProjection' | 'inputProjection' | 'deleteProjection'
  private static boolean namespaceDeclRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceDeclRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_IMPORT);
    if (!r) r = consumeToken(b, S_NAMESPACE);
    if (!r) r = consumeToken(b, S_ABSTRACT);
    if (!r) r = consumeToken(b, S_RECORD);
    if (!r) r = consumeToken(b, S_COMMA);
    if (!r) r = consumeToken(b, S_MAP);
    if (!r) r = consumeToken(b, S_LIST);
    if (!r) r = consumeToken(b, S_ENTITY);
    if (!r) r = consumeToken(b, S_ENUM);
    if (!r) r = consumeToken(b, S_SUPPLEMENT);
    if (!r) r = consumeToken(b, S_STRING_T);
    if (!r) r = consumeToken(b, S_INTEGER_T);
    if (!r) r = consumeToken(b, S_LONG_T);
    if (!r) r = consumeToken(b, S_DOUBLE_T);
    if (!r) r = consumeToken(b, S_BOOLEAN_T);
    if (!r) r = consumeToken(b, S_RESOURCE);
    if (!r) r = consumeToken(b, S_TRANSFORMER);
    if (!r) r = consumeToken(b, S_OUTPUT_PROJ);
    if (!r) r = consumeToken(b, S_INPUT_PROJ);
    if (!r) r = consumeToken(b, S_DELETE_PROJ);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (dataTypeSpec '@')? 'null'
  public static boolean nullDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nullDatum")) return false;
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
  // '(' opEntityProjection ')'
  static boolean opBracedEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opBracedEntityProjection")) return false;
    if (!nextTokenIs(b, S_PAREN_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_PAREN_LEFT);
    r = r && opEntityProjection(b, l + 1);
    r = r && consumeToken(b, S_PAREN_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'default' ':' datum
  public static boolean opDefaultValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opDefaultValue")) return false;
    if (!nextTokenIs(b, S_DEFAULT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_DEFAULT_VALUE, null);
    r = consumeTokens(b, 1, S_DEFAULT, S_COLON);
    p = r; // pin = 1
    r = r && datum(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '(' (opEntityTailItem ','?)* ')'
  public static boolean opEntityMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opEntityMultiTail")) return false;
    if (!nextTokenIs(b, S_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_ENTITY_MULTI_TAIL, null);
    r = consumeToken(b, S_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opEntityMultiTail_1(b, l + 1));
    r = p && consumeToken(b, S_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opEntityTailItem ','?)*
  private static boolean opEntityMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opEntityMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opEntityMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opEntityMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opEntityTailItem ','?
  private static boolean opEntityMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opEntityMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opEntityTailItem(b, l + 1);
    r = r && opEntityMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opEntityMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opEntityMultiTail_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ':' '~' ( opEntityTailItem | opEntityMultiTail )
  public static boolean opEntityPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opEntityPolymorphicTail")) return false;
    if (!nextTokenIs(b, S_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_ENTITY_POLYMORPHIC_TAIL, null);
    r = consumeTokens(b, 2, S_COLON, S_TILDA);
    p = r; // pin = 2
    r = r && opEntityPolymorphicTail_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // opEntityTailItem | opEntityMultiTail
  private static boolean opEntityPolymorphicTail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opEntityPolymorphicTail_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opEntityTailItem(b, l + 1);
    if (!r) r = opEntityMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // opNamedEntityProjection | opUnnamedOrRefEntityProjection
  public static boolean opEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opEntityProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_ENTITY_PROJECTION, "<op entity projection>");
    r = opNamedEntityProjection(b, l + 1);
    if (!r) r = opUnnamedOrRefEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '$' qid
  public static boolean opEntityProjectionRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opEntityProjectionRef")) return false;
    if (!nextTokenIs(b, S_DOLLAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_ENTITY_PROJECTION_REF, null);
    r = consumeToken(b, S_DOLLAR);
    p = r; // pin = 1
    r = r && qid(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // typeRef opEntityProjection
  public static boolean opEntityTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opEntityTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_ENTITY_TAIL_ITEM, "<op entity tail item>");
    r = typeRef(b, l + 1);
    r = r && opEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // opVarPath
  public static boolean opFieldPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opFieldPath")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_FIELD_PATH, "<op field path>");
    r = opVarPath(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // qid opFieldPath
  public static boolean opFieldPathEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opFieldPathEntry")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    r = r && opFieldPath(b, l + 1);
    exit_section_(b, m, S_OP_FIELD_PATH_ENTRY, r);
    return r;
  }

  /* ********************************************************** */
  // opEntityProjection
  public static boolean opFieldProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opFieldProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_FIELD_PROJECTION, "<op field projection>");
    r = opEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '+'? qid opFieldProjection
  public static boolean opFieldProjectionEntry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opFieldProjectionEntry")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_FIELD_PROJECTION_ENTRY, "<op field projection entry>");
    r = opFieldProjectionEntry_0(b, l + 1);
    r = r && qid(b, l + 1);
    p = r; // pin = 2
    r = r && opFieldProjection(b, l + 1);
    exit_section_(b, l, m, r, p, recordModelProjectionRecover_parser_);
    return r || p;
  }

  // '+'?
  private static boolean opFieldProjectionEntry_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opFieldProjectionEntry_0")) return false;
    consumeToken(b, S_PLUS);
    return true;
  }

  /* ********************************************************** */
  // 'projection' ':' opModelProjection
  public static boolean opKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeyProjection")) return false;
    if (!nextTokenIs(b, S_PROJECTION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_KEY_PROJECTION, null);
    r = consumeTokens(b, 1, S_PROJECTION, S_COLON);
    p = r; // pin = 1
    r = r && opModelProjection(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '[' opKeySpecInt ']'
  public static boolean opKeySpec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpec")) return false;
    if (!nextTokenIs(b, S_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_KEY_SPEC, null);
    r = consumeToken(b, S_BRACKET_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opKeySpecInt(b, l + 1));
    r = p && consumeToken(b, S_BRACKET_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ('required' ','?| 'forbidden' ','?)? (opKeySpecPart ','?)*
  static boolean opKeySpecInt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecInt")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opKeySpecInt_0(b, l + 1);
    r = r && opKeySpecInt_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('required' ','?| 'forbidden' ','?)?
  private static boolean opKeySpecInt_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecInt_0")) return false;
    opKeySpecInt_0_0(b, l + 1);
    return true;
  }

  // 'required' ','?| 'forbidden' ','?
  private static boolean opKeySpecInt_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecInt_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opKeySpecInt_0_0_0(b, l + 1);
    if (!r) r = opKeySpecInt_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'required' ','?
  private static boolean opKeySpecInt_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecInt_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_REQUIRED);
    r = r && opKeySpecInt_0_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opKeySpecInt_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecInt_0_0_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  // 'forbidden' ','?
  private static boolean opKeySpecInt_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecInt_0_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_FORBIDDEN);
    r = r && opKeySpecInt_0_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opKeySpecInt_0_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecInt_0_0_1_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  // (opKeySpecPart ','?)*
  private static boolean opKeySpecInt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecInt_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opKeySpecInt_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opKeySpecInt_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opKeySpecPart ','?
  private static boolean opKeySpecInt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecInt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opKeySpecPart(b, l + 1);
    r = r && opKeySpecInt_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opKeySpecInt_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecInt_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opKeyProjection | opParam | annotation
  public static boolean opKeySpecPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_KEY_SPEC_PART, "<op key spec part>");
    r = opKeyProjection(b, l + 1);
    if (!r) r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, opKeySpecRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // ! ( ']' | ',' | ';' | 'projection' )
  static boolean opKeySpecRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !opKeySpecRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ']' | ',' | ';' | 'projection'
  private static boolean opKeySpecRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opKeySpecRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_BRACKET_RIGHT);
    if (!r) r = consumeToken(b, S_COMMA);
    if (!r) r = consumeToken(b, S_SEMICOLON);
    if (!r) r = consumeToken(b, S_PROJECTION);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '*' '+'? ( opBracedEntityProjection | opEntityProjection )
  public static boolean opListModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opListModelProjection")) return false;
    if (!nextTokenIs(b, S_STAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_LIST_MODEL_PROJECTION, null);
    r = consumeToken(b, S_STAR);
    p = r; // pin = 1
    r = r && report_error_(b, opListModelProjection_1(b, l + 1));
    r = p && opListModelProjection_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean opListModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opListModelProjection_1")) return false;
    consumeToken(b, S_PLUS);
    return true;
  }

  // opBracedEntityProjection | opEntityProjection
  private static boolean opListModelProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opListModelProjection_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opBracedEntityProjection(b, l + 1);
    if (!r) r = opEntityProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '/' opPathKeyProjection opVarPath
  public static boolean opMapModelPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMapModelPath")) return false;
    if (!nextTokenIs(b, S_SLASH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_MAP_MODEL_PATH, null);
    r = consumeToken(b, S_SLASH);
    r = r && opPathKeyProjection(b, l + 1);
    p = r; // pin = 2
    r = r && opVarPath(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // opKeySpec '+'? ( opBracedEntityProjection | opEntityProjection )
  public static boolean opMapModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMapModelProjection")) return false;
    if (!nextTokenIs(b, S_BRACKET_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_MAP_MODEL_PROJECTION, null);
    r = opKeySpec(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, opMapModelProjection_1(b, l + 1));
    r = p && opMapModelProjection_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean opMapModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMapModelProjection_1")) return false;
    consumeToken(b, S_PLUS);
    return true;
  }

  // opBracedEntityProjection | opEntityProjection
  private static boolean opMapModelProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMapModelProjection_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opBracedEntityProjection(b, l + 1);
    if (!r) r = opEntityProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'meta' ':' '+'? opModelProjection
  public static boolean opModelMeta(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelMeta")) return false;
    if (!nextTokenIs(b, S_META)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, S_META, S_COLON);
    r = r && opModelMeta_2(b, l + 1);
    r = r && opModelProjection(b, l + 1);
    exit_section_(b, m, S_OP_MODEL_META, r);
    return r;
  }

  // '+'?
  private static boolean opModelMeta_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelMeta_2")) return false;
    consumeToken(b, S_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '(' (opModelTailItem ','?)* ')'
  public static boolean opModelMultiTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelMultiTail")) return false;
    if (!nextTokenIs(b, S_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_MODEL_MULTI_TAIL, null);
    r = consumeToken(b, S_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opModelMultiTail_1(b, l + 1));
    r = p && consumeToken(b, S_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opModelTailItem ','?)*
  private static boolean opModelMultiTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelMultiTail_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opModelMultiTail_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opModelMultiTail_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opModelTailItem ','?
  private static boolean opModelMultiTail_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelMultiTail_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opModelTailItem(b, l + 1);
    r = r && opModelMultiTail_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opModelMultiTail_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelMultiTail_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ( opRecordModelPath
  //                 | opMapModelPath
  //                 )?
  public static boolean opModelPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelPath")) return false;
    Marker m = enter_section_(b, l, _NONE_, S_OP_MODEL_PATH, "<op model path>");
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
    if (!nextTokenIs(b, "<op model path property>", S_SEMICOLON, S_AT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_MODEL_PATH_PROPERTY, "<op model path property>");
    r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '~' ( opModelTailItem | opModelMultiTail )
  public static boolean opModelPolymorphicTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelPolymorphicTail")) return false;
    if (!nextTokenIs(b, S_TILDA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_TILDA);
    r = r && opModelPolymorphicTail_1(b, l + 1);
    exit_section_(b, m, S_OP_MODEL_POLYMORPHIC_TAIL, r);
    return r;
  }

  // opModelTailItem | opModelMultiTail
  private static boolean opModelPolymorphicTail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelPolymorphicTail_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opModelTailItem(b, l + 1);
    if (!r) r = opModelMultiTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // opNamedModelProjection | opUnnamedOrRefModelProjection
  public static boolean opModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_MODEL_PROJECTION, "<op model projection>");
    r = opNamedModelProjection(b, l + 1);
    if (!r) r = opUnnamedOrRefModelProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{' (opModelProperty ','?)* '}'
  static boolean opModelProjectionProperties(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelProjectionProperties")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opModelProjectionProperties_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opModelProperty ','?)*
  private static boolean opModelProjectionProperties_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelProjectionProperties_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opModelProjectionProperties_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opModelProjectionProperties_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opModelProperty ','?
  private static boolean opModelProjectionProperties_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelProjectionProperties_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opModelProperty(b, l + 1);
    r = r && opModelProjectionProperties_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opModelProjectionProperties_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelProjectionProperties_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '$' qid
  public static boolean opModelProjectionRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelProjectionRef")) return false;
    if (!nextTokenIs(b, S_DOLLAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_MODEL_PROJECTION_REF, null);
    r = consumeToken(b, S_DOLLAR);
    p = r; // pin = 1
    r = r && qid(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // opDefaultValue | opParam | annotation | opModelMeta
  public static boolean opModelProperty(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelProperty")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_MODEL_PROPERTY, "<op model property>");
    r = opDefaultValue(b, l + 1);
    if (!r) r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    if (!r) r = opModelMeta(b, l + 1);
    exit_section_(b, l, m, r, false, opModelPropertyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // ! ( '}' | ',' | 'default' | ';' | '@' | 'meta' )
  static boolean opModelPropertyRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelPropertyRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !opModelPropertyRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}' | ',' | 'default' | ';' | '@' | 'meta'
  private static boolean opModelPropertyRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelPropertyRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_CURLY_RIGHT);
    if (!r) r = consumeToken(b, S_COMMA);
    if (!r) r = consumeToken(b, S_DEFAULT);
    if (!r) r = consumeToken(b, S_SEMICOLON);
    if (!r) r = consumeToken(b, S_AT);
    if (!r) r = consumeToken(b, S_META);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // typeRef opModelProjection
  public static boolean opModelTailItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opModelTailItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_MODEL_TAIL_ITEM, "<op model tail item>");
    r = typeRef(b, l + 1);
    r = r && opModelProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ':' '(' (opMultiTagProjectionItem ','?)* ')'
  public static boolean opMultiTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMultiTagProjection")) return false;
    if (!nextTokenIs(b, S_COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_MULTI_TAG_PROJECTION, null);
    r = consumeTokens(b, 2, S_COLON, S_PAREN_LEFT);
    p = r; // pin = 2
    r = r && report_error_(b, opMultiTagProjection_2(b, l + 1));
    r = p && consumeToken(b, S_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opMultiTagProjectionItem ','?)*
  private static boolean opMultiTagProjection_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMultiTagProjection_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opMultiTagProjection_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opMultiTagProjection_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opMultiTagProjectionItem ','?
  private static boolean opMultiTagProjection_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMultiTagProjection_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opMultiTagProjectionItem(b, l + 1);
    r = r && opMultiTagProjection_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opMultiTagProjection_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMultiTagProjection_2_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '+'? tagName opModelProjection
  public static boolean opMultiTagProjectionItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMultiTagProjectionItem")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_MULTI_TAG_PROJECTION_ITEM, "<op multi tag projection item>");
    r = opMultiTagProjectionItem_0(b, l + 1);
    r = r && tagName(b, l + 1);
    r = r && opModelProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+'?
  private static boolean opMultiTagProjectionItem_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opMultiTagProjectionItem_0")) return false;
    consumeToken(b, S_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '$' qid '=' opUnnamedOrRefEntityProjection
  public static boolean opNamedEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNamedEntityProjection")) return false;
    if (!nextTokenIs(b, S_DOLLAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_NAMED_ENTITY_PROJECTION, null);
    r = consumeToken(b, S_DOLLAR);
    r = r && qid(b, l + 1);
    r = r && consumeToken(b, S_EQ);
    p = r; // pin = 3
    r = r && opUnnamedOrRefEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '$' qid '=' opUnnamedOrRefModelProjection
  public static boolean opNamedModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opNamedModelProjection")) return false;
    if (!nextTokenIs(b, S_DOLLAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_NAMED_MODEL_PROJECTION, null);
    r = consumeToken(b, S_DOLLAR);
    r = r && qid(b, l + 1);
    r = r && consumeToken(b, S_EQ);
    p = r; // pin = 3
    r = r && opUnnamedOrRefModelProjection(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ';' '+'? qid ':' typeRef opModelProjection
  public static boolean opParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParam")) return false;
    if (!nextTokenIs(b, S_SEMICOLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_PARAM, null);
    r = consumeToken(b, S_SEMICOLON);
    p = r; // pin = 1
    r = r && report_error_(b, opParam_1(b, l + 1));
    r = p && report_error_(b, qid(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, S_COLON)) && r;
    r = p && report_error_(b, typeRef(b, l + 1)) && r;
    r = p && opModelProjection(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '+'?
  private static boolean opParam_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opParam_1")) return false;
    consumeToken(b, S_PLUS);
    return true;
  }

  /* ********************************************************** */
  // '.' opPathKeyProjectionBody?
  public static boolean opPathKeyProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjection")) return false;
    if (!nextTokenIs(b, S_DOT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_PATH_KEY_PROJECTION, null);
    r = consumeToken(b, S_DOT);
    p = r; // pin = 1
    r = r && opPathKeyProjection_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // opPathKeyProjectionBody?
  private static boolean opPathKeyProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjection_1")) return false;
    opPathKeyProjectionBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (opPathKeyProjectionPart ','?)*  '}'
  public static boolean opPathKeyProjectionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjectionBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_PATH_KEY_PROJECTION_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opPathKeyProjectionBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opPathKeyProjectionPart ','?)*
  private static boolean opPathKeyProjectionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjectionBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opPathKeyProjectionBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opPathKeyProjectionBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opPathKeyProjectionPart ','?
  private static boolean opPathKeyProjectionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjectionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opPathKeyProjectionPart(b, l + 1);
    r = r && opPathKeyProjectionBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opPathKeyProjectionBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjectionBody_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // opKeyProjection | opParam | annotation
  public static boolean opPathKeyProjectionPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opPathKeyProjectionPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_PATH_KEY_PROJECTION_PART, "<op path key projection part>");
    r = opKeyProjection(b, l + 1);
    if (!r) r = opParam(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // '/' opFieldPathEntry
  public static boolean opRecordModelPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opRecordModelPath")) return false;
    if (!nextTokenIs(b, S_SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_SLASH);
    r = r && opFieldPathEntry(b, l + 1);
    exit_section_(b, m, S_OP_RECORD_MODEL_PATH, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (opFieldProjectionEntry ','?)* ')'
  public static boolean opRecordModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opRecordModelProjection")) return false;
    if (!nextTokenIs(b, S_PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OP_RECORD_MODEL_PROJECTION, null);
    r = consumeToken(b, S_PAREN_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opRecordModelProjection_1(b, l + 1));
    r = p && consumeToken(b, S_PAREN_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opFieldProjectionEntry ','?)*
  private static boolean opRecordModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opRecordModelProjection_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opRecordModelProjection_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opRecordModelProjection_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opFieldProjectionEntry ','?
  private static boolean opRecordModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opRecordModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opFieldProjectionEntry(b, l + 1);
    r = r && opRecordModelProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opRecordModelProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opRecordModelProjection_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ( ( ':' '+'? tagName) | '+' )? opModelProjection
  public static boolean opSingleTagProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opSingleTagProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_SINGLE_TAG_PROJECTION, "<op single tag projection>");
    r = opSingleTagProjection_0(b, l + 1);
    r = r && opModelProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ( ':' '+'? tagName) | '+' )?
  private static boolean opSingleTagProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opSingleTagProjection_0")) return false;
    opSingleTagProjection_0_0(b, l + 1);
    return true;
  }

  // ( ':' '+'? tagName) | '+'
  private static boolean opSingleTagProjection_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opSingleTagProjection_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opSingleTagProjection_0_0_0(b, l + 1);
    if (!r) r = consumeToken(b, S_PLUS);
    exit_section_(b, m, null, r);
    return r;
  }

  // ':' '+'? tagName
  private static boolean opSingleTagProjection_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opSingleTagProjection_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COLON);
    r = r && opSingleTagProjection_0_0_0_1(b, l + 1);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'?
  private static boolean opSingleTagProjection_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opSingleTagProjection_0_0_0_1")) return false;
    consumeToken(b, S_PLUS);
    return true;
  }

  /* ********************************************************** */
  // ( opMultiTagProjection | opSingleTagProjection ) opEntityPolymorphicTail?
  public static boolean opUnnamedEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opUnnamedEntityProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_UNNAMED_ENTITY_PROJECTION, "<op unnamed entity projection>");
    r = opUnnamedEntityProjection_0(b, l + 1);
    r = r && opUnnamedEntityProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // opMultiTagProjection | opSingleTagProjection
  private static boolean opUnnamedEntityProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opUnnamedEntityProjection_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opMultiTagProjection(b, l + 1);
    if (!r) r = opSingleTagProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opEntityPolymorphicTail?
  private static boolean opUnnamedEntityProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opUnnamedEntityProjection_1")) return false;
    opEntityPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // opModelProjectionProperties ?
  //                                    ( ( opRecordModelProjection
  //                                      | opListModelProjection
  //                                      | opMapModelProjection
  //                                      ) opModelPolymorphicTail?
  //                                    )?
  public static boolean opUnnamedModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opUnnamedModelProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_UNNAMED_MODEL_PROJECTION, "<op unnamed model projection>");
    r = opUnnamedModelProjection_0(b, l + 1);
    r = r && opUnnamedModelProjection_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // opModelProjectionProperties ?
  private static boolean opUnnamedModelProjection_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opUnnamedModelProjection_0")) return false;
    opModelProjectionProperties(b, l + 1);
    return true;
  }

  // ( ( opRecordModelProjection
  //                                      | opListModelProjection
  //                                      | opMapModelProjection
  //                                      ) opModelPolymorphicTail?
  //                                    )?
  private static boolean opUnnamedModelProjection_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opUnnamedModelProjection_1")) return false;
    opUnnamedModelProjection_1_0(b, l + 1);
    return true;
  }

  // ( opRecordModelProjection
  //                                      | opListModelProjection
  //                                      | opMapModelProjection
  //                                      ) opModelPolymorphicTail?
  private static boolean opUnnamedModelProjection_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opUnnamedModelProjection_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opUnnamedModelProjection_1_0_0(b, l + 1);
    r = r && opUnnamedModelProjection_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opRecordModelProjection
  //                                      | opListModelProjection
  //                                      | opMapModelProjection
  private static boolean opUnnamedModelProjection_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opUnnamedModelProjection_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opRecordModelProjection(b, l + 1);
    if (!r) r = opListModelProjection(b, l + 1);
    if (!r) r = opMapModelProjection(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opModelPolymorphicTail?
  private static boolean opUnnamedModelProjection_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opUnnamedModelProjection_1_0_1")) return false;
    opModelPolymorphicTail(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // opEntityProjectionRef | opUnnamedEntityProjection
  public static boolean opUnnamedOrRefEntityProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opUnnamedOrRefEntityProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_UNNAMED_OR_REF_ENTITY_PROJECTION, "<op unnamed or ref entity projection>");
    r = opEntityProjectionRef(b, l + 1);
    if (!r) r = opUnnamedEntityProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // opModelProjectionRef | opUnnamedModelProjection
  public static boolean opUnnamedOrRefModelProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opUnnamedOrRefModelProjection")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_UNNAMED_OR_REF_MODEL_PROJECTION, "<op unnamed or ref model projection>");
    r = opModelProjectionRef(b, l + 1);
    if (!r) r = opUnnamedModelProjection(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( ':' tagName)? opVarPathBody? opModelPath
  public static boolean opVarPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opVarPath")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OP_VAR_PATH, "<op var path>");
    r = opVarPath_0(b, l + 1);
    r = r && opVarPath_1(b, l + 1);
    r = r && opModelPath(b, l + 1);
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
    r = consumeToken(b, S_COLON);
    r = r && tagName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // opVarPathBody?
  private static boolean opVarPath_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opVarPath_1")) return false;
    opVarPathBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' (opModelPathProperty ','?)* '}'
  static boolean opVarPathBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opVarPathBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, opVarPathBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (opModelPathProperty ','?)*
  private static boolean opVarPathBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opVarPathBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!opVarPathBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "opVarPathBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // opModelPathProperty ','?
  private static boolean opVarPathBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opVarPathBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = opModelPathProperty(b, l + 1);
    r = r && opVarPathBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean opVarPathBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "opVarPathBody_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ! ( '}' | ',' |
  //   'method' | 'inputType' | 'inputProjection' | 'outputType' | 'outputProjection' | 'deleteProjection' | 'path' |
  //   '@' | 'read' | 'create' | 'update' | 'delete' | 'custom' )
  static boolean operationBodyRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationBodyRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !operationBodyRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}' | ',' |
  //   'method' | 'inputType' | 'inputProjection' | 'outputType' | 'outputProjection' | 'deleteProjection' | 'path' |
  //   '@' | 'read' | 'create' | 'update' | 'delete' | 'custom'
  private static boolean operationBodyRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationBodyRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_CURLY_RIGHT);
    if (!r) r = consumeToken(b, S_COMMA);
    if (!r) r = consumeToken(b, S_METHOD);
    if (!r) r = consumeToken(b, S_INPUT_TYPE);
    if (!r) r = consumeToken(b, S_INPUT_PROJ);
    if (!r) r = consumeToken(b, S_OUTPUT_TYPE);
    if (!r) r = consumeToken(b, S_OUTPUT_PROJ);
    if (!r) r = consumeToken(b, S_DELETE_PROJ);
    if (!r) r = consumeToken(b, S_PATH);
    if (!r) r = consumeToken(b, S_AT);
    if (!r) r = consumeToken(b, S_OP_READ);
    if (!r) r = consumeToken(b, S_OP_CREATE);
    if (!r) r = consumeToken(b, S_OP_UPDATE);
    if (!r) r = consumeToken(b, S_OP_DELETE);
    if (!r) r = consumeToken(b, S_OP_CUSTOM);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // createOperationDef | readOperationDef | updateOperationDef | deleteOperationDef | customOperationDef
  public static boolean operationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OPERATION_DEF, "<Operation declaration>");
    r = createOperationDef(b, l + 1);
    if (!r) r = readOperationDef(b, l + 1);
    if (!r) r = updateOperationDef(b, l + 1);
    if (!r) r = deleteOperationDef(b, l + 1);
    if (!r) r = customOperationDef(b, l + 1);
    exit_section_(b, l, m, r, false, resourceDefBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'inputType' typeRef
  public static boolean operationInputType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationInputType")) return false;
    if (!nextTokenIs(b, S_INPUT_TYPE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OPERATION_INPUT_TYPE, null);
    r = consumeToken(b, S_INPUT_TYPE);
    p = r; // pin = 1
    r = r && typeRef(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'method' ('GET' | 'POST' | 'PUT' | 'DELETE')
  public static boolean operationMethod(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationMethod")) return false;
    if (!nextTokenIs(b, S_METHOD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OPERATION_METHOD, null);
    r = consumeToken(b, S_METHOD);
    p = r; // pin = 1
    r = r && operationMethod_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // 'GET' | 'POST' | 'PUT' | 'DELETE'
  private static boolean operationMethod_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationMethod_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_GET);
    if (!r) r = consumeToken(b, S_POST);
    if (!r) r = consumeToken(b, S_PUT);
    if (!r) r = consumeToken(b, S_DELETE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'default' | qid
  public static boolean operationName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationName")) return false;
    if (!nextTokenIs(b, "<operation name>", S_DEFAULT, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_OPERATION_NAME, "<operation name>");
    r = consumeToken(b, S_DEFAULT);
    if (!r) r = qid(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'outputType' valueTypeRef
  public static boolean operationOutputType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationOutputType")) return false;
    if (!nextTokenIs(b, S_OUTPUT_TYPE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OPERATION_OUTPUT_TYPE, null);
    r = consumeToken(b, S_OUTPUT_TYPE);
    p = r; // pin = 1
    r = r && valueTypeRef(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'path' opFieldPath
  public static boolean operationPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "operationPath")) return false;
    if (!nextTokenIs(b, S_PATH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OPERATION_PATH, null);
    r = consumeToken(b, S_PATH);
    p = r; // pin = 1
    r = r && opFieldPath(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'outputProjection' opFieldProjection
  public static boolean outputProjection(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "outputProjection")) return false;
    if (!nextTokenIs(b, S_OUTPUT_PROJ)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OUTPUT_PROJECTION, null);
    r = consumeToken(b, S_OUTPUT_PROJ);
    p = r; // pin = 1
    r = r && opFieldProjection(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'outputProjection' qid ':' typeRef '=' opUnnamedOrRefEntityProjection
  public static boolean outputProjectionDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "outputProjectionDef")) return false;
    if (!nextTokenIs(b, S_OUTPUT_PROJ)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_OUTPUT_PROJECTION_DEF, null);
    r = consumeToken(b, S_OUTPUT_PROJ);
    p = r; // pin = 1
    r = r && report_error_(b, qid(b, l + 1));
    r = p && report_error_(b, consumeToken(b, S_COLON)) && r;
    r = p && report_error_(b, typeRef(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, S_EQ)) && r;
    r = p && opUnnamedOrRefEntityProjection(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ! ('}' | '@' | qid ':' | 'abstract' | 'override' | ',' )
  static boolean partRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !partRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}' | '@' | qid ':' | 'abstract' | 'override' | ','
  private static boolean partRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_CURLY_RIGHT);
    if (!r) r = consumeToken(b, S_AT);
    if (!r) r = partRecover_0_2(b, l + 1);
    if (!r) r = consumeToken(b, S_ABSTRACT);
    if (!r) r = consumeToken(b, S_OVERRIDE);
    if (!r) r = consumeToken(b, S_COMMA);
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
  // annotation
  static boolean primitiveBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = annotation(b, l + 1);
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
  // '{' (primitiveBodyPart ','?)* '}' {
  // //  recoverWhile = defRecover
  // }
  public static boolean primitiveTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primitiveTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_PRIMITIVE_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, primitiveTypeBody_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, S_CURLY_RIGHT)) && r;
    r = p && primitiveTypeBody_3(b, l + 1) && r;
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

  // {
  // //  recoverWhile = defRecover
  // }
  private static boolean primitiveTypeBody_3(PsiBuilder b, int l) {
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
  // outputProjectionDef | inputProjectionDef | deleteProjectionDef
  public static boolean projectionDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projectionDef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_PROJECTION_DEF, "<projection def>");
    r = outputProjectionDef(b, l + 1);
    if (!r) r = inputProjectionDef(b, l + 1);
    if (!r) r = deleteProjectionDef(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
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
  // qnSegment ('.' qnSegment)*
  public static boolean qn(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qn")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qnSegment(b, l + 1);
    r = r && qn_1(b, l + 1);
    exit_section_(b, m, S_QN, r);
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
    r = consumeToken(b, S_DOT);
    r = r && qnSegment(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qid
  public static boolean qnSegment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qnSegment")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, S_QN_SEGMENT, r);
    return r;
  }

  /* ********************************************************** */
  // qn
  public static boolean qnTypeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qnTypeRef")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qn(b, l + 1);
    exit_section_(b, m, S_QN_TYPE_REF, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (readOperationBodyPart ','?)* '}'
  static boolean readOperationBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOperationBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, readOperationBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // operationPath |
  //                           outputProjection |
  //                           annotation
  public static boolean readOperationBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOperationBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_READ_OPERATION_BODY_PART, "<read operation body part>");
    r = operationPath(b, l + 1);
    if (!r) r = outputProjection(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, operationBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'read' operationName? readOperationBody
  public static boolean readOperationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOperationDef")) return false;
    if (!nextTokenIs(b, S_OP_READ)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_READ_OPERATION_DEF, null);
    r = consumeToken(b, S_OP_READ);
    p = r; // pin = 1
    r = r && report_error_(b, readOperationDef_1(b, l + 1));
    r = p && readOperationBody(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // operationName?
  private static boolean readOperationDef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOperationDef_1")) return false;
    operationName(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // fieldDecl | annotation
  static boolean recordBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = fieldDecl(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, partRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // dataTypeSpec? '{' recordDatumEntry* '}'
  public static boolean recordDatum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordDatum")) return false;
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
  // ! ( ',' | ')' | qid )
  static boolean recordModelProjectionRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordModelProjectionRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !recordModelProjectionRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ',' | ')' | qid
  private static boolean recordModelProjectionRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordModelProjectionRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COMMA);
    if (!r) r = consumeToken(b, S_PAREN_RIGHT);
    if (!r) r = qid(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' (recordBodyPart ','?)* '}' {
  // //  recoverWhile = defRecover
  // }
  public static boolean recordTypeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recordTypeBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_RECORD_TYPE_BODY, null);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, recordTypeBody_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, S_CURLY_RIGHT)) && r;
    r = p && recordTypeBody_3(b, l + 1) && r;
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

  // {
  // //  recoverWhile = defRecover
  // }
  private static boolean recordTypeBody_3(PsiBuilder b, int l) {
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
  // 'resource' resourceName resourceType resourceDefBody
  public static boolean resourceDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDef")) return false;
    if (!nextTokenIs(b, S_RESOURCE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_RESOURCE_DEF, null);
    r = consumeToken(b, S_RESOURCE);
    p = r; // pin = 1
    r = r && report_error_(b, resourceName(b, l + 1));
    r = p && report_error_(b, resourceType(b, l + 1)) && r;
    r = p && resourceDefBody(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' ( ( operationDef | projectionDef ) ','?)* '}'
  static boolean resourceDefBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBody")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, resourceDefBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, defRecover_parser_);
    return r || p;
  }

  // ( ( operationDef | projectionDef ) ','?)*
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

  // ( operationDef | projectionDef ) ','?
  private static boolean resourceDefBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = resourceDefBody_1_0_0(b, l + 1);
    r = r && resourceDefBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // operationDef | projectionDef
  private static boolean resourceDefBody_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBody_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = operationDef(b, l + 1);
    if (!r) r = projectionDef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean resourceDefBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBody_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ! ( '}' | ',' | '@' | 'read' | 'create' | 'update' | 'delete' | 'custom' |
  //   ( ('inputProjection' | 'outputProjection' | 'deleteProjection') qid ) )
  static boolean resourceDefBodyRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBodyRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !resourceDefBodyRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}' | ',' | '@' | 'read' | 'create' | 'update' | 'delete' | 'custom' |
  //   ( ('inputProjection' | 'outputProjection' | 'deleteProjection') qid )
  private static boolean resourceDefBodyRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBodyRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_CURLY_RIGHT);
    if (!r) r = consumeToken(b, S_COMMA);
    if (!r) r = consumeToken(b, S_AT);
    if (!r) r = consumeToken(b, S_OP_READ);
    if (!r) r = consumeToken(b, S_OP_CREATE);
    if (!r) r = consumeToken(b, S_OP_UPDATE);
    if (!r) r = consumeToken(b, S_OP_DELETE);
    if (!r) r = consumeToken(b, S_OP_CUSTOM);
    if (!r) r = resourceDefBodyRecover_0_8(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('inputProjection' | 'outputProjection' | 'deleteProjection') qid
  private static boolean resourceDefBodyRecover_0_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBodyRecover_0_8")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = resourceDefBodyRecover_0_8_0(b, l + 1);
    r = r && qid(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'inputProjection' | 'outputProjection' | 'deleteProjection'
  private static boolean resourceDefBodyRecover_0_8_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceDefBodyRecover_0_8_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_INPUT_PROJ);
    if (!r) r = consumeToken(b, S_OUTPUT_PROJ);
    if (!r) r = consumeToken(b, S_DELETE_PROJ);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qid
  public static boolean resourceName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceName")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, S_RESOURCE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // ':' valueTypeRef
  public static boolean resourceType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resourceType")) return false;
    if (!nextTokenIs(b, S_COLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COLON);
    r = r && valueTypeRef(b, l + 1);
    exit_section_(b, m, S_RESOURCE_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // 'retro' entityTagRef
  public static boolean retroDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "retroDecl")) return false;
    if (!nextTokenIs(b, S_RETRO)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_RETRO);
    r = r && entityTagRef(b, l + 1);
    exit_section_(b, m, S_RETRO_DECL, r);
    return r;
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
  // 'supplement' qnTypeRef (',' qnTypeRef)* 'with' qnTypeRef
  public static boolean supplementDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementDef")) return false;
    if (!nextTokenIs(b, S_SUPPLEMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_SUPPLEMENT_DEF, null);
    r = consumeToken(b, S_SUPPLEMENT);
    p = r; // pin = 1
    r = r && report_error_(b, qnTypeRef(b, l + 1));
    r = p && report_error_(b, supplementDef_2(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, S_WITH)) && r;
    r = p && qnTypeRef(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (',' qnTypeRef)*
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

  // ',' qnTypeRef
  private static boolean supplementDef_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementDef_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COMMA);
    r = r && qnTypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'supplements' qnTypeRef (',' qnTypeRef)*
  public static boolean supplementsDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementsDecl")) return false;
    if (!nextTokenIs(b, S_SUPPLEMENTS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_SUPPLEMENTS_DECL, null);
    r = consumeToken(b, S_SUPPLEMENTS);
    p = r; // pin = 1
    r = r && report_error_(b, qnTypeRef(b, l + 1));
    r = p && supplementsDecl_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (',' qnTypeRef)*
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

  // ',' qnTypeRef
  private static boolean supplementsDecl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "supplementsDecl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COMMA);
    r = r && qnTypeRef(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // qid | '_'
  public static boolean tagName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tagName")) return false;
    if (!nextTokenIs(b, "<tag name>", S_UNDERSCORE, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_TAG_NAME, "<tag name>");
    r = qid(b, l + 1);
    if (!r) r = consumeToken(b, S_UNDERSCORE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // inputProjection | outputProjection | annotation
  public static boolean transformerBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "transformerBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_TRANSFORMER_BODY_PART, "<transformer body part>");
    r = inputProjection(b, l + 1);
    if (!r) r = outputProjection(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, transformerBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // ! ( '}' | ',' | 'inputProjection' | 'outputProjection' | '@' )
  static boolean transformerBodyRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "transformerBodyRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !transformerBodyRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}' | ',' | 'inputProjection' | 'outputProjection' | '@'
  private static boolean transformerBodyRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "transformerBodyRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_CURLY_RIGHT);
    if (!r) r = consumeToken(b, S_COMMA);
    if (!r) r = consumeToken(b, S_INPUT_PROJ);
    if (!r) r = consumeToken(b, S_OUTPUT_PROJ);
    if (!r) r = consumeToken(b, S_AT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'transformer' transformerName transformerType transformerDefBody
  public static boolean transformerDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "transformerDef")) return false;
    if (!nextTokenIs(b, S_TRANSFORMER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_TRANSFORMER_DEF, null);
    r = consumeToken(b, S_TRANSFORMER);
    p = r; // pin = 1
    r = r && report_error_(b, transformerName(b, l + 1));
    r = p && report_error_(b, transformerType(b, l + 1)) && r;
    r = p && transformerDefBody(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' (transformerBodyPart ','?)* '}'
  static boolean transformerDefBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "transformerDefBody")) return false;
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, transformerDefBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (transformerBodyPart ','?)*
  private static boolean transformerDefBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "transformerDefBody_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!transformerDefBody_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "transformerDefBody_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // transformerBodyPart ','?
  private static boolean transformerDefBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "transformerDefBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = transformerBodyPart(b, l + 1);
    r = r && transformerDefBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean transformerDefBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "transformerDefBody_1_0_1")) return false;
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // qid
  public static boolean transformerName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "transformerName")) return false;
    if (!nextTokenIs(b, S_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = qid(b, l + 1);
    exit_section_(b, m, S_TRANSFORMER_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // ':' typeRef
  public static boolean transformerType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "transformerType")) return false;
    if (!nextTokenIs(b, S_COLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, S_COLON);
    r = r && typeRef(b, l + 1);
    exit_section_(b, m, S_TRANSFORMER_TYPE, r);
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
  // entityTypeDef | recordTypeDef | mapTypeDef | listTypeDef | primitiveTypeDef | enumTypeDef
  public static boolean typeDefWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeDefWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_TYPE_DEF_WRAPPER, "<type definition>");
    r = entityTypeDef(b, l + 1);
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
  // qnTypeRef | anonList | anonMap
  public static boolean typeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, S_TYPE_REF, "<type>");
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
    if (!nextTokenIs(b, S_CURLY_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, S_CURLY_LEFT);
    p = r; // pin = 1
    r = r && report_error_(b, updateOperationBody_1(b, l + 1));
    r = p && consumeToken(b, S_CURLY_RIGHT) && r;
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
    consumeToken(b, S_COMMA);
    return true;
  }

  /* ********************************************************** */
  // operationPath |
  //                             operationInputType |
  //                             inputProjection |
  //                             operationOutputType |
  //                             outputProjection |
  //                             annotation
  public static boolean updateOperationBodyPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateOperationBodyPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_UPDATE_OPERATION_BODY_PART, "<update operation body part>");
    r = operationPath(b, l + 1);
    if (!r) r = operationInputType(b, l + 1);
    if (!r) r = inputProjection(b, l + 1);
    if (!r) r = operationOutputType(b, l + 1);
    if (!r) r = outputProjection(b, l + 1);
    if (!r) r = annotation(b, l + 1);
    exit_section_(b, l, m, r, false, operationBodyRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // 'update' operationName? updateOperationBody
  public static boolean updateOperationDef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateOperationDef")) return false;
    if (!nextTokenIs(b, S_OP_UPDATE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, S_UPDATE_OPERATION_DEF, null);
    r = consumeToken(b, S_OP_UPDATE);
    p = r; // pin = 1
    r = r && report_error_(b, updateOperationDef_1(b, l + 1));
    r = p && updateOperationBody(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // operationName?
  private static boolean updateOperationDef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "updateOperationDef_1")) return false;
    operationName(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // typeRef retroDecl?
  public static boolean valueTypeRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueTypeRef")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, S_VALUE_TYPE_REF, "<value type ref>");
    r = typeRef(b, l + 1);
    r = r && valueTypeRef_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // retroDecl?
  private static boolean valueTypeRef_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "valueTypeRef_1")) return false;
    retroDecl(b, l + 1);
    return true;
  }

  final static Parser dataValueRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return dataValueRecover(b, l + 1);
    }
  };
  final static Parser defRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return defRecover(b, l + 1);
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
  final static Parser opKeySpecRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return opKeySpecRecover(b, l + 1);
    }
  };
  final static Parser opModelPropertyRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return opModelPropertyRecover(b, l + 1);
    }
  };
  final static Parser operationBodyRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return operationBodyRecover(b, l + 1);
    }
  };
  final static Parser partRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return partRecover(b, l + 1);
    }
  };
  final static Parser recordModelProjectionRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return recordModelProjectionRecover(b, l + 1);
    }
  };
  final static Parser resourceDefBodyRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return resourceDefBodyRecover(b, l + 1);
    }
  };
  final static Parser transformerBodyRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return transformerBodyRecover(b, l + 1);
    }
  };
}
