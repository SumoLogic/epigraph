package com.sumologic.dohyo.plugin.schema.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.util.IconUtil;
import com.intellij.util.PlatformIcons;
import com.sumologic.dohyo.plugin.schema.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaStructureViewElement implements StructureViewTreeElement {
  private final NavigatablePsiElement element;

  public SchemaStructureViewElement(PsiElement element) {
    this.element = (NavigatablePsiElement) element; // TODO
  }

  @Override
  public Object getValue() {
    return element;
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    if (element instanceof SchemaFile) {
      SchemaFile schemaFile = (SchemaFile) element;

      String fqn = null;
      SchemaNamespaceDecl namespaceDecl = schemaFile.getNamespaceDecl();
      if (namespaceDecl != null) {
        SchemaFqn schemaFqn = namespaceDecl.getFqn();
        if (schemaFqn != null) fqn = schemaFqn.getFqnString();
      }

      return new StaticItemPresentation("Schema", fqn, null);
    }

    if (element instanceof SchemaTypeDef) {
      SchemaTypeDef schemaTypeDef = (SchemaTypeDef) element;

      Icon icon = null;
      if (schemaTypeDef instanceof SchemaRecordTypeDef) icon = PlatformIcons.CLASS_ICON;
      if (schemaTypeDef instanceof SchemaEnumTypeDef) icon = PlatformIcons.ENUM_ICON;
      if (schemaTypeDef instanceof SchemaMultiTypeDef) icon = PlatformIcons.INTERFACE_ICON;
      // TODO icons for union, map, list, primitive

      return new StaticItemPresentation(schemaTypeDef.getId().getText(), null, icon);
    }

    if (element instanceof SchemaCustomParam) {
      SchemaCustomParam schemaCustomParam = (SchemaCustomParam) element;
      return new StaticItemPresentation(schemaCustomParam.getId().getText(), null, PlatformIcons.ANNOTATION_TYPE_ICON);
    }

    if (element instanceof SchemaFieldDecl) {
      SchemaFieldDecl schemaFieldDecl = (SchemaFieldDecl) element;
      return new StaticItemPresentation(schemaFieldDecl.getId().getText(), null, PlatformIcons.FIELD_ICON);
    }

    if (element instanceof SchemaTagDecl) {
      SchemaTagDecl schemaTagDecl = (SchemaTagDecl) element;
      return new StaticItemPresentation(schemaTagDecl.getId().getText(), null, PlatformIcons.FIELD_ICON);
    }

    if (element instanceof SchemaMultiMemberDecl) {
      SchemaMultiMemberDecl schemaMultiMemberDecl = (SchemaMultiMemberDecl) element;
      Icon icon = PlatformIcons.FUNCTION_ICON;
      if (schemaMultiMemberDecl.getDefault() != null) {
        icon = IconUtil.addText(icon, "D"); // TODO separate icon for default member
      }

      return new StaticItemPresentation(schemaMultiMemberDecl.getId().getText(), null, icon);
    }

    if (element instanceof SchemaEnumMemberDecl) {
      SchemaEnumMemberDecl schemaEnumMemberDecl = (SchemaEnumMemberDecl) element;
      return new StaticItemPresentation(schemaEnumMemberDecl.getId().getText(), null, PlatformIcons.FIELD_ICON);
    }

    if (element instanceof SchemaSupplementDef) {
      SchemaSupplementDef schemaSupplementDef = (SchemaSupplementDef) element;
      SchemaFqn fqn = schemaSupplementDef.getFqn();
      String name = fqn == null ? "???" : fqn.getFqnString();
      return new StaticItemPresentation(name, null, PlatformIcons.ASPECT_ICON); // TODO icon
    }

    return new StaticItemPresentation("Unknown element: " + element.getClass(), null, null);
  }

  @NotNull
  @Override
  public TreeElement[] getChildren() {
    final Collection<PsiElement> children = new ArrayList<>();

    if (element instanceof CustomParamsHolder) {
      children.addAll(((CustomParamsHolder) element).getCustomParamList());
    }

    if (element instanceof SchemaFile) {
      SchemaDefs defs = ((SchemaFile) element).getDefs();
      if (defs != null) {
        children.addAll(defs.getTypeDefList());
        children.addAll(defs.getSupplementDefList()); // SchemaSupplementDef
      }
    } else if (element instanceof SchemaRecordTypeDef) {
      SchemaRecordTypeBody recordTypeBody = ((SchemaRecordTypeDef) element).getRecordTypeBody();
      if (recordTypeBody != null) {
        children.addAll(recordTypeBody.getCustomParamList()); // SchemaCustomParam
        children.addAll(recordTypeBody.getFieldDeclList()); // SchemaFieldDecl
      }
    } else if (element instanceof SchemaUnionTypeDef) {
      SchemaUnionTypeBody unionTypeBody = ((SchemaUnionTypeDef) element).getUnionTypeBody();
      if (unionTypeBody != null) {
        children.addAll(unionTypeBody.getCustomParamList());
        children.addAll(unionTypeBody.getTagDeclList()); // SchemaTagDecl
      }
    } else if (element instanceof SchemaMultiTypeDef) {
      SchemaMultiTypeBody multiTypeBody = ((SchemaMultiTypeDef) element).getMultiTypeBody();
      if (multiTypeBody != null) {
        children.addAll(multiTypeBody.getCustomParamList());
        children.addAll(multiTypeBody.getMultiMemberDeclList()); // SchemaMultiMemberDecl
      }
    } else if (element instanceof SchemaEnumTypeDef) {
      SchemaEnumTypeBody enumTypeBody = ((SchemaEnumTypeDef) element).getEnumTypeBody();
      if (enumTypeBody != null) {
        children.addAll(enumTypeBody.getCustomParamList());
        children.addAll(enumTypeBody.getEnumMemberDeclList()); // SchemaEnumMemberDecl
      }
    } else if (element instanceof SchemaMapTypeDef) {
      SchemaMapTypeBody mapTypeBody = ((SchemaMapTypeDef) element).getMapTypeBody();
      if (mapTypeBody != null) children.addAll(mapTypeBody.getCustomParamList());
    } else if (element instanceof SchemaListTypeDef) {
      SchemaListTypeBody listTypeBody = ((SchemaListTypeDef) element).getListTypeBody();
      if (listTypeBody != null) children.addAll(listTypeBody.getCustomParamList());
    } else if (element instanceof SchemaPrimitiveTypeDef) {
      SchemaPrimitiveTypeBody primitiveTypeBody = ((SchemaPrimitiveTypeDef) element).getPrimitiveTypeBody();
      if (primitiveTypeBody != null) children.addAll(primitiveTypeBody.getCustomParamList());
    }

    if (children.isEmpty()) return StructureViewTreeElement.EMPTY_ARRAY;

    return children.stream().map(SchemaStructureViewElement::new).toArray(TreeElement[]::new);
  }

  boolean isAlwaysLeaf() {
    return getChildren().length == 0; // too expensive?
  }

  boolean isAutoExpand() {
    return element instanceof SchemaTypeDef;
  }

  @Override
  public void navigate(boolean requestFocus) {
    element.navigate(requestFocus);
  }

  @Override
  public boolean canNavigate() {
    return element.canNavigate();
  }

  @Override
  public boolean canNavigateToSource() {
    return element.canNavigateToSource();
  }

  private static class StaticItemPresentation implements ItemPresentation {
    @Nullable
    private final String presentableText;
    @Nullable
    private final String locationString;
    @Nullable
    private final Icon icon;

    StaticItemPresentation(@Nullable String presentableText, @Nullable String locationString, @Nullable Icon icon) {
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
