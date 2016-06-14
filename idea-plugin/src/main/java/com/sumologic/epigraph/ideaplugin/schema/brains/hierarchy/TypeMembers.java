package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.sumologic.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class TypeMembers {
  @NotNull
  public static List<SchemaFieldDecl> getOverridenFields(@NotNull SchemaFieldDecl fieldDecl) {
    Project project = fieldDecl.getProject();
    return getSameNameFields(fieldDecl, typeDef -> HierarchyCache.getHierarchyCache(project).getTypeParents(typeDef));
  }

  @NotNull
  public static List<SchemaFieldDecl> getOverridingFields(@NotNull SchemaFieldDecl fieldDecl) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(fieldDecl.getProject());
    return getSameNameFields(fieldDecl, hierarchyCache::getTypeInheritors);
  }

  @NotNull
  public static List<SchemaVarTagDecl> getOverridenTags(@NotNull SchemaVarTagDecl varTagDecl) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(varTagDecl.getProject());
    return getSameNameTags(varTagDecl, hierarchyCache::getTypeParents);
  }

  @NotNull
  public static List<SchemaVarTagDecl> getOverridingTags(@NotNull SchemaVarTagDecl varTagDecl) {
    Project project = varTagDecl.getProject();
    return getSameNameTags(varTagDecl, typeDef -> HierarchyCache.getHierarchyCache(project).getTypeInheritors(typeDef));
  }

  @NotNull
  public static List<SchemaFieldDecl> getFieldDecls(@NotNull SchemaTypeDef hostType, @NotNull String fieldName) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(hostType.getProject());
    return getFieldDecls(hostType, fieldName, hierarchyCache::getTypeParents);
  }

  @NotNull
  public static List<SchemaVarTagDecl> getVarTagDecls(@NotNull SchemaTypeDef hostType, @NotNull String tagName) {
    final HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(hostType.getProject());
    return getVarTagDecls(hostType, tagName, hierarchyCache::getTypeParents);
  }

  @NotNull
  private static List<SchemaFieldDecl> getSameNameFields(@NotNull SchemaFieldDecl fieldDecl,
                                                         @NotNull Function<SchemaTypeDef, List<SchemaTypeDef>> typesProvider) {
    final String fieldName = fieldDecl.getName();
    if (fieldName == null) return Collections.emptyList();

    PsiElement body = fieldDecl.getParent();
    if (body == null) return Collections.emptyList();

    SchemaTypeDef typeDef = (SchemaTypeDef) body.getParent();
    if (typeDef == null) return Collections.emptyList();

    return getFieldDecls(typeDef, fieldName, typesProvider);
  }

  private static List<SchemaFieldDecl> getFieldDecls(@NotNull SchemaTypeDef typeDef,
                                                     @NotNull String fieldName,
                                                     @NotNull Function<SchemaTypeDef, List<SchemaTypeDef>> typesProvider) {
    List<SchemaTypeDef> parents = typesProvider.apply(typeDef);
    if (parents.isEmpty()) return Collections.emptyList();

    return parents.stream()
        .filter(parent -> parent instanceof SchemaRecordTypeDef)
        .flatMap(parent -> {
          SchemaRecordTypeDef recordTypeDef = (SchemaRecordTypeDef) parent;
          SchemaRecordTypeBody recordTypeBody = recordTypeDef.getRecordTypeBody();
          if (recordTypeBody != null) {
            return recordTypeBody.getFieldDeclList().stream().filter(f -> fieldName.equals(f.getName()));
          } else {
            return Stream.empty();
          }
        })
        .collect(Collectors.toList());
  }

  @NotNull
  private static List<SchemaVarTagDecl> getSameNameTags(@NotNull SchemaVarTagDecl varTagDecl,
                                                               @NotNull Function<SchemaTypeDef, List<SchemaTypeDef>> typesProvider) {
    final String varTypeMemberName = varTagDecl.getName();
    if (varTypeMemberName == null) return Collections.emptyList();

    PsiElement body = varTagDecl.getParent();
    if (body == null) return Collections.emptyList();

    SchemaTypeDef typeDef = (SchemaTypeDef) body.getParent();
    if (typeDef == null) return Collections.emptyList();

    return getVarTagDecls(typeDef, varTypeMemberName, typesProvider);
  }

  private static List<SchemaVarTagDecl> getVarTagDecls(@NotNull SchemaTypeDef typeDef,
                                                                     @NotNull String varTypeMemberName,
                                                                     @NotNull Function<SchemaTypeDef, List<SchemaTypeDef>> typesProvider) {
    List<SchemaTypeDef> parents = typesProvider.apply(typeDef);
    if (parents.isEmpty()) return Collections.emptyList();

    return parents.stream()
        .filter(parent -> parent instanceof SchemaVarTypeDef)
        .flatMap(parent -> {
          SchemaVarTypeDef varTypeDef = (SchemaVarTypeDef) parent;
          SchemaVarTypeBody varTypeBody = varTypeDef.getVarTypeBody();
          if (varTypeBody != null) {
            return varTypeBody.getVarTagDeclList().stream().filter(f -> varTypeMemberName.equals(f.getName()));
          } else {
            return Stream.empty();
          }
        })
        .collect(Collectors.toList());
  }
}
