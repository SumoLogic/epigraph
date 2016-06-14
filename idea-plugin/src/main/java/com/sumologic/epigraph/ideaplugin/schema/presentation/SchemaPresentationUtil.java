package com.sumologic.epigraph.ideaplugin.schema.presentation;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.epigraph.schema.parser.Common;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.*;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPresentationUtil {
  // TODO own icons!
  public static final Icon SCHEMA_FILE_ICON = AllIcons.FileTypes.Custom;

  public static final Icon PARENT_TYPES_GUTTER_ICON = AllIcons.Gutter.OverridingMethod;
  public static final Icon CHILD_TYPES_GUTTER_ICON = AllIcons.Gutter.OverridenMethod;
  public static final Icon SUPPLEMENTS_GUTTER_ICON = AllIcons.Gutter.ExtAnnotation;

  public static final Icon OVERRIDEN_FIELD_GUTTER_ICON = AllIcons.Gutter.OverridenMethod;
  public static final Icon OVERRIDING_FIELD_GUTTER_ICON = AllIcons.Gutter.OverridingMethod;
  public static final Icon OVERRIDEN_TAG_GUTTER_ICON = AllIcons.Gutter.ImplementedMethod;
  public static final Icon OVERRIDING_TAG_GUTTER_ICON = AllIcons.Gutter.ImplementingMethod;

  @Nullable
  public static String getName(@NotNull PsiNamedElement element, boolean qualified) {
    if (element instanceof SchemaFqnSegment) {
      SchemaFqnSegment fqnSegment = (SchemaFqnSegment) element;
      return fqnSegment.getFqn().toString();
    }

    String shortName = element.getName();
    if (shortName == null) return null;

    if (qualified) {
      Fqn namespace;
      if (element instanceof SchemaTypeDef) {
        SchemaTypeDef typeDef = (SchemaTypeDef) element;
        namespace = typeDef.getNamespace();
      } else namespace = NamespaceManager.getNamespace(element);
      return namespace == null ? shortName : namespace.append(shortName).toString();
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
    if (element instanceof SchemaVarTagDecl)
      return AllIcons.Nodes.Function; // IconUtil.addText(icon, "D") for default member?

    // TODO icons for all!
    return null;
  }

  @NotNull
  static String getPresentableText(@NotNull PsiElement element, boolean structureView) {
    if (element instanceof SchemaFile) {
      String name = ((SchemaFile) element).getName();
      return name.substring(name.length() - Common.FILE_EXTENSION.length());
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

    if (element instanceof SchemaVarTagDecl) {
      SchemaVarTagDecl varTagDecl = (SchemaVarTagDecl) element;
      String name = varTagDecl.getId().getText();

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

    return "(getPresentableText) Unknown element: " + element.getClass();
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
    Fqn namespace;

    if (element instanceof SchemaTypeDef) {
      SchemaTypeDef typeDef = (SchemaTypeDef) element;
      namespace = typeDef.getNamespace();
    } else {
      namespace = NamespaceManager.getNamespace(element);
    }

    if (namespace != null) return namespace.toString();

    try {
      PsiFile containingFile = element.getContainingFile();
      if (containingFile == null) return "[invalid]";
      return containingFile.getName();
    } catch (PsiInvalidElementAccessException e) {
      return "[invalid]"; // file not available
    }
  }

  @NotNull
  public static String getType(@NotNull PsiElement element) {
    if (element instanceof SchemaListTypeDef) return "List type";
    if (element instanceof SchemaPrimitiveTypeDef) return "Primitive type";
    if (element instanceof SchemaEnumTypeDef) return "Enum type";
    if (element instanceof SchemaVarTypeDef) return "Var type";
    if (element instanceof SchemaRecordTypeDef) return "Record type";
    if (element instanceof SchemaMapTypeDef) return "Map type";
    if (element instanceof SchemaFqnSegment) return "Namespace";
    if (element instanceof SchemaVarTagDecl) return "Var tag";
    if (element instanceof SchemaFieldDecl) return "Record field";

    return "(getType) Unknown element: " + element;
  }

  @NotNull
  public static String psiToString(@NotNull PsiElement element) {
    return element.getClass().getSimpleName() + "(" + element.getNode().getElementType().toString() + ")";
  }

}
