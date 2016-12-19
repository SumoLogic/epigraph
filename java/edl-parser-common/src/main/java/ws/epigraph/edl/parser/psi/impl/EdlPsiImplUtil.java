/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.edl.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.TokenType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.NamingConventions;
import ws.epigraph.edl.parser.psi.*;
import ws.epigraph.edl.parser.psi.stubs.EdlNamespaceDeclStub;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ws.epigraph.edl.lexer.EdlElementTypes.S_WITH;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlPsiImplUtil {
//  private static final EdlTypeDef[] EMPTY_TYPE_DEFS = new EdlTypeDef[0];

  // namespace --------------------------------------------

  @Contract(pure = true)
  @Nullable
  public static Qn getFqn(EdlNamespaceDecl namespaceDecl) {
    EdlNamespaceDeclStub stub = namespaceDecl.getStub();
    if (stub != null) return stub.getFqn();

    EdlQn edlQn = namespaceDecl.getQn();
    return edlQn == null ? null : getQn(edlQn);
  }

  // qid --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static PsiElement setName(EdlQid qid, String name) {
    PsiElement oldId = qid.getId();
    PsiElement newId = EdlElementFactory.createId(qid.getProject(), name);
    oldId.replace(newId);
    return qid;
  }

  @Contract(pure = true)
  @NotNull
  public static String getName(EdlQid qid) {
    return qid.getId().getText();
  }

  @Contract(pure = true)
  @NotNull
  public static String getCanonicalName(EdlQid qid) {
    String name = getName(qid);
    return NamingConventions.unquote(name);
  }

  // fqn --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static Qn getQn(EdlQn e) {
    List<EdlQnSegment> fqnSegmentList = e.getQnSegmentList();
    String[] segments = new String[fqnSegmentList.size()];
    int idx = 0;

    for (EdlQnSegment segment : fqnSegmentList) {
      segments[idx++] = segment.getQid().getCanonicalName();
    }

    return new Qn(segments);
  }

  // typedef wrapper --------------------------------------------

  public static void delete(@NotNull EdlTypeDefWrapper edlTypeDef) throws IncorrectOperationException {
    final ASTNode parentNode = edlTypeDef.getParent().getNode();
    assert parentNode != null;

    ASTNode node = edlTypeDef.getNode();
    ASTNode prev = node.getTreePrev();
    ASTNode next = node.getTreeNext();
    parentNode.removeChild(node);
    if ((prev == null || prev.getElementType() == TokenType.WHITE_SPACE) && next != null &&
        next.getElementType() == TokenType.WHITE_SPACE) {
      parentNode.removeChild(next);
    }
  }

  @Contract(pure = true)
  @NotNull
  public static EdlTypeDef getElement(EdlTypeDefWrapper typeDef) {
    EdlTypeDef e = typeDef.getVarTypeDef();
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
  @NotNull
  public static List<EdlTypeDef> supplemented(@NotNull EdlRecordTypeDef recordTypeDef) {
    return EdlPsiImplUtilExt.supplemented(recordTypeDef);
  }

  // primitive --------------------------------------------
  @Contract(pure = true)
  @NotNull
  public static PrimitiveTypeKind getPrimitiveTypeKind(@NotNull EdlPrimitiveTypeDef primitiveTypeDef) {
    if (primitiveTypeDef.getStringT() != null) return PrimitiveTypeKind.STRING;
    if (primitiveTypeDef.getLongT() != null) return PrimitiveTypeKind.LONG;
    if (primitiveTypeDef.getIntegerT() != null) return PrimitiveTypeKind.INTEGER;
    if (primitiveTypeDef.getBooleanT() != null) return PrimitiveTypeKind.BOOLEAN;
    if (primitiveTypeDef.getDoubleT() != null) return PrimitiveTypeKind.DOUBLE;

    throw new IllegalStateException("Primitive type kind not found: " + primitiveTypeDef);
  }

  // var --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<EdlTypeDef> supplemented(@NotNull EdlVarTypeDef varTypeDef) {
    return EdlPsiImplUtilExt.supplemented(varTypeDef);
  }

