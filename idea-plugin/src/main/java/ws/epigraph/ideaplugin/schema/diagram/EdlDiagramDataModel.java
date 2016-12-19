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

package ws.epigraph.ideaplugin.schema.diagram;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.intellij.diagram.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.ideaplugin.schema.presentation.EdlPresentationUtil;
import ws.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlDiagramDataModel extends DiagramDataModel<PsiNamedElement> implements ModificationTracker {
  private final EdlFile file;

  private final Collection<PsiNamedElement> extraElements = new HashSet<>();
  private final Collection<PsiNamedElement> removedElements = new HashSet<>();

  private final BiMap<PsiNamedElement, DiagramNode<PsiNamedElement>> nodes = HashBiMap.create();
  private final Collection<DiagramEdge<PsiNamedElement>> edges = new HashSet<>();

  EdlDiagramDataModel(Project project, EdlFile file, EdlDiagramProvider provider) {
    super(project, provider);
    this.file = file;
  }

  @NotNull
  @Override
  public Collection<? extends DiagramNode<PsiNamedElement>> getNodes() { return nodes.values(); }

  @NotNull
  @Override
  public Collection<? extends DiagramEdge<PsiNamedElement>> getEdges() { return edges; }

  @NotNull
  @Override
  public String getNodeName(DiagramNode<PsiNamedElement> diagramNode) {
    return StringUtil.notNullize(diagramNode.getTooltip());
  }

  @Nullable
  @Override
  public DiagramNode<PsiNamedElement> addElement(PsiNamedElement psiNamedElement) {
    removedElements.remove(psiNamedElement);
    extraElements.add(psiNamedElement);
    refreshDataModel();
    return nodes.get(psiNamedElement);
  }

  @Override
  public void removeNode(DiagramNode<PsiNamedElement> node) {
    PsiNamedElement namedElement = nodes.inverse().get(node);
    if (namedElement != null) {
      removedElements.add(namedElement);
      extraElements.remove(namedElement);
      refreshDataModel(); // needed?
    }

    super.removeNode(node);
  }

  @Override
  public boolean isDependencyDiagramSupported() {
    return true;
  }

  @Override
  public void refreshDataModel() {
    nodes.clear();
    edges.clear();

    Collection<PsiNamedElement> allElements = new HashSet<>();
    allElements.addAll(extraElements);

    EdlDefs defs = file.getDefs();
    if (defs != null) {
      allElements.addAll(defs.getTypeDefWrapperList()
                             .stream()
                             .map(EdlTypeDefWrapper::getElement)
                             .collect(Collectors.toList()));
    }

    for (PsiNamedElement element : allElements) {
      if (element instanceof EdlTypeDef && !removedElements.contains(element)) {
        EdlTypeDef typeDef = (EdlTypeDef) element;

        DiagramNode<PsiNamedElement> node = getOrAddNode(typeDef, allElements);

        addParents(typeDef, node, allElements);
        addMeta(typeDef, node, allElements);
        if (isShowDependencies())
          addMembers(typeDef, node, allElements);
      }
    }
  }

  private void addMembers(EdlTypeDef typeDef,
                          DiagramNode<PsiNamedElement> node,
                          Collection<PsiNamedElement> allElements) {
    if (typeDef instanceof EdlVarTypeDef) {
      EdlVarTypeDef varTypeDef = (EdlVarTypeDef) typeDef;
      EdlVarTypeBody body = varTypeDef.getVarTypeBody();
      if (body != null) {
        for (EdlVarTagDecl tagDecl : body.getVarTagDeclList()) {
          EdlTypeRef typeRef = tagDecl.getTypeRef();
          addMember(node, typeRef, allElements, tagDecl.getQid().getCanonicalName(), null, null);
        }
      }

    }

    if (typeDef instanceof EdlRecordTypeDef) {
      EdlRecordTypeDef recordTypeDef = (EdlRecordTypeDef) typeDef;
      EdlRecordTypeBody body = recordTypeDef.getRecordTypeBody();
      if (body != null) {
        for (EdlFieldDecl fieldDecl : body.getFieldDeclList()) {
          EdlValueTypeRef valueTypeRef = fieldDecl.getValueTypeRef();
          addMember(node, valueTypeRef, allElements, fieldDecl.getQid().getCanonicalName(), null, null);
        }
      }
    }
  }

  private void addMember(final DiagramNode<PsiNamedElement> node,
                         EdlValueTypeRef valueTypeRef,
                         Collection<PsiNamedElement> allElements,
                         String label,
                         String fromLabel,
                         String toLabel) {
    if (valueTypeRef != null) {
      EdlTypeRef typeRef = valueTypeRef.getTypeRef();
      addMember(node, typeRef, allElements, label, fromLabel, toLabel);
    }
  }

  private void addMember(final DiagramNode<PsiNamedElement> node,
                         EdlTypeRef typeRef,
                         Collection<PsiNamedElement> allElements,
                         final String label,
                         final String fromLabel,
                         final String toLabel) {
    if (typeRef instanceof EdlQnTypeRef) {
      EdlTypeDef targetTypeDef = ((EdlQnTypeRef) typeRef).resolve();
      if (targetTypeDef != null) {
        DiagramNode<PsiNamedElement> targetNode = getOrAddNode(targetTypeDef, allElements);
        if (targetNode != null) {
          edges.add(
              new DiagramEdgeBase<PsiNamedElement>(
                  targetNode, node, EdlDiagramRelationshipManager.member(
                  label,
                  fromLabel,
                  toLabel
              )
              ) {}
          );
        }
      }
    } else if (typeRef instanceof EdlAnonList) {
      addMember(node,
                ((EdlAnonList) typeRef).getValueTypeRef(),
                allElements,
                label,
                null,
                EdlDiagramRelationshipManager.ZERO_OR_MORE_CARDINALITY
      );
    } else if (typeRef instanceof EdlAnonMap) {
      addMember(node,
                ((EdlAnonMap) typeRef).getTypeRef(),
                allElements,
                label,
                null,
                EdlDiagramRelationshipManager.ZERO_OR_MORE_CARDINALITY
      );
      addMember(node,
                ((EdlAnonMap) typeRef).getValueTypeRef(),
                allElements,
                label,
                null,
                EdlDiagramRelationshipManager.ZERO_OR_MORE_CARDINALITY
      );
    }
  }

  private void addParents(EdlTypeDef typeDef,
                          final DiagramNode<PsiNamedElement> node,
                          Collection<PsiNamedElement> allElements) {
    List<EdlTypeDef> parents = typeDef.extendsParents();
    for (EdlTypeDef parent : parents) {
      DiagramNode<PsiNamedElement> node2 = getOrAddNode(parent, allElements);

      if (node2 != null) {
        edges.add(
            new DiagramEdgeBase<PsiNamedElement>(
                node, node2, EdlDiagramRelationshipManager.EXTENDS
            ) {}
        );
      }
    }
  }

  @Nullable
  private DiagramNode<PsiNamedElement> getOrAddNode(EdlTypeDef typeDef, Collection<PsiNamedElement> allElements) {
    DiagramNode<PsiNamedElement> node = nodes.get(typeDef);
    if (node == null && allElements.contains(typeDef))
      node = addNode(typeDef);
    return node;
  }

  private void addMeta(EdlTypeDef typeDef,
                       final DiagramNode<PsiNamedElement> node,
                       Collection<PsiNamedElement> allElements) {
    EdlMetaDecl metaDecl = typeDef.getMetaDecl();
    if (metaDecl != null) {
      EdlQnTypeRef typeRef = metaDecl.getQnTypeRef();
      if (typeRef != null) {
        EdlTypeDef metaType = typeRef.resolve();
        if (metaType != null) {
          DiagramNode<PsiNamedElement> node2 = getOrAddNode(metaType, allElements);

          if (node2 != null) {
            edges.add(
                new DiagramEdgeBase<PsiNamedElement>(
                    node, node2, EdlDiagramRelationshipManager.META
                ) {}
            );
          }
        }
      }
    }
  }

  @NotNull
  private DiagramNode<PsiNamedElement> addNode(final PsiNamedElement element) {
    DiagramNode<PsiNamedElement> node = new PsiDiagramNode<PsiNamedElement>(element, getProvider()) {
      @Nullable
      @Override
      public String getTooltip() {
        return EdlPresentationUtil.getName(element, true);
      }
    };
    nodes.put(element, node);
    return node;
  }

  @NotNull
  @Override
  public ModificationTracker getModificationTracker() {
    return this;
  }

  @Override
  public long getModificationCount() {
    return file.getModificationStamp();
  }

  @Override
  public void dispose() {

  }
}
