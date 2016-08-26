package com.sumologic.epigraph.ideaplugin.schema.features.hierarchy;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.TypeMembers;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import io.epigraph.lang.parser.psi.EpigraphFieldDecl;
import io.epigraph.lang.parser.psi.EpigraphSupplementDef;
import io.epigraph.lang.parser.psi.EpigraphVarTagDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static io.epigraph.lang.lexer.EpigraphElementTypes.E_QID;

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
    if (element.getNode().getElementType() != E_QID) return;

    PsiElement parent = element.getParent();

    if (parent instanceof EpigraphTypeDef) {
      EpigraphTypeDef typeDef = (EpigraphTypeDef) parent;

      List<EpigraphSupplementDef> supplements = HierarchyCache.getHierarchyCache(project).getSupplementsBySupplemented(typeDef);

      if (!supplements.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.SUPPLEMENTS_GUTTER_ICON)
                .setTargets(supplements)
                .setAlignment(GutterIconRenderer.Alignment.CENTER)
                .setTooltipText("Navigate to supplement");

        result.add(builder.createLineMarkerInfo(element));
      }

      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(project);

      Collection<EpigraphTypeDef> parents = hierarchyCache.getDirectTypeParents(typeDef);
      if (!parents.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.PARENT_TYPES_GUTTER_ICON)
                .setTargets(parents)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent");

        result.add(builder.createLineMarkerInfo(element));
      }

      Collection<EpigraphTypeDef> children = hierarchyCache.getDirectTypeInheritors(typeDef);
      if (!children.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.CHILD_TYPES_GUTTER_ICON)
                .setTargets(children)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTooltipText("Navigate to child");

        result.add(builder.createLineMarkerInfo(element));
      }

    }

    if (parent instanceof EpigraphFieldDecl) {
      EpigraphFieldDecl fieldDecl = (EpigraphFieldDecl) parent;

      List<EpigraphFieldDecl> overridenFields = TypeMembers.getOverridenFields(fieldDecl);
      if (!overridenFields.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDING_FIELD_GUTTER_ICON)
                .setTargets(overridenFields)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent field");

        result.add(builder.createLineMarkerInfo(element));
      }

      List<EpigraphFieldDecl> overridingFields = TypeMembers.getOverridingFields(fieldDecl);
      if (!overridingFields.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDEN_FIELD_GUTTER_ICON)
                .setTargets(overridingFields)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTooltipText("Navigate to overriding field");

        result.add(builder.createLineMarkerInfo(element));
      }
    }

    if (parent instanceof EpigraphVarTagDecl) {
      EpigraphVarTagDecl varTagDecl = (EpigraphVarTagDecl) parent;

      List<EpigraphVarTagDecl> overridenVarTypeMembers = TypeMembers.getOverridenTags(varTagDecl);
      if (!overridenVarTypeMembers.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDING_TAG_GUTTER_ICON)
                .setTargets(overridenVarTypeMembers)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent tag");

        result.add(builder.createLineMarkerInfo(element));
      }

      List<EpigraphVarTagDecl> overridingVarTypeMembers = TypeMembers.getOverridingTags(varTagDecl);
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