//  @Nullable
//  public static String getName(@NotNull EdlSupplementDef edlSupplementDef) {
//
//  }

  // fqn type ref --------------------------------------------

  // not exposed through PSI
  @Contract(pure = true)
  @Nullable
  public static PsiReference getReference(@NotNull EdlQnTypeRef typeRef) {
    List<EdlQnSegment> fqnSegmentList = typeRef.getQn().getQnSegmentList();
    if (fqnSegmentList.isEmpty()) return null;
    return fqnSegmentList.get(fqnSegmentList.size() - 1).getReference();
  }

  @Contract(pure = true)
  @Nullable
  public static EdlTypeDef resolve(@NotNull EdlQnTypeRef typeRef) {
    PsiReference reference = getReference(typeRef);
    if (reference == null) return null;
    PsiElement element = reference.resolve();
    if (element instanceof EdlTypeDef) return (EdlTypeDef) element;
    return null;
  }


  // supplement --------------------------------------------

  // can't use EdlSupplementDef::getFqnTypeRefList as it will include both source and all supplemented

  @Contract(pure = true)
  @Nullable
  public static EdlQnTypeRef sourceRef(@NotNull EdlSupplementDef supplementDef) {
    PsiElement with = supplementDef.getWith();
    if (with == null) return null;
    return PsiTreeUtil.getNextSiblingOfType(with, EdlQnTypeRef.class);
  }

  @Contract(pure = true)
  @NotNull
  public static List<EdlQnTypeRef> supplementedRefs(@NotNull EdlSupplementDef supplementDef) {
    /*
    PsiElement with = supplementDef.getWith();
    if (with == null) return Collections.emptyList();

    EdlFqnTypeRef ref = PsiTreeUtil.getPrevSiblingOfType(with, EdlFqnTypeRef.class);
    if (ref == null) return Collections.emptyList();

    List<EdlFqnTypeRef> result = new ArrayList<>();
    while (ref != null) {
      result.add(ref);
      ref = PsiTreeUtil.getPrevSiblingOfType(ref, EdlFqnTypeRef.class);
    }

    return result;
    */

    List<EdlQnTypeRef> result = new ArrayList<>();

    for (PsiElement element = supplementDef.getSupplement();
         element != null && element.getNode().getElementType() != S_WITH;
         element = element.getNextSibling()) {

      if (element instanceof EdlQnTypeRef) result.add((EdlQnTypeRef) element);
    }

    return result;
  }

  @Contract(pure = true)
  @Nullable
  public static EdlTypeDef source(@NotNull EdlSupplementDef supplementDef) {
    return EdlPsiImplUtilExt.source(supplementDef);
  }

  @Contract(pure = true)
  @NotNull
  public static List<EdlTypeDef> supplemented(@NotNull EdlSupplementDef supplementDef) {
    return EdlPsiImplUtilExt.supplemented(supplementDef);
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull EdlSupplementDef supplementDef) {
    return EdlPsiImplUtilExt.getPresentation(supplementDef);
  }

