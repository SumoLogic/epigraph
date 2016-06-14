package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.sumologic.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class TypeMembers {
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
  public static List<SchemaVarTagDecl> getOverridenTags(@NotNull SchemaVarTagDecl varTagDecl) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(varTagDecl.getProject());
    return getSameNameTags(varTagDecl, hierarchyCache.getTypeParents(varTagDecl.getVarTypeDef()));
  }

  @NotNull
  public static List<SchemaVarTagDecl> getOverridingTags(@NotNull SchemaVarTagDecl varTagDecl) {
    Project project = varTagDecl.getProject();
    return getSameNameTags(varTagDecl, HierarchyCache.getHierarchyCache(project).getTypeInheritors(varTagDecl.getVarTypeDef()));
  }

  @NotNull
  public static List<SchemaFieldDecl> getFieldDecls(@NotNull SchemaTypeDef hostType, @Nullable String fieldName) {
    return getFieldDecls(hostType, fieldName, getTypeAndParents(hostType));
  }

  @NotNull
  public static List<SchemaVarTagDecl> getVarTagDecls(@NotNull SchemaTypeDef hostType, @Nullable String tagName) {
    return getVarTagDecls(hostType, tagName, getTypeAndParents(hostType));
  }

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
    final String fieldName = fieldDecl.getName();
    if (fieldName == null) return Collections.emptyList();

    PsiElement body = fieldDecl.getParent();
    if (body == null) return Collections.emptyList();

    SchemaTypeDef typeDef = (SchemaTypeDef) body.getParent();
    if (typeDef == null) return Collections.emptyList();

    return getFieldDecls(typeDef, fieldName, types);
  }

  private static List<SchemaFieldDecl> getFieldDecls(@NotNull SchemaTypeDef typeDef,
                                                     @Nullable String fieldName,
                                                     @NotNull List<SchemaTypeDef> types) {
    if (types.isEmpty()) return Collections.emptyList();

    return types.stream()
        .filter(type -> type instanceof SchemaRecordTypeDef)
        .flatMap(type -> {
          SchemaRecordTypeDef recordTypeDef = (SchemaRecordTypeDef) type;
          SchemaRecordTypeBody recordTypeBody = recordTypeDef.getRecordTypeBody();
          if (recordTypeBody != null) {
            return recordTypeBody.getFieldDeclList().stream().filter(f -> fieldName == null || fieldName.equals(f.getName()));
          } else {
            return Stream.empty();
          }
        })
        .collect(Collectors.toList());
  }

  @NotNull
  private static List<SchemaVarTagDecl> getSameNameTags(@NotNull SchemaVarTagDecl varTagDecl,
                                                        @NotNull List<SchemaTypeDef> types) {
    final String varTypeMemberName = varTagDecl.getName();
    if (varTypeMemberName == null) return Collections.emptyList();

    PsiElement body = varTagDecl.getParent();
    if (body == null) return Collections.emptyList();

    SchemaTypeDef typeDef = (SchemaTypeDef) body.getParent();
    if (typeDef == null) return Collections.emptyList();

    return getVarTagDecls(typeDef, varTypeMemberName, types);
  }

  private static List<SchemaVarTagDecl> getVarTagDecls(@NotNull SchemaTypeDef typeDef,
                                                       @Nullable String varTagName,
                                                       @NotNull List<SchemaTypeDef> types) {
    if (types.isEmpty()) return Collections.emptyList();

    return types.stream()
        .filter(type -> type instanceof SchemaVarTypeDef)
        .flatMap(type -> {
          SchemaVarTypeDef varTypeDef = (SchemaVarTypeDef) type;
          SchemaVarTypeBody varTypeBody = varTypeDef.getVarTypeBody();
          if (varTypeBody != null) {
            return varTypeBody.getVarTagDeclList().stream().filter(f -> varTagName == null || varTagName.equals(f.getName()));
          } else {
            return Stream.empty();
          }
        })
        .collect(Collectors.toList());
  }
}
