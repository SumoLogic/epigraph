package com.sumologic.dohyo.plugin.schema.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFqn;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFqnSegment;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFqnTypeRef;
import com.sumologic.dohyo.plugin.schema.psi.SchemaTypeDef;
import com.sumologic.dohyo.plugin.schema.psi.references.SchemaTypeReference;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPsiImplUtil {

  @Nullable
  public static String getFqnString(SchemaFqn e) {
    StringBuilder res = new StringBuilder();

    for (PsiElement psiElement : e.getFqnSegmentList()) {
      if (res.length() > 0) res.append('.');
      res.append(psiElement.getText().trim());
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

  public static PsiElement getNameIdentifier(SchemaTypeDef schemaTypeDef) {
    return schemaTypeDef.getId();
  }

  public static String getName(SchemaFqnTypeRef fqnTypeRef) {
    // TODO return full dotted notation as name
    SchemaFqn fqn = fqnTypeRef.getFqn();
    List<SchemaFqnSegment> segments = fqn.getFqnSegmentList();
    return segments.isEmpty() ? null : getLast(segments).getText();
  }

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
    if (!isLast(segment)) return null;

    SchemaFqnTypeRef fqnTypeRef = getFqnTypeRef(segment);
    if (fqnTypeRef == null) return null;

    return new SchemaTypeReference(segment, getName(fqnTypeRef), null); // TODO kind
  }

  private static <T> T getLast(List<T> list) {
    if (list == null || list.isEmpty()) return null;
    return list.get(list.size() - 1);
  }
}
