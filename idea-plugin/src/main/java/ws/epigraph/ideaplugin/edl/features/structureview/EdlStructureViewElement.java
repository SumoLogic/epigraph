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

package ws.epigraph.ideaplugin.edl.features.structureview;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import ws.epigraph.edl.parser.psi.*;
import ws.epigraph.ideaplugin.edl.presentation.EdlPresentationUtil;
import ws.epigraph.ideaplugin.edl.presentation.StaticItemPresentation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class EdlStructureViewElement implements StructureViewTreeElement {
  private final NavigatablePsiElement element;

  EdlStructureViewElement(PsiElement element) {
    this.element = (NavigatablePsiElement) element;
  }

  @Override
  public Object getValue() {
    return element;
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    ItemPresentation presentation = EdlPresentationUtil.getPresentation(element, true);
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

    if (element instanceof AnnotationsHolder) {
      children.addAll(((AnnotationsHolder) element).getAnnotationList());
    }

    if (element instanceof EdlFile) {
      EdlDefs defs = ((EdlFile) element).getDefs();
      if (defs != null) {
        children.addAll(defs.getTypeDefWrapperList().stream().map(EdlTypeDefWrapper::getElement).collect(Collectors.toList()));
        children.addAll(defs.getSupplementDefList()); // EdlSupplementDef
      }
    } else if (element instanceof EdlRecordTypeDef) {
      EdlRecordTypeBody recordTypeBody = ((EdlRecordTypeDef) element).getRecordTypeBody();
      if (recordTypeBody != null) {
        children.addAll(recordTypeBody.getAnnotationList()); // EdlCustomParam
        children.addAll(recordTypeBody.getFieldDeclList()); // EdlFieldDecl
      }
    } else if (element instanceof EdlVarTypeDef) {
      EdlVarTypeBody varTypeBody = ((EdlVarTypeDef) element).getVarTypeBody();
      if (varTypeBody != null) {
        children.addAll(varTypeBody.getAnnotationList());
        children.addAll(varTypeBody.getVarTagDeclList()); // EdlVarTagDecl
      }
    } else if (element instanceof EdlEnumTypeDef) {
      EdlEnumTypeBody enumTypeBody = ((EdlEnumTypeDef) element).getEnumTypeBody();
      if (enumTypeBody != null) {
        children.addAll(enumTypeBody.getAnnotationList());
        children.addAll(enumTypeBody.getEnumMemberDeclList()); // EdlEnumMemberDecl
      }
    } else if (element instanceof EdlMapTypeDef) {
      EdlMapTypeBody mapTypeBody = ((EdlMapTypeDef) element).getMapTypeBody();
      if (mapTypeBody != null) children.addAll(mapTypeBody.getAnnotationList());
    } else if (element instanceof EdlListTypeDef) {
      EdlListTypeBody listTypeBody = ((EdlListTypeDef) element).getListTypeBody();
      if (listTypeBody != null) children.addAll(listTypeBody.getAnnotationList());
    } else if (element instanceof EdlPrimitiveTypeDef) {
      EdlPrimitiveTypeBody primitiveTypeBody = ((EdlPrimitiveTypeDef) element).getPrimitiveTypeBody();
      if (primitiveTypeBody != null) children.addAll(primitiveTypeBody.getAnnotationList());
    }

    if (children.isEmpty()) return StructureViewTreeElement.EMPTY_ARRAY;

    return children.stream().map(EdlStructureViewElement::new).toArray(TreeElement[]::new);
  }

  boolean isAlwaysLeaf() {
    return getChildren().length == 0; // too expensive?
  }

  boolean isAutoExpand() {
    return element instanceof EdlFile;
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
