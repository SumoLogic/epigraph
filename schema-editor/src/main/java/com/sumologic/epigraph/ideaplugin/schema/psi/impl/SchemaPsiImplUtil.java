package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.sumologic.epigraph.ideaplugin.schema.brains.Fqn;
import com.sumologic.epigraph.ideaplugin.schema.brains.ReferenceFactory;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqn;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnSegment;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnTypeRef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPsiImplUtil {

  @NotNull
  public static Fqn getFqn(SchemaFqn e) {
    List<SchemaFqnSegment> fqnSegmentList = e.getFqnSegmentList();
    String[] segments = new String[fqnSegmentList.size()];
    int idx = 0;

    for (SchemaFqnSegment segment : fqnSegmentList) {
      segments[idx++] = segment.getName();
    }

    return new Fqn(segments);
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

//  public static PsiElement setName(SchemaFqnTypeRef fqnTypeRef, String name) {
//    SchemaFqn oldFqn = fqnTypeRef.getFqn();
//    SchemaFqn newFqn = SchemaElementFactory.createFqn(fqnTypeRef.getProject(), name);
//    oldFqn.replace(newFqn);
//    return oldFqn;
//  }

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
    if (!isLast(segment)) return null; // build reference to schema file(s)?

    SchemaFqnTypeRef fqnTypeRef = getFqnTypeRef(segment);
    if (fqnTypeRef == null) return null;

    return ReferenceFactory.getReference(segment, fqnTypeRef);
  }

  private static <T> T getLast(List<T> list) {
    if (list == null || list.isEmpty()) return null;
    return list.get(list.size() - 1);
  }
}
