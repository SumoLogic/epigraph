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

package ws.epigraph.schema.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.TokenType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.NamingConventions;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.schema.parser.psi.stubs.SchemaNamespaceDeclStub;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ws.epigraph.schema.lexer.SchemaElementTypes.S_WITH;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaPsiImplUtil {
//  private static final SchemaTypeDef[] EMPTY_TYPE_DEFS = new SchemaTypeDef[0];

  // namespace --------------------------------------------

  @Contract(pure = true)
  public static @Nullable Qn getFqn(SchemaNamespaceDecl namespaceDecl) {
    SchemaNamespaceDeclStub stub = namespaceDecl.getStub();
    if (stub != null) return stub.getFqn();

    SchemaQn schemaQn = namespaceDecl.getQn();
    return schemaQn == null ? null : getQn(schemaQn);
  }

  // qid --------------------------------------------

  @Contract(pure = true)
  public static @NotNull PsiElement setName(SchemaQid qid, String name) {
    PsiElement oldId = qid.getId();
    PsiElement newId = SchemaElementFactory.createId(qid.getProject(), name);
    oldId.replace(newId);
    return qid;
  }

  @Contract(pure = true)
  public static @NotNull String getName(SchemaQid qid) {
    return qid.getId().getText();
  }

  @Contract(pure = true)
  public static @NotNull String getCanonicalName(SchemaQid qid) {
    String name = getName(qid);
    return NamingConventions.unquote(name);
  }

  // fqn --------------------------------------------

  @Contract(pure = true)
  public static @NotNull Qn getQn(SchemaQn e) {
    List<SchemaQnSegment> fqnSegmentList = e.getQnSegmentList();
    String[] segments = new String[fqnSegmentList.size()];
    int idx = 0;

    for (SchemaQnSegment segment : fqnSegmentList) {
      segments[idx++] = segment.getQid().getCanonicalName();
    }

    return new Qn(segments);
  }

  // typedef wrapper --------------------------------------------

  public static void delete(@NotNull SchemaTypeDefWrapper schemaTypeDef) throws IncorrectOperationException {
    final ASTNode parentNode = schemaTypeDef.getParent().getNode();
    assert parentNode != null;

    ASTNode node = schemaTypeDef.getNode();
    ASTNode prev = node.getTreePrev();
    ASTNode next = node.getTreeNext();
    parentNode.removeChild(node);
    if ((prev == null || prev.getElementType() == TokenType.WHITE_SPACE) && next != null &&
        next.getElementType() == TokenType.WHITE_SPACE) {
      parentNode.removeChild(next);
    }
  }

  @Contract(pure = true)
  public static @NotNull SchemaTypeDef getElement(SchemaTypeDefWrapper typeDef) {
    SchemaTypeDef e = typeDef.getEntityTypeDef();
    if (e != null) return e;
    e = typeDef.getRecordTypeDef();
    if (e != null) return e;
    e = typeDef.getMapTypeDef();
    if (e != null) return e;
    e = typeDef.getListTypeDef();
    if (e != null) return e;
    e = typeDef.getEnumTypeDef();
    if (e != null) return e;
    e = typeDef.getPrimitiveTypeDef();
    if (e != null) return e;

    throw new IllegalStateException("Unknown type def: " + typeDef);
  }

  // record --------------------------------------------

  @Contract(pure = true)
  public static @NotNull List<SchemaTypeDef> supplemented(@NotNull SchemaRecordTypeDef recordTypeDef) {
    return SchemaPsiImplUtilExt.supplemented(recordTypeDef);
  }

  // primitive --------------------------------------------
  @Contract(pure = true)
  public static @NotNull PrimitiveTypeKind getPrimitiveTypeKind(@NotNull SchemaPrimitiveTypeDef primitiveTypeDef) {
    if (primitiveTypeDef.getStringT() != null) return PrimitiveTypeKind.STRING;
    if (primitiveTypeDef.getLongT() != null) return PrimitiveTypeKind.LONG;
    if (primitiveTypeDef.getIntegerT() != null) return PrimitiveTypeKind.INTEGER;
    if (primitiveTypeDef.getBooleanT() != null) return PrimitiveTypeKind.BOOLEAN;
    if (primitiveTypeDef.getDoubleT() != null) return PrimitiveTypeKind.DOUBLE;

    throw new IllegalStateException("Primitive type kind not found: " + primitiveTypeDef);
  }

  // var --------------------------------------------

  @Contract(pure = true)
  public static @NotNull List<SchemaTypeDef> supplemented(@NotNull SchemaEntityTypeDef entityTypeDef) {
    return SchemaPsiImplUtilExt.supplemented(entityTypeDef);
  }

//  @Nullable
//  public static String getName(@NotNull SchemaSupplementDef schemaSupplementDef) {
//
//  }

  // fqn type ref --------------------------------------------

  // not exposed through PSI
  @Contract(pure = true)
  public static @Nullable PsiReference getReference(@NotNull SchemaQnTypeRef typeRef) {
    List<SchemaQnSegment> fqnSegmentList = typeRef.getQn().getQnSegmentList();
    if (fqnSegmentList.isEmpty()) return null;
    return fqnSegmentList.get(fqnSegmentList.size() - 1).getReference();
  }

  @Contract(pure = true)
  public static @Nullable SchemaTypeDef resolve(@NotNull SchemaQnTypeRef typeRef) {
    PsiReference reference = getReference(typeRef);
    if (reference == null) return null;
    PsiElement element = reference.resolve();
    if (element instanceof SchemaTypeDef) return (SchemaTypeDef) element;
    return null;
  }


  // supplement --------------------------------------------

  // can't use SchemaSupplementDef::getFqnTypeRefList as it will include both source and all supplemented

  @Contract(pure = true)
  public static @Nullable SchemaQnTypeRef sourceRef(@NotNull SchemaSupplementDef supplementDef) {
    PsiElement with = supplementDef.getWith();
    if (with == null) return null;
    return PsiTreeUtil.getNextSiblingOfType(with, SchemaQnTypeRef.class);
  }

  @Contract(pure = true)
  public static @NotNull List<SchemaQnTypeRef> supplementedRefs(@NotNull SchemaSupplementDef supplementDef) {
    /*
    PsiElement with = supplementDef.getWith();
    if (with == null) return Collections.emptyList();

    SchemaFqnTypeRef ref = PsiTreeUtil.getPrevSiblingOfType(with, SchemaFqnTypeRef.class);
    if (ref == null) return Collections.emptyList();

    List<SchemaFqnTypeRef> result = new ArrayList<>();
    while (ref != null) {
      result.add(ref);
      ref = PsiTreeUtil.getPrevSiblingOfType(ref, SchemaFqnTypeRef.class);
    }

    return result;
    */

    List<SchemaQnTypeRef> result = new ArrayList<>();

    for (PsiElement element = supplementDef.getSupplement();
         element != null && element.getNode().getElementType() != S_WITH;
         element = element.getNextSibling()) {

      if (element instanceof SchemaQnTypeRef) result.add((SchemaQnTypeRef) element);
    }

    return result;
  }

  @Contract(pure = true)
  public static @Nullable SchemaTypeDef source(@NotNull SchemaSupplementDef supplementDef) {
    return SchemaPsiImplUtilExt.source(supplementDef);
  }

  @Contract(pure = true)
  public static @NotNull List<SchemaTypeDef> supplemented(@NotNull SchemaSupplementDef supplementDef) {
    return SchemaPsiImplUtilExt.supplemented(supplementDef);
  }

  @Contract(pure = true)
  public static @NotNull ItemPresentation getPresentation(@NotNull SchemaSupplementDef supplementDef) {
    return SchemaPsiImplUtilExt.getPresentation(supplementDef);
  }

//  public static PsiElement setName(SchemaFqnTypeRef fqnTypeRef, String name) {
//    SchemaFqn oldFqn = fqnTypeRef.getFqn();
//    SchemaFqn newFqn = SchemaElementFactory.createFqn(fqnTypeRef.getProject(), name);
//    oldFqn.replace(newFqn);
//    return fqnTypeRef;
//  }

  // segment --------------------------------------------

  /**
   * @return FQN of this segment. If it's a part of a larger FQN, then all segments up to
   * (including) this one are returned.
   */
  @Contract(pure = true)
  public static @NotNull Qn getQn(SchemaQnSegment e) {
    SchemaQn schemaQn = (SchemaQn) e.getParent();
    assert schemaQn != null;

    List<SchemaQnSegment> fqnSegmentList = schemaQn.getQnSegmentList();
    List<String> segments = new ArrayList<>(fqnSegmentList.size());

    for (SchemaQnSegment segment : fqnSegmentList) {
      segments.add(segment.getName());
      if (segment == e) break;
    }

    return new Qn(segments);
  }

  @Contract(pure = true)
  public static @Nullable SchemaQn getSchemaFqn(SchemaQnSegment segment) {
    PsiElement fqn = segment.getParent();
    if (fqn instanceof SchemaQn) {
      return (SchemaQn) fqn;
    }

    return null;
  }

  @Contract(pure = true)
  public static @Nullable SchemaQnTypeRef getSchemaFqnTypeRef(SchemaQnSegment segment) {
    PsiElement fqn = segment.getParent();
    if (fqn instanceof SchemaQn) {
      PsiElement fqnParent = fqn.getParent();
      if (fqnParent instanceof SchemaQnTypeRef) {
        return (SchemaQnTypeRef) fqnParent;
      }
    }

    return null;
  }

  @Contract(pure = true)
  public static @Nullable String getName(SchemaQnSegment segment) {
    return getNameIdentifier(segment).getText();
  }

  @Contract(pure = true)
  public static @NotNull PsiElement setName(SchemaQnSegment segment, String name) {
    segment.getQid().setName(name);
    return segment;
  }

  @Contract(pure = true)
  public static @NotNull PsiElement getNameIdentifier(SchemaQnSegment segment) {
    return segment.getQid().getId();
  }

  @Contract(pure = true)
  public static boolean isLast(SchemaQnSegment segment) {
    PsiElement parent = segment.getParent();
    if (parent instanceof SchemaQn) {
      SchemaQn schemaQn = (SchemaQn) parent;
      List<SchemaQnSegment> segmentList = schemaQn.getQnSegmentList();
      return segment == getLast(segmentList);
    }
    return false;
  }

  @Contract(pure = true)
  public static @Nullable PsiReference getReference(SchemaQnSegment segment) {
    return SchemaReferenceFactory.getQnReference(segment);
  }


  // member decls --------------------------------------------
  // field decl

  @Contract(pure = true)
  public static @Nullable String getName(SchemaFieldDecl fieldDecl) {
    return getNameIdentifier(fieldDecl).getText();
  }

  public static PsiElement setName(SchemaFieldDecl fieldDecl, String name) {
//    if (NamingConventions.validateFieldName(name) != null) name = NamingConventions.enquote(name);
    fieldDecl.getQid().setName(name);
    return fieldDecl;
  }

  @Contract(pure = true)
  public static @NotNull PsiElement getNameIdentifier(SchemaFieldDecl fieldDecl) {
    return fieldDecl.getQid().getId();
  }

  public static int getTextOffset(@NotNull SchemaFieldDecl fieldDecl) {
    PsiElement nameIdentifier = fieldDecl.getNameIdentifier();
    return nameIdentifier.getTextOffset();
  }

  @Contract(pure = true)
  public static @NotNull ItemPresentation getPresentation(@NotNull SchemaFieldDecl fieldDecl) {
    return SchemaPsiImplUtilExt.getPresentation(fieldDecl);
  }

  public static @NotNull SchemaRecordTypeDef getRecordTypeDef(@NotNull SchemaFieldDecl fieldDecl) {
    SchemaRecordTypeDef recordTypeDef = PsiTreeUtil.getParentOfType(fieldDecl, SchemaRecordTypeDef.class);
    assert recordTypeDef != null;
    return recordTypeDef;
  }

  // varTypeMember decl

  @Contract(pure = true)
  public static @Nullable String getName(SchemaEntityTagDecl entityTagDecl) {
    return getNameIdentifier(entityTagDecl).getText();
  }

  public static PsiElement setName(SchemaEntityTagDecl entityTagDecl, String name) {
//    if (NamingConventions.validateTagName(name) != null) name = NamingConventions.enquote(name);
    entityTagDecl.getQid().setName(name);
    return entityTagDecl;
  }

  @Contract(pure = true)
  public static @NotNull PsiElement getNameIdentifier(SchemaEntityTagDecl varTagDecl) {
    return varTagDecl.getQid().getId();
  }

  public static int getTextOffset(@NotNull SchemaEntityTagDecl entityTagDecl) {
    PsiElement nameIdentifier = entityTagDecl.getNameIdentifier();
    return nameIdentifier.getTextOffset();
  }

  @Contract(pure = true)
  public static @NotNull ItemPresentation getPresentation(@NotNull SchemaEntityTagDecl entityTagDecl) {
    return SchemaPsiImplUtilExt.getPresentation(entityTagDecl);
  }

  public static @NotNull SchemaEntityTypeDef getEntityDef(@NotNull SchemaEntityTagDecl entityTagDecl) {
    SchemaEntityTypeDef varTypeDef = PsiTreeUtil.getParentOfType(entityTagDecl, SchemaEntityTypeDef.class);
    assert varTypeDef != null;
    return varTypeDef;
  }

  // vartype default ref

  @Contract(pure = true)
  public static @Nullable PsiReference getReference(@NotNull SchemaEntityTagRef entityTagRef) {
    return SchemaReferenceFactory.getEntityTagReference(entityTagRef);
  }

  @Contract(pure = true)
  public static @NotNull PsiElement getNameIdentifier(@NotNull SchemaEntityTagRef entityTagRef) {
    return entityTagRef.getQid().getId();
  }

  public static PsiElement setName(SchemaEntityTagRef entityTagRef, String name) {
//    if (NamingConventions.validateTagName(name) != null) name = NamingConventions.enquote(name);
    entityTagRef.getQid().setName(name);
    return entityTagRef;
  }

  // enumMember decl

  @Contract(pure = true)
  public static @Nullable String getName(SchemaEnumMemberDecl enumMemberDecl) {
    return getNameIdentifier(enumMemberDecl).getText();
  }

  public static PsiElement setName(SchemaEnumMemberDecl enumMemberDecl, String name) {
    enumMemberDecl.getQid().setName(name);
    return enumMemberDecl;
  }

  @Contract(pure = true)
  public static @NotNull PsiElement getNameIdentifier(SchemaEnumMemberDecl enumMemberDecl) {
    return enumMemberDecl.getQid().getId();
  }

  // annotation

  @Contract(pure = true)
  public static @Nullable String getName(SchemaAnnotation annotation) {
    return getNameIdentifier(annotation).getText();
  }

  public static PsiElement setName(SchemaAnnotation annotation, String name) {
    annotation.getQid().setName(name);
    return annotation;
  }

  @Contract(pure = true)
  public static @NotNull PsiElement getNameIdentifier(SchemaAnnotation annotation) {
    return annotation.getQid().getId();
  }

  // common toNullableString for all stub-based elements --------------------------------------------

  @Contract(pure = true)
  public static @NotNull String toString(PsiElement element) {
    return element.getClass().getSimpleName() + "(" + element.getNode().getElementType().toString() + ")";
  }

  /////////////

  @Contract(value = "null -> null", pure = true)
  private static <T> T getLast(List<T> list) {
    if (list == null || list.isEmpty()) return null;
    return list.get(list.size() - 1);
  }
}
