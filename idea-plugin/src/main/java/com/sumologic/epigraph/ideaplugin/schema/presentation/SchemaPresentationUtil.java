package com.sumologic.epigraph.ideaplugin.schema.presentation;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPresentationUtil {
  // TODO own icons!
  public static final Icon SCHEMA_FILE_ICON = AllIcons.FileTypes.Properties;

  public static final Icon PARENT_TYPES_GUTTER_ICON = AllIcons.Gutter.OverridingMethod;
  public static final Icon CHILD_TYPES_GUTTER_ICON = AllIcons.Gutter.OverridenMethod;
  public static final Icon SUPPLEMENTS_GUTTER_ICON = AllIcons.Gutter.ExtAnnotation;

  public static final Icon OVERRIDEN_FIELD_GUTTER_ICON = AllIcons.Gutter.OverridenMethod;
  public static final Icon OVERRIDING_FIELD_GUTTER_ICON = AllIcons.Gutter.OverridingMethod;
  public static final Icon OVERRIDEN_TAG_GUTTER_ICON = AllIcons.Gutter.ImplementedMethod;
  public static final Icon OVERRIDING_TAG_GUTTER_ICON = AllIcons.Gutter.ImplementingMethod;

  @Nullable
  public static String getName(@NotNull PsiNamedElement element, boolean qualified) {
    String shortName = element.getName();
    if (qualified) {
      String namespace;
      if (element instanceof SchemaTypeDef) {
        SchemaTypeDef typeDef = (SchemaTypeDef) element;
        namespace = typeDef.getNamespace();
      } else namespace = NamespaceManager.getNamespace(element);
      return namespace == null ? shortName : namespace + '.' + shortName;
    } else return shortName;
  }

  @Nullable
  public static Icon getIcon(@NotNull PsiElement element) {
    if (element instanceof SchemaFile) return SCHEMA_FILE_ICON;

    if (element instanceof SchemaRecordTypeDef) return AllIcons.Nodes.Class;
    if (element instanceof SchemaEnumTypeDef) return AllIcons.Nodes.Enum;
    if (element instanceof SchemaVarTypeDef) return AllIcons.Nodes.Interface;

    if (element instanceof SchemaTypeDef) return AllIcons.Nodes.Class;

    if (element instanceof SchemaSupplementDef) return AllIcons.Nodes.Aspect;

    if (element instanceof SchemaCustomParam) return AllIcons.Nodes.Annotationtype;
    if (element instanceof SchemaFieldDecl) return AllIcons.Nodes.Field;
    if (element instanceof SchemaEnumMemberDecl) return AllIcons.Nodes.Property;
    if (element instanceof SchemaVarTypeMemberDecl)
      return AllIcons.Nodes.Function; // IconUtil.addText(icon, "D") for default member?

    // TODO icons for all!
    return null;
  }

  @NotNull
  static String getPresentableText(@NotNull PsiElement element, boolean structureView) {
    if (element instanceof SchemaFile) {
      return ((SchemaFile) element).getName();
    }

    if (element instanceof SchemaTypeDef) {
      SchemaTypeDef schemaTypeDef = (SchemaTypeDef) element;

      PsiElement id = schemaTypeDef.getNameIdentifier();
      return id == null ? "" : id.getText();
    }

    if (element instanceof SchemaCustomParam) {
      SchemaCustomParam schemaCustomParam = (SchemaCustomParam) element;
      return schemaCustomParam.getId().getText();
    }

    if (element instanceof SchemaFieldDecl) {
      SchemaFieldDecl schemaFieldDecl = (SchemaFieldDecl) element;
      String name = schemaFieldDecl.getId().getText();

      if (structureView) return name;

      SchemaRecordTypeDef recordTypeDef = PsiTreeUtil.getParentOfType(element, SchemaRecordTypeDef.class);
      String typeName = recordTypeDef == null ? null : recordTypeDef.getName();
      typeName = typeName == null ? "???" : typeName;

      return typeName + '.' + name;
    }

    if (element instanceof SchemaVarTypeMemberDecl) {
      SchemaVarTypeMemberDecl varTypeMemberDecl = (SchemaVarTypeMemberDecl) element;
      String name = varTypeMemberDecl.getId().getText();

      if (structureView) return name;

      SchemaVarTypeDef varTypeDef = PsiTreeUtil.getParentOfType(element, SchemaVarTypeDef.class);
      String varTypeName = varTypeDef == null ? null : varTypeDef.getName();
      varTypeName = varTypeName == null ? "???" : varTypeName;

      return varTypeName + '.' + name;
    }

    if (element instanceof SchemaEnumMemberDecl) {
      SchemaEnumMemberDecl schemaEnumMemberDecl = (SchemaEnumMemberDecl) element;
      return schemaEnumMemberDecl.getId().getText();
    }

    if (element instanceof SchemaSupplementDef) {
      SchemaSupplementDef schemaSupplementDef = (SchemaSupplementDef) element;

      StringBuilder name = new StringBuilder();

      List<SchemaFqnTypeRef> fqnTypeRef = schemaSupplementDef.supplementedRefs();
      for (SchemaFqnTypeRef typeRef : fqnTypeRef) {
        if (name.length() > 0) name.append(", ");
        name.append(typeRef.getFqn().getFqn().toString());
      }
      name.append(" with ");
      SchemaTypeDef source = schemaSupplementDef.source();
      name.append(source == null ? "???" : source.getName());

      return "supplement " + name;
    }

    return "Unknown getElement: " + element.getClass();
  }

  @NotNull
  public static ItemPresentation getPresentation(@NotNull PsiElement element, boolean structureView) {
    final String presentableTest = getPresentableText(element, structureView);
    final String location = getNamespaceString(element);
    final Icon icon = getIcon(element);

    return new StaticItemPresentation(presentableTest, location, icon);
  }

  @NotNull
  public static String getNamespaceString(@NotNull PsiElement element) {
    String namespace;

    if (element instanceof SchemaTypeDef) {
      SchemaTypeDef typeDef = (SchemaTypeDef) element;
      namespace = typeDef.getNamespace();
    } else {
      namespace = NamespaceManager.getNamespace(element);
    }

    if (namespace != null) return namespace;

    try {
      PsiFile containingFile = element.getContainingFile();
      if (containingFile == null) return "[invalid]";
      return containingFile.getName();
    } catch (PsiInvalidElementAccessException e) {
      return "[invalid]"; // file not available
    }

  }

  @NotNull
  public static String psiToString(@NotNull PsiElement element) {
    return element.getClass().getSimpleName() + "(" + element.getNode().getElementType().toString() + ")";
  }

  public static class StaticItemPresentation implements ItemPresentation {
    @Nullable
    private final String presentableText;
    @Nullable
    private final String locationString;
    @Nullable
    private final Icon icon;

    public StaticItemPresentation(@Nullable String presentableText, @Nullable String locationString, @Nullable Icon icon) {
      this.presentableText = presentableText;
      this.locationString = locationString;
      this.icon = icon;
    }

    @Nullable
    @Override
    public String getPresentableText() {
      return presentableText;
    }

    @Nullable
    @Override
    public String getLocationString() {
      return locationString;
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
      return icon;
    }
  }
}