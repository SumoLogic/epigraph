/*
 * Copyright 2018 Sumo Logic
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

package ws.epigraph.ideaplugin.schema.features.hierarchy;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.TypeMembers;
import ws.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import ws.epigraph.schema.parser.psi.SchemaFieldDecl;
import ws.epigraph.schema.parser.psi.SchemaSupplementDef;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import ws.epigraph.schema.parser.psi.SchemaEntityTagDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static ws.epigraph.schema.lexer.SchemaElementTypes.S_QID;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaLineMarkerProvider extends RelatedItemLineMarkerProvider {
  @Override
  public @Nullable String getName() {
    return "Epigraph line markers";
  }

  @Override
  protected void collectNavigationMarkers(
      @NotNull PsiElement element,
      @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {

    Project project = element.getProject();

    PsiElement parent = element.getParent();
    if (parent == null || parent.getNode() == null || parent.getNode().getElementType() != S_QID) return;

    parent = parent.getParent();

    if (parent instanceof SchemaTypeDef) {
      SchemaTypeDef typeDef = (SchemaTypeDef) parent;

      List<SchemaSupplementDef> supplements =
          HierarchyCache.getHierarchyCache(project).getSupplementsBySupplemented(typeDef);

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

      List<SchemaFieldDecl> overridenFields = TypeMembers.getOverridenFields(fieldDecl);
      if (!overridenFields.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDING_FIELD_GUTTER_ICON)
                .setTargets(overridenFields)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent field");

        result.add(builder.createLineMarkerInfo(element));
      }

      List<SchemaFieldDecl> overridingFields = TypeMembers.getOverridingFields(fieldDecl);
      if (!overridingFields.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDEN_FIELD_GUTTER_ICON)
                .setTargets(overridingFields)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTooltipText("Navigate to overriding field");

        result.add(builder.createLineMarkerInfo(element));
      }
    }

    if (parent instanceof SchemaEntityTagDecl) {
      SchemaEntityTagDecl varTagDecl = (SchemaEntityTagDecl) parent;

      List<SchemaEntityTagDecl> overridenEntityTypeMembers = TypeMembers.getOverridenTags(varTagDecl);
      if (!overridenEntityTypeMembers.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDING_TAG_GUTTER_ICON)
                .setTargets(overridenEntityTypeMembers)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent tag");

        result.add(builder.createLineMarkerInfo(element));
      }

      List<SchemaEntityTagDecl> overridingEntityTypeMembers = TypeMembers.getOverridingTags(varTagDecl);
      if (!overridingEntityTypeMembers.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDEN_TAG_GUTTER_ICON)
                .setTargets(overridingEntityTypeMembers)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTooltipText("Navigate to overriding tag");

        result.add(builder.createLineMarkerInfo(element));
      }
    }
  }
}
