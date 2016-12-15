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

package ws.epigraph.ideaplugin.edl.features.hierarchy;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import ws.epigraph.ideaplugin.edl.brains.hierarchy.HierarchyCache;
import ws.epigraph.ideaplugin.edl.brains.hierarchy.TypeMembers;
import ws.epigraph.ideaplugin.edl.presentation.EdlPresentationUtil;
import ws.epigraph.edl.parser.psi.EdlFieldDecl;
import ws.epigraph.edl.parser.psi.EdlSupplementDef;
import ws.epigraph.edl.parser.psi.EdlTypeDef;
import ws.epigraph.edl.parser.psi.EdlVarTagDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static ws.epigraph.edl.lexer.EdlElementTypes.E_QID;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlLineMarkerProvider extends RelatedItemLineMarkerProvider {
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

    if (parent instanceof EdlTypeDef) {
      EdlTypeDef typeDef = (EdlTypeDef) parent;

      List<EdlSupplementDef> supplements = HierarchyCache.getHierarchyCache(project).getSupplementsBySupplemented(typeDef);

      if (!supplements.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(EdlPresentationUtil.SUPPLEMENTS_GUTTER_ICON)
                .setTargets(supplements)
                .setAlignment(GutterIconRenderer.Alignment.CENTER)
                .setTooltipText("Navigate to supplement");

        result.add(builder.createLineMarkerInfo(element));
      }

      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(project);

      Collection<EdlTypeDef> parents = hierarchyCache.getDirectTypeParents(typeDef);
      if (!parents.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(EdlPresentationUtil.PARENT_TYPES_GUTTER_ICON)
                .setTargets(parents)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent");

        result.add(builder.createLineMarkerInfo(element));
      }

      Collection<EdlTypeDef> children = hierarchyCache.getDirectTypeInheritors(typeDef);
      if (!children.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(EdlPresentationUtil.CHILD_TYPES_GUTTER_ICON)
                .setTargets(children)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTooltipText("Navigate to child");

        result.add(builder.createLineMarkerInfo(element));
      }

    }

    if (parent instanceof EdlFieldDecl) {
      EdlFieldDecl fieldDecl = (EdlFieldDecl) parent;

      List<EdlFieldDecl> overridenFields = TypeMembers.getOverridenFields(fieldDecl);
      if (!overridenFields.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(EdlPresentationUtil.OVERRIDING_FIELD_GUTTER_ICON)
                .setTargets(overridenFields)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent field");

        result.add(builder.createLineMarkerInfo(element));
      }

      List<EdlFieldDecl> overridingFields = TypeMembers.getOverridingFields(fieldDecl);
      if (!overridingFields.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(EdlPresentationUtil.OVERRIDEN_FIELD_GUTTER_ICON)
                .setTargets(overridingFields)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTooltipText("Navigate to overriding field");

        result.add(builder.createLineMarkerInfo(element));
      }
    }

    if (parent instanceof EdlVarTagDecl) {
      EdlVarTagDecl varTagDecl = (EdlVarTagDecl) parent;

      List<EdlVarTagDecl> overridenVarTypeMembers = TypeMembers.getOverridenTags(varTagDecl);
      if (!overridenVarTypeMembers.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(EdlPresentationUtil.OVERRIDING_TAG_GUTTER_ICON)
                .setTargets(overridenVarTypeMembers)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent tag");

        result.add(builder.createLineMarkerInfo(element));
      }

      List<EdlVarTagDecl> overridingVarTypeMembers = TypeMembers.getOverridingTags(varTagDecl);
      if (!overridingVarTypeMembers.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(EdlPresentationUtil.OVERRIDEN_TAG_GUTTER_ICON)
                .setTargets(overridingVarTypeMembers)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTooltipText("Navigate to overriding tag");

        result.add(builder.createLineMarkerInfo(element));
      }
    }
  }
}
