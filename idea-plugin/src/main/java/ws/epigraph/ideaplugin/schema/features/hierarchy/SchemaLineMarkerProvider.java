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
import ws.epigraph.schema.parser.psi.SchemaVarTagDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static ws.epigraph.schema.lexer.SchemaElementTypes.S_QID;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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
    if (element.getNode().getElementType() != S_QID) return;

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

    if (parent instanceof SchemaVarTagDecl) {
      SchemaVarTagDecl varTagDecl = (SchemaVarTagDecl) parent;

      List<SchemaVarTagDecl> overridenVarTypeMembers = TypeMembers.getOverridenTags(varTagDecl);
      if (!overridenVarTypeMembers.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.OVERRIDING_TAG_GUTTER_ICON)
                .setTargets(overridenVarTypeMembers)
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setTooltipText("Navigate to parent tag");

        result.add(builder.createLineMarkerInfo(element));
      }

      List<SchemaVarTagDecl> overridingVarTypeMembers = TypeMembers.getOverridingTags(varTagDecl);
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
