package com.sumologic.epigraph.ideaplugin.schema.features.structureview;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import io.epigraph.lang.parser.psi.*;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import com.sumologic.epigraph.ideaplugin.schema.presentation.StaticItemPresentation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class SchemaStructureViewElement implements StructureViewTreeElement {
  private final NavigatablePsiElement element;

  SchemaStructureViewElement(PsiElement element) {
    this.element = (NavigatablePsiElement) element;
  }

  @Override
  public Object getValue() {
    return element;
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    ItemPresentation presentation = SchemaPresentationUtil.getPresentation(element, true);
    // remove location
    return new StaticItemPresentation(
        presentation.getPresentableText(),
        null,
        presentation.getIcon(false)
    );
  }

  @NotNull
  @Override
  public TreeElement[] getChildren() {
    final Collection<PsiElement> children = new ArrayList<>();

    if (element instanceof CustomParamsHolder) {
      children.addAll(((CustomParamsHolder) element).getCustomParamList());
    }

    if (element instanceof SchemaFile) {
      EpigraphDefs defs = ((SchemaFile) element).getDefs();
      if (defs != null) {
        children.addAll(defs.getTypeDefWrapperList().stream().map(EpigraphTypeDefWrapper::getElement).collect(Collectors.toList()));
        children.addAll(defs.getSupplementDefList()); // SchemaSupplementDef
      }
    } else if (element instanceof EpigraphRecordTypeDef) {
      EpigraphRecordTypeBody recordTypeBody = ((EpigraphRecordTypeDef) element).getRecordTypeBody();
      if (recordTypeBody != null) {
        children.addAll(recordTypeBody.getCustomParamList()); // SchemaCustomParam
        children.addAll(recordTypeBody.getFieldDeclList()); // SchemaFieldDecl
      }
    } else if (element instanceof EpigraphVarTypeDef) {
      EpigraphVarTypeBody varTypeBody = ((EpigraphVarTypeDef) element).getVarTypeBody();
      if (varTypeBody != null) {
        children.addAll(varTypeBody.getCustomParamList());
        children.addAll(varTypeBody.getVarTagDeclList()); // SchemaVarTagDecl
      }
    } else if (element instanceof EpigraphEnumTypeDef) {
      EpigraphEnumTypeBody enumTypeBody = ((EpigraphEnumTypeDef) element).getEnumTypeBody();
      if (enumTypeBody != null) {
        children.addAll(enumTypeBody.getCustomParamList());
        children.addAll(enumTypeBody.getEnumMemberDeclList()); // SchemaEnumMemberDecl
      }
    } else if (element instanceof EpigraphMapTypeDef) {
      EpigraphMapTypeBody mapTypeBody = ((EpigraphMapTypeDef) element).getMapTypeBody();
      if (mapTypeBody != null) children.addAll(mapTypeBody.getCustomParamList());
    } else if (element instanceof EpigraphListTypeDef) {
      EpigraphListTypeBody listTypeBody = ((EpigraphListTypeDef) element).getListTypeBody();
      if (listTypeBody != null) children.addAll(listTypeBody.getCustomParamList());
    } else if (element instanceof EpigraphPrimitiveTypeDef) {
      EpigraphPrimitiveTypeBody primitiveTypeBody = ((EpigraphPrimitiveTypeDef) element).getPrimitiveTypeBody();
      if (primitiveTypeBody != null) children.addAll(primitiveTypeBody.getCustomParamList());
    }

    if (children.isEmpty()) return StructureViewTreeElement.EMPTY_ARRAY;

    return children.stream().map(SchemaStructureViewElement::new).toArray(TreeElement[]::new);
  }

  boolean isAlwaysLeaf() {
    return getChildren().length == 0; // too expensive?
  }

  boolean isAutoExpand() {
    return element instanceof SchemaFile;
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


}
