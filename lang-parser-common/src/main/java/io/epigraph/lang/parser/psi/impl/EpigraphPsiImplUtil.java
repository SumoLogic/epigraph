package io.epigraph.lang.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.TokenType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.parser.NamingConventions;
import io.epigraph.lang.parser.psi.*;
import io.epigraph.lang.parser.psi.stubs.EpigraphNamespaceDeclStub;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.epigraph.lang.lexer.EpigraphElementTypes.E_WITH;


/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphPsiImplUtil {
//  private static final SchemaTypeDef[] EMPTY_TYPE_DEFS = new SchemaTypeDef[0];

  // namespace --------------------------------------------

  @Contract(pure = true)
  @Nullable
  public static Fqn getFqn2(EpigraphNamespaceDecl namespaceDecl) {
    EpigraphNamespaceDeclStub stub = namespaceDecl.getStub();
    if (stub != null) return stub.getFqn();

    EpigraphFqn epigraphFqn = namespaceDecl.getFqn();
    return epigraphFqn == null ? null : getFqn(epigraphFqn);
  }

  // qid --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static PsiElement setName(EpigraphQid qid, String name) {
    PsiElement oldId = qid.getId();
    PsiElement newId = EpigraphElementFactory.createId(qid.getProject(), name);
    oldId.replace(newId);
    return qid;
  }

  @Contract(pure = true)
  @NotNull
  public static String getName(EpigraphQid qid) {
    return qid.getId().getText();
  }

  @Contract(pure = true)
  @NotNull
  public static String getCanonicalName(EpigraphQid qid) {
    String name = getName(qid);
    return NamingConventions.unquote(name);
  }

  // fqn --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static Fqn getFqn(EpigraphFqn e) {
    List<EpigraphFqnSegment> fqnSegmentList = e.getFqnSegmentList();
    String[] segments = new String[fqnSegmentList.size()];
    int idx = 0;

    for (EpigraphFqnSegment segment : fqnSegmentList) {
      segments[idx++] = segment.getQid().getCanonicalName();
    }

    return new Fqn(segments);
  }

  // typedef wrapper --------------------------------------------

  public static void delete(@NotNull EpigraphTypeDefWrapper schemaTypeDef) throws IncorrectOperationException {
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
  @NotNull
  public static EpigraphTypeDef getElement(EpigraphTypeDefWrapper typeDef) {
    EpigraphTypeDef e = typeDef.getVarTypeDef();
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
  public static List<EpigraphTypeDef> supplemented(@NotNull EpigraphRecordTypeDef recordTypeDef) {
    return EpigraphPsiImplUtilExt.supplemented(recordTypeDef);
  }

  // primitive --------------------------------------------
  @Contract(pure = true)
  @NotNull
  public static PrimitiveTypeKind getPrimitiveTypeKind(@NotNull EpigraphPrimitiveTypeDef primitiveTypeDef) {
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
  public static List<EpigraphTypeDef> supplemented(@NotNull EpigraphVarTypeDef varTypeDef) {
    return EpigraphPsiImplUtilExt.supplemented(varTypeDef);
  }

//  @Nullable
//  public static String getName(@NotNull SchemaSupplementDef schemaSupplementDef) {
//
//  }

  // fqn type ref --------------------------------------------

  // not exposed through PSI
  @Contract(pure = true)
  @Nullable
  public static PsiReference getReference(@NotNull EpigraphFqnTypeRef typeRef) {
    List<EpigraphFqnSegment> fqnSegmentList = typeRef.getFqn().getFqnSegmentList();
    if (fqnSegmentList.isEmpty()) return null;
    return fqnSegmentList.get(fqnSegmentList.size() - 1).getReference();
  }

  @Contract(pure = true)
  @Nullable
  public static EpigraphTypeDef resolve(@NotNull EpigraphFqnTypeRef typeRef) {
    PsiReference reference = getReference(typeRef);
    if (reference == null) return null;
    PsiElement element = reference.resolve();
    if (element instanceof EpigraphTypeDef) return (EpigraphTypeDef) element;
    return null;
  }


  // supplement --------------------------------------------

  // can't use SchemaSupplementDef::getFqnTypeRefList as it will include both source and all supplemented

  @Contract(pure = true)
  @Nullable
  public static EpigraphFqnTypeRef sourceRef(@NotNull EpigraphSupplementDef supplementDef) {
    PsiElement with = supplementDef.getWith();
    if (with == null) return null;
    return PsiTreeUtil.getNextSiblingOfType(with, EpigraphFqnTypeRef.class);
  }

  @Contract(pure = true)
  @NotNull
  public static List<EpigraphFqnTypeRef> supplementedRefs(@NotNull EpigraphSupplementDef supplementDef) {
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

    List<EpigraphFqnTypeRef> result = new ArrayList<>();

    for (PsiElement element = supplementDef.getSupplement();
         element != null && element.getNode().getElementType() != E_WITH;
         element = element.getNextSibling()) {

      if (element instanceof EpigraphFqnTypeRef) result.add((EpigraphFqnTypeRef) element);
    }

    return result;
  }

  @Contract(pure = true)
  @Nullable
  public static EpigraphTypeDef source(@NotNull EpigraphSupplementDef supplementDef) {
    return EpigraphPsiImplUtilExt.source(supplementDef);
  }

  @Contract(pure = true)
  @NotNull
  public static List<EpigraphTypeDef> supplemented(@NotNull EpigraphSupplementDef supplementDef) {
    return EpigraphPsiImplUtilExt.supplemented(supplementDef);
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull EpigraphSupplementDef supplementDef) {
    return EpigraphPsiImplUtilExt.getPresentation(supplementDef);
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
  @NotNull
  public static Fqn getFqn(EpigraphFqnSegment e) {
    EpigraphFqn epigraphFqn = (EpigraphFqn) e.getParent();
    assert epigraphFqn != null;

    List<EpigraphFqnSegment> fqnSegmentList = epigraphFqn.getFqnSegmentList();
    List<String> segments = new ArrayList<>(fqnSegmentList.size());

    for (EpigraphFqnSegment segment : fqnSegmentList) {
      segments.add(segment.getName());
      if (segment == e) break;
    }

    return new Fqn(segments);
  }

  @Contract(pure = true)
  @Nullable
  public static EpigraphFqn getSchemaFqn(EpigraphFqnSegment segment) {
    PsiElement fqn = segment.getParent();
    if (fqn instanceof EpigraphFqn) {
      return (EpigraphFqn) fqn;
    }

    return null;
  }

  @Contract(pure = true)
  @Nullable
  public static EpigraphFqnTypeRef getSchemaFqnTypeRef(EpigraphFqnSegment segment) {
    PsiElement fqn = segment.getParent();
    if (fqn instanceof EpigraphFqn) {
      PsiElement fqnParent = fqn.getParent();
      if (fqnParent instanceof EpigraphFqnTypeRef) {
        return (EpigraphFqnTypeRef) fqnParent;
      }
    }

    return null;
  }

  @Contract(pure = true)
  @Nullable
  public static String getName(EpigraphFqnSegment segment) {
    return getNameIdentifier(segment).getText();
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement setName(EpigraphFqnSegment segment, String name) {
    segment.getQid().setName(name);
    return segment;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(EpigraphFqnSegment segment) {
    return segment.getQid().getId();
  }

  @Contract(pure = true)
  public static boolean isLast(EpigraphFqnSegment segment) {
    PsiElement parent = segment.getParent();
    if (parent instanceof EpigraphFqn) {
      EpigraphFqn epigraphFqn = (EpigraphFqn) parent;
      List<EpigraphFqnSegment> segmentList = epigraphFqn.getFqnSegmentList();
      return segment == getLast(segmentList);
    }
    return false;
  }

  @Contract(pure = true)
  @Nullable
  public static PsiReference getReference(EpigraphFqnSegment segment) {
    return EpigraphReferenceFactory.getFqnReference(segment);
  }


  // member decls --------------------------------------------
  // field decl

  @Contract(pure = true)
  @Nullable
  public static String getName(EpigraphFieldDecl fieldDecl) {
    return getNameIdentifier(fieldDecl).getText();
  }

  public static PsiElement setName(EpigraphFieldDecl fieldDecl, String name) {
//    if (NamingConventions.validateFieldName(name) != null) name = NamingConventions.enquote(name);
    fieldDecl.getQid().setName(name);
    return fieldDecl;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(EpigraphFieldDecl fieldDecl) {
    return fieldDecl.getQid().getId();
  }

  public static int getTextOffset(@NotNull EpigraphFieldDecl fieldDecl) {
    PsiElement nameIdentifier = fieldDecl.getNameIdentifier();
    return nameIdentifier.getTextOffset();
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull EpigraphFieldDecl fieldDecl) {
    return EpigraphPsiImplUtilExt.getPresentation(fieldDecl);
  }

  @NotNull
  public static EpigraphRecordTypeDef getRecordTypeDef(@NotNull EpigraphFieldDecl fieldDecl) {
    EpigraphRecordTypeDef recordTypeDef = PsiTreeUtil.getParentOfType(fieldDecl, EpigraphRecordTypeDef.class);
    assert recordTypeDef != null;
    return recordTypeDef;
  }

  // varTypeMember decl

  @Contract(pure = true)
  @Nullable
  public static String getName(EpigraphVarTagDecl varTagDecl) {
    return getNameIdentifier(varTagDecl).getText();
  }

  public static PsiElement setName(EpigraphVarTagDecl varTagDecl, String name) {
//    if (NamingConventions.validateTagName(name) != null) name = NamingConventions.enquote(name);
    varTagDecl.getQid().setName(name);
    return varTagDecl;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(EpigraphVarTagDecl varTagDecl) {
    return varTagDecl.getQid().getId();
  }

  public static int getTextOffset(@NotNull EpigraphVarTagDecl varTagDecl) {
    PsiElement nameIdentifier = varTagDecl.getNameIdentifier();
    return nameIdentifier.getTextOffset();
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull EpigraphVarTagDecl varTagDecl) {
    return EpigraphPsiImplUtilExt.getPresentation(varTagDecl);
  }

  @NotNull
  public static EpigraphVarTypeDef getVarTypeDef(@NotNull EpigraphVarTagDecl varTagDecl) {
    EpigraphVarTypeDef varTypeDef = PsiTreeUtil.getParentOfType(varTagDecl, EpigraphVarTypeDef.class);
    assert varTypeDef != null;
    return varTypeDef;
  }

  // vartype default ref

  @Contract(pure = true)
  @Nullable
  public static PsiReference getReference(@NotNull EpigraphVarTagRef varTagRef) {
    return EpigraphReferenceFactory.getVarTagReference(varTagRef);
  }

  @Contract(pure = true)
  @Nullable
  public static PsiElement getNameIdentifier(@NotNull EpigraphVarTagRef varTagRef) {
    return varTagRef.getQid().getId();
  }

  public static PsiElement setName(EpigraphVarTagRef varTagRef, String name) {
//    if (NamingConventions.validateTagName(name) != null) name = NamingConventions.enquote(name);
    varTagRef.getQid().setName(name);
    return varTagRef;
  }

  // enumMember decl

  @Contract(pure = true)
  @Nullable
  public static String getName(EpigraphEnumMemberDecl enumMemberDecl) {
    return getNameIdentifier(enumMemberDecl).getText();
  }

  public static PsiElement setName(EpigraphEnumMemberDecl enumMemberDecl, String name) {
    enumMemberDecl.getQid().setName(name);
    return enumMemberDecl;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(EpigraphEnumMemberDecl enumMemberDecl) {
    return enumMemberDecl.getQid().getId();
  }

  // custom param

  @Contract(pure = true)
  @Nullable
  public static String getName(EpigraphCustomParam customParam) {
    return getNameIdentifier(customParam).getText();
  }

  public static PsiElement setName(EpigraphCustomParam customParam, String name) {
    customParam.getQid().setName(name);
    return customParam;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(EpigraphCustomParam customParam) {
    return customParam.getQid().getId();
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
