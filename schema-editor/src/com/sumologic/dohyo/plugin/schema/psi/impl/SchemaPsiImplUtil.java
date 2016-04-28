package com.sumologic.dohyo.plugin.schema.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.dohyo.plugin.schema.psi.*;
import com.sumologic.dohyo.plugin.schema.psi.references.SchemaTypeReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPsiImplUtil {

  @NotNull
  public static String getFqnString(SchemaFqn e) {
    StringBuilder res = new StringBuilder();

    for (PsiElement psiElement : e.getFqnSegmentList()) {
      if (res.length() > 0) res.append('.');
      res.append(psiElement.getText().trim());
    }

    return res.toString();
  }

  @Nullable
  public static String getNamespacePrefix(SchemaFqn e) {
    List<SchemaFqnSegment> fqnSegmentList = e.getFqnSegmentList();
    if (fqnSegmentList.size() < 2) return null;

    StringBuilder res = new StringBuilder();
    for (int i = 0; i < fqnSegmentList.size() - 1; i++) {
      if (i > 0) res.append('.');
      res.append(fqnSegmentList.get(i).getId().getText());
    }

    return res.toString();
  }

  @Nullable
  public static SchemaFqnSegment getLastSegment(SchemaFqn e) {
    return getLast(e.getFqnSegmentList());
  }

  @Nullable
  public static String getName(SchemaTypeDef schemaTypeDef) {
    PsiElement id = schemaTypeDef.getId();
    return id == null ? null : id.getText();
  }

  @Nullable
  public static PsiElement setName(SchemaTypeDef schemaTypeDef, String name) {
    PsiElement id = schemaTypeDef.getId();
    if (id == null) return null;
    else {
      PsiElement newId = SchemaElementFactory.createId(name);
      id.replace(newId);
      return id;
    }
  }

  @Nullable
  public static PsiElement getNameIdentifier(@NotNull SchemaTypeDef schemaTypeDef) {
    return schemaTypeDef.getId();
  }

  public static int getTextOffset(@NotNull SchemaTypeDef schemaTypeDef) {
    PsiElement nameIdentifier = schemaTypeDef.getNameIdentifier();
    return nameIdentifier == null ? 0 : nameIdentifier.getTextOffset();
  }

//  @Nullable
//  public static String getFullName(@NotNull SchemaTypeDef schemaTypeDef) {
//    String shortName = getName(schemaTypeDef);
//    if (shortName == null) return null;
//
//    SchemaFile file = PsiTreeUtil.getParentOfType(schemaTypeDef, SchemaFile.class);
//    if (file == null) return null;
//
//    SchemaNamespaceDecl namespaceDecl = file.getNamespaceDecl();
//    if (namespaceDecl == null) return null; // or short name? or anon/root namespace?
//
//    SchemaFqn fqn = namespaceDecl.getFqn();
//    if (fqn == null) return null;
//
//    String namespace = getFqnString(fqn);
//
//    return namespace.length() == 0 ? null : namespace + '.' + shortName;
//  }
//
//  public static String getName(SchemaFqnTypeRef fqnTypeRef) {
//    // TODO return full dotted notation as name
//    SchemaFqn fqn = fqnTypeRef.getFqn();
//    List<SchemaFqnSegment> segments = fqn.getFqnSegmentList();
//    return segments.isEmpty() ? null : getLast(segments).getText();
//  }
//
//  public static PsiElement setName(SchemaFqnTypeRef fqnTypeRef, String name) {
//    SchemaFqn oldFqn = fqnTypeRef.getFqn();
//    SchemaFqn newFqn = SchemaElementFactory.createFqn(fqnTypeRef.getProject(), name);
//    oldFqn.replace(newFqn);
//    return oldFqn;
//  }

  public static PsiElement setName(SchemaFqnTypeRef fqnTypeRef, String name) {
    SchemaFqn oldFqn = fqnTypeRef.getFqn();
    SchemaFqn newFqn = SchemaElementFactory.createFqn(fqnTypeRef.getProject(), name);
    oldFqn.replace(newFqn);
    return oldFqn;
  }

  @Nullable
  public static SchemaFqnTypeRef getFqnTypeRef(SchemaFqnSegment segment) {
    PsiElement fqn = segment.getParent();
    if (fqn instanceof SchemaFqn) {
      PsiElement fqnParent = fqn.getParent();
      if (fqnParent instanceof SchemaFqnTypeRef) {
        return (SchemaFqnTypeRef) fqnParent;
      }
    }

    return null;
  }

  @Nullable
  public static String getName(SchemaFqnSegment segment) {
    return getNameIdentifier(segment).getText();
  }

  public static PsiElement setName(SchemaFqnSegment segment, String name) {
    PsiElement oldId = segment.getId();
    PsiElement newId = SchemaElementFactory.createId(name);
    return oldId.replace(newId);
  }

  @Nullable
  public static PsiElement getNameIdentifier(SchemaFqnSegment segment) {
    return segment.getId();
  }

  public static boolean isLast(SchemaFqnSegment segment) {
    PsiElement parent = segment.getParent();
    if (parent instanceof SchemaFqn) {
      SchemaFqn schemaFqn = (SchemaFqn) parent;
      List<SchemaFqnSegment> segmentList = schemaFqn.getFqnSegmentList();
      return segment == getLast(segmentList);
    }
    return false;
  }

  @Nullable
  public static PsiReference getReference(SchemaFqnSegment segment) {
    // TODO optimize: this method calls fqn.getSegmentList() a lot, creating a bunch of copies

    if (!isLast(segment)) return null; // build reference to schema file(s)?

    SchemaFqnTypeRef fqnTypeRef = getFqnTypeRef(segment);
    if (fqnTypeRef == null) return null;

    SchemaFqn fqn = fqnTypeRef.getFqn();
    SchemaFqnSegment lastSegment = getLastSegment(fqn);
    if (lastSegment == null) return null;

    String shortName = lastSegment.getId().getText();

    Collection<String> namespacesToSearch;
    boolean isFullyQualified = fqn.getFqnSegmentList().size() > 1;
    if (isFullyQualified) {
      namespacesToSearch = Collections.singleton(getNamespacePrefix(fqn));
    } else {
      namespacesToSearch = getVisibleNamespaces(fqnTypeRef);
    }

    return new SchemaTypeReference(segment, namespacesToSearch, shortName);
  }

  @NotNull
  private static Set<String> getVisibleNamespaces(PsiElement element) {
    // any way or need to cache this?
    SchemaFile schemaFile = PsiTreeUtil.getParentOfType(element, SchemaFile.class);
    if (schemaFile == null) return Collections.emptySet();

    Set<String> res = new HashSet<>();

    SchemaNamespaceDecl namespaceDecl = schemaFile.getNamespaceDecl();
    if (namespaceDecl != null) {
      SchemaFqn namespaceDeclFqn = namespaceDecl.getFqn();
      res.add(getFqnString(namespaceDeclFqn));
    }

    SchemaImportStatement[] importStatements = schemaFile.getImportStatements();
    for (SchemaImportStatement importStatement : importStatements) {
      SchemaFqn importFqn = importStatement.getFqn();
      if (importFqn != null)
        res.add(getFqnString(importFqn));
    }

    return res;
  }

  private static <T> T getLast(List<T> list) {
    if (list == null || list.isEmpty()) return null;
    return list.get(list.size() - 1);
  }
}
