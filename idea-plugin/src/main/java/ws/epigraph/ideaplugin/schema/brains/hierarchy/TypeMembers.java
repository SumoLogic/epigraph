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

package ws.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import ws.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TypeMembers {
  @NotNull
  public static List<EdlFieldDecl> getOverridenFields(@NotNull EdlFieldDecl fieldDecl) {
    Project project = fieldDecl.getProject();
    return getSameNameFields(fieldDecl, HierarchyCache.getHierarchyCache(project).getTypeParents(fieldDecl.getRecordTypeDef()));
  }

  @NotNull
  public static List<EdlFieldDecl> getOverridingFields(@NotNull EdlFieldDecl fieldDecl) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(fieldDecl.getProject());
    return getSameNameFields(fieldDecl, hierarchyCache.getTypeInheritors(fieldDecl.getRecordTypeDef()));
  }

  @NotNull
  public static List<EdlFieldDecl> getOverridableFields(@NotNull EdlRecordTypeDef recordTypeDef) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(recordTypeDef.getProject());
    List<EdlFieldDecl> allFieldDecls = getFieldDecls(recordTypeDef, null);
    List<EdlFieldDecl> existingFieldDecls = getFieldDecls(null, Collections.singletonList(recordTypeDef));
    allFieldDecls.removeAll(existingFieldDecls);
    return allFieldDecls;
  }

  @NotNull
  public static List<EdlVarTagDecl> getOverridenTags(@NotNull EdlVarTagDecl varTagDecl) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(varTagDecl.getProject());
    return getSameNameTags(varTagDecl, hierarchyCache.getTypeParents(varTagDecl.getVarTypeDef()));
  }

  @NotNull
  public static List<EdlVarTagDecl> getOverridingTags(@NotNull EdlVarTagDecl varTagDecl) {
    Project project = varTagDecl.getProject();
    return getSameNameTags(varTagDecl, HierarchyCache.getHierarchyCache(project).getTypeInheritors(varTagDecl.getVarTypeDef()));
  }

  @NotNull
  public static List<EdlVarTagDecl> getOverridableTags(@NotNull EdlVarTypeDef varTypeDef) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(varTypeDef.getProject());
    List<EdlVarTagDecl> allTagDecls = getVarTagDecls(varTypeDef, null);
    List<EdlVarTagDecl> existingFieldDecls = getVarTagDecls(null, Collections.singletonList(varTypeDef));
    allTagDecls.removeAll(existingFieldDecls);
    return allTagDecls;
  }

  @NotNull
  public static List<EdlFieldDecl> getFieldDecls(@NotNull EdlTypeDef hostType, @Nullable String fieldName) {
    return getFieldDecls(fieldName, getTypeAndParents(hostType));
  }

  @NotNull
  public static List<EdlVarTagDecl> getVarTagDecls(@NotNull EdlTypeDef hostType, @Nullable String tagName) {
    return getVarTagDecls(tagName, getTypeAndParents(hostType));
  }

  public static boolean canHaveDefault(@NotNull EdlValueTypeRef valueTypeRef) {
    EdlTypeRef typeRef = valueTypeRef.getTypeRef();
    if (typeRef instanceof EdlQnTypeRef) {
      EdlQnTypeRef fqnTypeRef = (EdlQnTypeRef) typeRef;
      EdlTypeDef typeDef = fqnTypeRef.resolve();

      return typeDef instanceof EdlVarTypeDef;
    } else return false;
  }

  @Nullable
  public static EdlVarTagDecl getEffectiveDefault(@NotNull EdlValueTypeRef valueTypeRef) {
    if (!canHaveDefault(valueTypeRef)) return null;

    EdlVarTagDecl defaultTag = getDefaultTag(valueTypeRef);
    if (defaultTag != null) return defaultTag;

    EdlFieldDecl fieldDecl = PsiTreeUtil.getParentOfType(valueTypeRef, EdlFieldDecl.class);
    if (fieldDecl != null) {
      List<EdlFieldDecl> overridenFields = getOverridenFields(fieldDecl);

      for (EdlFieldDecl overridenField : overridenFields) {
        ProgressManager.checkCanceled();
        defaultTag = getDefaultTag(overridenField.getValueTypeRef());
        if (defaultTag != null) return defaultTag;
      }
    }

    return null;
    // todo handle lists/maps?
  }

  @Nullable
  private static EdlVarTagDecl getDefaultTag(@Nullable EdlValueTypeRef valueTypeRef) {
    if (valueTypeRef == null) return null;

    EdlDefaultOverride defaultOverride = valueTypeRef.getDefaultOverride();
    if (defaultOverride != null) {
      EdlVarTagRef varTagRef = defaultOverride.getVarTagRef();
      PsiReference reference = varTagRef.getReference();
      return reference == null ? null : (EdlVarTagDecl) reference.resolve();
    }
    return null;
  }

  // =========================

  private static List<EdlTypeDef> getTypeAndParents(@NotNull EdlTypeDef typeDef) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(typeDef.getProject());
    List<EdlTypeDef> parents = hierarchyCache.getTypeParents(typeDef);
    if (parents.isEmpty()) return Collections.singletonList(typeDef);
    final ArrayList<EdlTypeDef> res = new ArrayList<>(parents.size() + 1);
    res.add(typeDef);
    res.addAll(parents);
    return res;
  }

  @NotNull
  private static List<EdlFieldDecl> getSameNameFields(@NotNull EdlFieldDecl fieldDecl,
                                                         @NotNull List<EdlTypeDef> types) {
    final String fieldName = fieldDecl.getQid().getCanonicalName();

    PsiElement body = fieldDecl.getParent();
    if (body == null) return Collections.emptyList();

    EdlTypeDef typeDef = (EdlTypeDef) body.getParent();
    if (typeDef == null) return Collections.emptyList();

    return getFieldDecls(fieldName, types);
  }

  private static List<EdlFieldDecl> getFieldDecls(@Nullable String fieldName,
                                                     @NotNull List<EdlTypeDef> typeAndParents) {
    if (typeAndParents.isEmpty()) return Collections.emptyList();

    return typeAndParents.stream()
        .filter(type -> type instanceof EdlRecordTypeDef)
        .flatMap(type -> {
          EdlRecordTypeDef recordTypeDef = (EdlRecordTypeDef) type;
          EdlRecordTypeBody recordTypeBody = recordTypeDef.getRecordTypeBody();
          if (recordTypeBody != null) {
            return recordTypeBody.getFieldDeclList().stream().filter(f -> fieldName == null || fieldName.equals(f.getQid().getCanonicalName()));
          } else {
            return Stream.empty();
          }
        })
        .collect(Collectors.toList());
  }

  @NotNull
  private static List<EdlVarTagDecl> getSameNameTags(@NotNull EdlVarTagDecl varTagDecl,
                                                        @NotNull List<EdlTypeDef> types) {
    final String varTypeMemberName = varTagDecl.getQid().getCanonicalName();

    PsiElement body = varTagDecl.getParent();
    if (body == null) return Collections.emptyList();

    EdlTypeDef typeDef = (EdlTypeDef) body.getParent();
    if (typeDef == null) return Collections.emptyList();

    return getVarTagDecls(varTypeMemberName, types);
  }

  private static List<EdlVarTagDecl> getVarTagDecls(@Nullable String varTagName,
                                                       @NotNull List<EdlTypeDef> typeAndParents) {
    if (typeAndParents.isEmpty()) return Collections.emptyList();

    return typeAndParents.stream()
        .filter(type -> type instanceof EdlVarTypeDef)
        .flatMap(type -> {
          EdlVarTypeDef varTypeDef = (EdlVarTypeDef) type;
          EdlVarTypeBody varTypeBody = varTypeDef.getVarTypeBody();
          if (varTypeBody != null) {
            return varTypeBody.getVarTagDeclList().stream().filter(f -> varTagName == null || varTagName.equals(f.getQid().getCanonicalName()));
          } else {
            return Stream.empty();
          }
        })
        .collect(Collectors.toList());
  }
}
