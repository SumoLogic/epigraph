package com.sumologic.epigraph.ideaplugin.schema.features.hierarchy;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.InheritedMembers;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFieldDecl;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaSupplementDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaVarTypeMemberDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.S_ID;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaLineMarkerProvider extends RelatedItemLineMarkerProvider {
  @Nullable
  @Override
  public String getName() {
    return "Epigraph line markers";
  }

  @Override
  protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
    Project project = element.getProject();
    if (element.getNode().getElementType() != S_ID) return;

    PsiElement parent = element.getParent();

    if (parent instanceof SchemaTypeDef) {
      SchemaTypeDef typeDef = (SchemaTypeDef) parent;

      List<SchemaSupplementDef> supplements = HierarchyCache.getHierarchyCache(project).getSupplementsBySupplemented(typeDef);

      if (!supplements.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.SUPPLEMENTS_GUTTER_ICON)
                .setTargets(supplements)
                .setAlignment(GutterIconRenderer.Alignment.CENTER)
                .setTooltipText("Navigate to supplement");

        result.add(builder.createLineMarkerInfo(element));
      }

      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(project);

      Collection<SchemaTypeDef> parents = hierarchyCache.getDirectTypeParents(typeDef);
      if (!parents.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.PARENT_TYPES_GUTTER_ICON)
                .setTargets(parents)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent");

        result.add(builder.createLineMarkerInfo(element));
      }

      Collection<SchemaTypeDef> children = hierarchyCache.getDirectTypeInheritors(typeDef);
      if (!children.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.CHILD_TYPES_GUTTER_ICON)
                .setTargets(children)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTooltipText("Navigate to child");

        result.add(builder.createLineMarkerInfo(element));
      }

    }

    if (parent instanceof SchemaFieldDecl) {
      SchemaFieldDecl fieldDecl = (SchemaFieldDecl) parent;

      List<SchemaFieldDecl> overridenFields = InheritedMembers.getOverridenFields(fieldDecl);
      if (!overridenFields.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDING_FIELD_GUTTER_ICON)
                .setTargets(overridenFields)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent field");

        result.add(builder.createLineMarkerInfo(element));
      }

      List<SchemaFieldDecl> overridingFields = InheritedMembers.getOverridingFields(fieldDecl);
      if (!overridingFields.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDEN_FIELD_GUTTER_ICON)
                .setTargets(overridingFields)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTooltipText("Navigate to overriding field");

        result.add(builder.createLineMarkerInfo(element));
      }
    }

    if (parent instanceof SchemaVarTypeMemberDecl) {
      SchemaVarTypeMemberDecl varTypeMemberDecl = (SchemaVarTypeMemberDecl) parent;

      List<SchemaVarTypeMemberDecl> overridenVarTypeMembers = InheritedMembers.getOverridenTags(varTypeMemberDecl);
      if (!overridenVarTypeMembers.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDING_TAG_GUTTER_ICON)
                .setTargets(overridenVarTypeMembers)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent tag");

        result.add(builder.createLineMarkerInfo(element));
      }

      List<SchemaVarTypeMemberDecl> overridingVarTypeMembers = InheritedMembers.getOverridingTags(varTypeMemberDecl);
      if (!overridingVarTypeMembers.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDEN_TAG_GUTTER_ICON)
                .setTargets(overridingVarTypeMembers)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTooltipText("Navigate to overriding tag");

        result.add(builder.createLineMarkerInfo(element));
      }
    }
  }
}