//  public static PsiElement setName(EdlFqnTypeRef fqnTypeRef, String name) {
//    EdlFqn oldFqn = fqnTypeRef.getFqn();
//    EdlFqn newFqn = EdlElementFactory.createFqn(fqnTypeRef.getProject(), name);
//    oldFqn.replace(newFqn);
//    return fqnTypeRef;
//  }

  // segment --------------------------------------------

  /**
   * @return FQN of this segment. If it's a part of a larger FQN, then all segments up to
   * (including) this one are returned.
   */
  @Contract(pure = true)
  @NotNull
  public static Qn getQn(EdlQnSegment e) {
    EdlQn edlQn = (EdlQn) e.getParent();
    assert edlQn != null;

    List<EdlQnSegment> fqnSegmentList = edlQn.getQnSegmentList();
    List<String> segments = new ArrayList<>(fqnSegmentList.size());

    for (EdlQnSegment segment : fqnSegmentList) {
      segments.add(segment.getName());
      if (segment == e) break;
    }

    return new Qn(segments);
  }

  @Contract(pure = true)
  @Nullable
  public static EdlQn getEdlFqn(EdlQnSegment segment) {
    PsiElement fqn = segment.getParent();
    if (fqn instanceof EdlQn) {
      return (EdlQn) fqn;
    }

    return null;
  }

  @Contract(pure = true)
  @Nullable
  public static EdlQnTypeRef getEdlFqnTypeRef(EdlQnSegment segment) {
    PsiElement fqn = segment.getParent();
    if (fqn instanceof EdlQn) {
      PsiElement fqnParent = fqn.getParent();
      if (fqnParent instanceof EdlQnTypeRef) {
        return (EdlQnTypeRef) fqnParent;
      }
    }

    return null;
  }

  @Contract(pure = true)
  @Nullable
  public static String getName(EdlQnSegment segment) {
    return getNameIdentifier(segment).getText();
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement setName(EdlQnSegment segment, String name) {
    segment.getQid().setName(name);
    return segment;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(EdlQnSegment segment) {
    return segment.getQid().getId();
  }

  @Contract(pure = true)
  public static boolean isLast(EdlQnSegment segment) {
    PsiElement parent = segment.getParent();
    if (parent instanceof EdlQn) {
      EdlQn edlQn = (EdlQn) parent;
      List<EdlQnSegment> segmentList = edlQn.getQnSegmentList();
      return segment == getLast(segmentList);
    }
    return false;
  }

  @Contract(pure = true)
  @Nullable
  public static PsiReference getReference(EdlQnSegment segment) {
    return EdlReferenceFactory.getQnReference(segment);
  }


  // member decls --------------------------------------------
  // field decl

  @Contract(pure = true)
  @Nullable
  public static String getName(EdlFieldDecl fieldDecl) {
    return getNameIdentifier(fieldDecl).getText();
  }

  public static PsiElement setName(EdlFieldDecl fieldDecl, String name) {
//    if (NamingConventions.validateFieldName(name) != null) name = NamingConventions.enquote(name);
    fieldDecl.getQid().setName(name);
    return fieldDecl;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(EdlFieldDecl fieldDecl) {
    return fieldDecl.getQid().getId();
  }

  public static int getTextOffset(@NotNull EdlFieldDecl fieldDecl) {
    PsiElement nameIdentifier = fieldDecl.getNameIdentifier();
    return nameIdentifier.getTextOffset();
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull EdlFieldDecl fieldDecl) {
    return EdlPsiImplUtilExt.getPresentation(fieldDecl);
  }

  @NotNull
  public static EdlRecordTypeDef getRecordTypeDef(@NotNull EdlFieldDecl fieldDecl) {
    EdlRecordTypeDef recordTypeDef = PsiTreeUtil.getParentOfType(fieldDecl, EdlRecordTypeDef.class);
    assert recordTypeDef != null;
    return recordTypeDef;
  }

  // varTypeMember decl

  @Contract(pure = true)
  @Nullable
  public static String getName(EdlVarTagDecl varTagDecl) {
    return getNameIdentifier(varTagDecl).getText();
  }

  public static PsiElement setName(EdlVarTagDecl varTagDecl, String name) {
//    if (NamingConventions.validateTagName(name) != null) name = NamingConventions.enquote(name);
    varTagDecl.getQid().setName(name);
    return varTagDecl;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(EdlVarTagDecl varTagDecl) {
    return varTagDecl.getQid().getId();
  }

  public static int getTextOffset(@NotNull EdlVarTagDecl varTagDecl) {
    PsiElement nameIdentifier = varTagDecl.getNameIdentifier();
    return nameIdentifier.getTextOffset();
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull EdlVarTagDecl varTagDecl) {
    return EdlPsiImplUtilExt.getPresentation(varTagDecl);
  }

  @NotNull
  public static EdlVarTypeDef getVarTypeDef(@NotNull EdlVarTagDecl varTagDecl) {
    EdlVarTypeDef varTypeDef = PsiTreeUtil.getParentOfType(varTagDecl, EdlVarTypeDef.class);
    assert varTypeDef != null;
    return varTypeDef;
  }

  // vartype default ref

  @Contract(pure = true)
  @Nullable
  public static PsiReference getReference(@NotNull EdlVarTagRef varTagRef) {
    return EdlReferenceFactory.getVarTagReference(varTagRef);
  }

  @Contract(pure = true)
  @Nullable
  public static PsiElement getNameIdentifier(@NotNull EdlVarTagRef varTagRef) {
    return varTagRef.getQid().getId();
  }

  public static PsiElement setName(EdlVarTagRef varTagRef, String name) {
//    if (NamingConventions.validateTagName(name) != null) name = NamingConventions.enquote(name);
    varTagRef.getQid().setName(name);
    return varTagRef;
  }

  // enumMember decl

  @Contract(pure = true)
  @Nullable
  public static String getName(EdlEnumMemberDecl enumMemberDecl) {
    return getNameIdentifier(enumMemberDecl).getText();
  }

  public static PsiElement setName(EdlEnumMemberDecl enumMemberDecl, String name) {
    enumMemberDecl.getQid().setName(name);
    return enumMemberDecl;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(EdlEnumMemberDecl enumMemberDecl) {
    return enumMemberDecl.getQid().getId();
  }

  // annotation

  @Contract(pure = true)
  @Nullable
  public static String getName(EdlAnnotation annotation) {
    return getNameIdentifier(annotation).getText();
  }

  public static PsiElement setName(EdlAnnotation annotation, String name) {
    annotation.getQid().setName(name);
    return annotation;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(EdlAnnotation annotation) {
    return annotation.getQid().getId();
  }

  // common toNullableString for all stub-based elements --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static String toString(PsiElement element) {
    return element.getClass().getSimpleName() + "(" + element.getNode().getElementType().toString() + ")";
  }

  /////////////

  @Contract(value = "null -> null", pure = true)
  private static <T> T getLast(List<T> list) {
    if (list == null || list.isEmpty()) return null;
    return list.get(list.size() - 1);
  }
}
