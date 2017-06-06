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

package ws.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
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
public final class TypeMembers {
  private TypeMembers() {}

  @NotNull
  public static List<SchemaFieldDecl> getOverridenFields(@NotNull SchemaFieldDecl fieldDecl) {
    Project project = fieldDecl.getProject();
    return getSameNameFields(fieldDecl, HierarchyCache.getHierarchyCache(project).getTypeParents(fieldDecl.getRecordTypeDef()));
  }

  @NotNull
  public static List<SchemaFieldDecl> getOverridingFields(@NotNull SchemaFieldDecl fieldDecl) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(fieldDecl.getProject());
    return getSameNameFields(fieldDecl, hierarchyCache.getTypeInheritors(fieldDecl.getRecordTypeDef()));
  }

  @NotNull
  public static List<SchemaFieldDecl> getOverridableFields(@NotNull SchemaRecordTypeDef recordTypeDef) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(recordTypeDef.getProject());
    List<SchemaFieldDecl> allFieldDecls = getFieldDecls(recordTypeDef, null);
    List<SchemaFieldDecl> existingFieldDecls = getFieldDecls(null, Collections.singletonList(recordTypeDef));
    allFieldDecls.removeAll(existingFieldDecls);
    return allFieldDecls;
  }

  @NotNull
  public static List<SchemaEntityTagDecl> getOverridenTags(@NotNull SchemaEntityTagDecl varTagDecl) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(varTagDecl.getProject());
    return getSameNameTags(varTagDecl, hierarchyCache.getTypeParents(varTagDecl.getEntityDef()));
  }

  @NotNull
  public static List<SchemaEntityTagDecl> getOverridingTags(@NotNull SchemaEntityTagDecl varTagDecl) {
    Project project = varTagDecl.getProject();
    return getSameNameTags(varTagDecl, HierarchyCache.getHierarchyCache(project).getTypeInheritors(varTagDecl.getEntityDef()));
  }

  @NotNull
  public static List<SchemaEntityTagDecl> getOverridableTags(@NotNull SchemaEntityTypeDef entityTypeDef) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(entityTypeDef.getProject());
    List<SchemaEntityTagDecl> allTagDecls = getEntityTagDecls(entityTypeDef, null);
    List<SchemaEntityTagDecl> existingFieldDecls = getEntityTagDecls(null, Collections.singletonList(entityTypeDef));
    allTagDecls.removeAll(existingFieldDecls);
    return allTagDecls;
  }

  @NotNull
  public static List<SchemaFieldDecl> getFieldDecls(@NotNull SchemaTypeDef hostType, @Nullable String fieldName) {
    return getFieldDecls(fieldName, getTypeAndParents(hostType));
  }

  @NotNull
  public static List<SchemaEntityTagDecl> getEntityTagDecls(@NotNull SchemaTypeDef hostType, @Nullable String tagName) {
    return getEntityTagDecls(tagName, getTypeAndParents(hostType));
  }

  public static boolean canHaveRetro(@NotNull SchemaValueTypeRef valueTypeRef) {
    SchemaTypeRef typeRef = valueTypeRef.getTypeRef();
    if (typeRef instanceof SchemaQnTypeRef) {
      SchemaQnTypeRef fqnTypeRef = (SchemaQnTypeRef) typeRef;
      SchemaTypeDef typeDef = fqnTypeRef.resolve();

      return typeDef instanceof SchemaEntityTypeDef;
    } else return false;
  }

  @Nullable
  public static SchemaEntityTagDecl getEffectiveRetro(@NotNull SchemaValueTypeRef valueTypeRef) {
    if (!canHaveRetro(valueTypeRef)) return null;

    SchemaEntityTagDecl defaultTag = getRetroTag(valueTypeRef);
    if (defaultTag != null) return defaultTag;

    SchemaFieldDecl fieldDecl = PsiTreeUtil.getParentOfType(valueTypeRef, SchemaFieldDecl.class);
    if (fieldDecl != null) {
      List<SchemaFieldDecl> overridenFields = getOverridenFields(fieldDecl);

      for (SchemaFieldDecl overridenField : overridenFields) {
        ProgressManager.checkCanceled();
        defaultTag = getRetroTag(overridenField.getValueTypeRef());
        if (defaultTag != null) return defaultTag;
      }
    }

    return null;
    // todo handle lists/maps?
  }

  @Contract("null -> null")
  @Nullable
  private static SchemaEntityTagDecl getRetroTag(@Nullable SchemaValueTypeRef valueTypeRef) {
    if (valueTypeRef == null) return null;

    SchemaRetroDecl retroDecl = valueTypeRef.getRetroDecl();
    if (retroDecl != null) {
      SchemaEntityTagRef varTagRef = retroDecl.getEntityTagRef();
      PsiReference reference = varTagRef.getReference();
      return reference == null ? null : (SchemaEntityTagDecl) reference.resolve();
    }
    return null;
  }

  // =========================

  private static List<SchemaTypeDef> getTypeAndParents(@NotNull SchemaTypeDef typeDef) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(typeDef.getProject());
    List<SchemaTypeDef> parents = hierarchyCache.getTypeParents(typeDef);
    if (parents.isEmpty()) return Collections.singletonList(typeDef);
    final ArrayList<SchemaTypeDef> res = new ArrayList<>(parents.size() + 1);
    res.add(typeDef);
    res.addAll(parents);
    return res;
  }

  @NotNull
  private static List<SchemaFieldDecl> getSameNameFields(@NotNull SchemaFieldDecl fieldDecl,
                                                         @NotNull List<SchemaTypeDef> types) {
    final String fieldName = fieldDecl.getQid().getCanonicalName();

    PsiElement body = fieldDecl.getParent();
    if (body == null) return Collections.emptyList();

    SchemaTypeDef typeDef = (SchemaTypeDef) body.getParent();
    if (typeDef == null) return Collections.emptyList();

    return getFieldDecls(fieldName, types);
  }

  private static List<SchemaFieldDecl> getFieldDecls(@Nullable String fieldName,
                                                     @NotNull List<SchemaTypeDef> typeAndParents) {
    if (typeAndParents.isEmpty()) return Collections.emptyList();

    return typeAndParents.stream()
        .filter(type -> type instanceof SchemaRecordTypeDef)
        .flatMap(type -> {
          SchemaRecordTypeDef recordTypeDef = (SchemaRecordTypeDef) type;
          SchemaRecordTypeBody recordTypeBody = recordTypeDef.getRecordTypeBody();
          if (recordTypeBody == null) {
            return Stream.empty();
          } else {
            return recordTypeBody.getFieldDeclList()
                .stream()
                .filter(f -> fieldName == null || fieldName.equals(f.getQid().getCanonicalName()));
          }
        })
        .collect(Collectors.toList());
  }

  @NotNull
  private static List<SchemaEntityTagDecl> getSameNameTags(@NotNull SchemaEntityTagDecl varTagDecl,
                                                        @NotNull List<SchemaTypeDef> types) {
    final String entityTypeMemberName = varTagDecl.getQid().getCanonicalName();

    PsiElement body = varTagDecl.getParent();
    if (body == null) return Collections.emptyList();

    SchemaTypeDef typeDef = (SchemaTypeDef) body.getParent();
    if (typeDef == null) return Collections.emptyList();

    return getEntityTagDecls(entityTypeMemberName, types);
  }

  private static List<SchemaEntityTagDecl> getEntityTagDecls(@Nullable String varTagName,
                                                       @NotNull List<SchemaTypeDef> typeAndParents) {
    if (typeAndParents.isEmpty()) return Collections.emptyList();

    return typeAndParents.stream()
        .filter(type -> type instanceof SchemaEntityTypeDef)
        .flatMap(type -> {
          SchemaEntityTypeDef entityTypeDef = (SchemaEntityTypeDef) type;
          SchemaEntityTypeBody entityTypeBody = entityTypeDef.getEntityTypeBody();
          if (entityTypeBody == null) {
            return Stream.empty();
          } else {
            return entityTypeBody.getEntityTagDeclList()
                .stream()
                .filter(f -> varTagName == null || varTagName.equals(f.getQid().getCanonicalName()));
          }
        })
        .collect(Collectors.toList());
  }
}
