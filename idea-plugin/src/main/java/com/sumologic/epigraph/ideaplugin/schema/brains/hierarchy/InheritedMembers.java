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
public class InheritedMembers {
  @NotNull
  public static List<SchemaFieldDecl> getOverridenFields(@NotNull SchemaFieldDecl fieldDecl) {
    Project project = fieldDecl.getProject();
    return getSameNameFields(fieldDecl, typeDef -> HierarchyCache.getHierarchyCache(project).getTypeParents(typeDef));
  }

  @NotNull
  public static List<SchemaFieldDecl> getOverridingFields(@NotNull SchemaFieldDecl fieldDecl) {
    Project project = fieldDecl.getProject();
    return getSameNameFields(fieldDecl, typeDef -> HierarchyCache.getHierarchyCache(project).getTypeInheritors(typeDef));
  }

  @NotNull
  public static List<SchemaVarTypeMemberDecl> getOverridenTags(@NotNull SchemaVarTypeMemberDecl varTypeMemberDecl) {
    Project project = varTypeMemberDecl.getProject();
    return getSameNameTags(varTypeMemberDecl, typeDef -> HierarchyCache.getHierarchyCache(project).getTypeParents(typeDef));
  }

  @NotNull
  public static List<SchemaVarTypeMemberDecl> getOverridingTags(@NotNull SchemaVarTypeMemberDecl varTypeMemberDecl) {
    Project project = varTypeMemberDecl.getProject();
    return getSameNameTags(varTypeMemberDecl, typeDef -> HierarchyCache.getHierarchyCache(project).getTypeInheritors(typeDef));
  }

  @NotNull
  private static List<SchemaFieldDecl> getSameNameFields(@NotNull SchemaFieldDecl fieldDecl, Function<SchemaTypeDef, List<SchemaTypeDef>> typesProvider) {
    PsiElement body = fieldDecl.getParent();
    if (body == null) return Collections.emptyList();

    SchemaTypeDef typeDef = (SchemaTypeDef) body.getParent();
    if (typeDef == null) return Collections.emptyList();

    List<SchemaTypeDef> parents = typesProvider.apply(typeDef);
    if (parents.isEmpty()) return Collections.emptyList();

    final String fieldName = fieldDecl.getName();
    if (fieldName == null) return Collections.emptyList();

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
  private static List<SchemaVarTypeMemberDecl> getSameNameTags(@NotNull SchemaVarTypeMemberDecl varTypeMemberDecl, Function<SchemaTypeDef, List<SchemaTypeDef>> typesProvider) {
    PsiElement body = varTypeMemberDecl.getParent();
    if (body == null) return Collections.emptyList();

    SchemaTypeDef typeDef = (SchemaTypeDef) body.getParent();
    if (typeDef == null) return Collections.emptyList();

    List<SchemaTypeDef> parents = typesProvider.apply(typeDef);
    if (parents.isEmpty()) return Collections.emptyList();

    final String varTypeMemberName = varTypeMemberDecl.getName();
    if (varTypeMemberName == null) return Collections.emptyList();

    return parents.stream()
        .filter(parent -> parent instanceof SchemaVarTypeDef)
        .flatMap(parent -> {
          SchemaVarTypeDef varTypeDef = (SchemaVarTypeDef) parent;
          SchemaVarTypeBody varTypeBody = varTypeDef.getVarTypeBody();
          if (varTypeBody != null) {
            return varTypeBody.getVarTypeMemberDeclList().stream().filter(f -> varTypeMemberName.equals(f.getName()));
          } else {
            return Stream.empty();
          }
        })
        .collect(Collectors.toList());
  }
}
