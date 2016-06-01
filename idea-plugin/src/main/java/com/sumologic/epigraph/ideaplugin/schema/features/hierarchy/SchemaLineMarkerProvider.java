package com.sumologic.epigraph.ideaplugin.schema.features.hierarchy;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.InheritedMembers;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.SchemaDirectTypeParentsSearch;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFieldDecl;
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

    if (element.getNode().getElementType() != S_ID) return;

    PsiElement parent = element.getParent();

    if (parent instanceof SchemaTypeDef) {
      SchemaTypeDef typeDef = (SchemaTypeDef) parent;

      Collection<SchemaTypeDef> supplementedBy = SchemaDirectTypeParentsSearch.search(
          new SchemaDirectTypeParentsSearch.SearchParameters(
              typeDef, false, true, true
          )
      ).findAll();

      if (!supplementedBy.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.supplementedGutterIcon())
                .setTargets(supplementedBy)
                .setTooltipText("Navigate to supplement");

        result.add(builder.createLineMarkerInfo(element));
      }
    }

    if (parent instanceof SchemaFieldDecl) {
      SchemaFieldDecl fieldDecl = (SchemaFieldDecl) parent;

      List<SchemaFieldDecl> overridenFields = InheritedMembers.getOverridenFields(fieldDecl);
      if (!overridenFields.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.overridenFieldGutterIcon())
                .setTargets(overridenFields)
                .setTooltipText("Navigate to overriden field");

        result.add(builder.createLineMarkerInfo(element));
      }

      List<SchemaFieldDecl> overridingFields = InheritedMembers.getOverridingFields(fieldDecl);
      if (!overridingFields.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.overridingFieldGutterIcon())
                .setTargets(overridingFields)
                .setTooltipText("Navigate to overriding field");

        result.add(builder.createLineMarkerInfo(element));
      }
    }

    if (parent instanceof SchemaVarTypeMemberDecl) {
      SchemaVarTypeMemberDecl varTypeMemberDecl = (SchemaVarTypeMemberDecl) parent;

      List<SchemaVarTypeMemberDecl> overridenVarTypeMembers = InheritedMembers.getOverridenTags(varTypeMemberDecl);
      if (!overridenVarTypeMembers.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.overridenTagGutterIcon())
                .setTargets(overridenVarTypeMembers)
                .setTooltipText("Navigate to overriden varTypeMember");

        result.add(builder.createLineMarkerInfo(element));
      }

      List<SchemaVarTypeMemberDecl> overridingVarTypeMembers = InheritedMembers.getOverridingTags(varTypeMemberDecl);
      if (!overridingVarTypeMembers.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(SchemaPresentationUtil.overridingTagGutterIcon())
                .setTargets(overridingVarTypeMembers)
                .setTooltipText("Navigate to overriding varTypeMember");

        result.add(builder.createLineMarkerInfo(element));
      }
    }
  }
}
