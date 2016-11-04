package ws.epigraph.ideaplugin.schema.diagram;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.intellij.diagram.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
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
public class SchemaDiagramDataModel extends DiagramDataModel<PsiNamedElement> implements ModificationTracker {
  private final SchemaFile file;

  private final Collection<PsiNamedElement> extraElements = new HashSet<>();
  private final Collection<PsiNamedElement> removedElements = new HashSet<>();

  private final BiMap<PsiNamedElement, DiagramNode<PsiNamedElement>> nodes = HashBiMap.create();
  private final Collection<DiagramEdge<PsiNamedElement>> edges = new HashSet<>();

  SchemaDiagramDataModel(Project project, SchemaFile file, SchemaDiagramProvider provider) {
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

    SchemaDefs defs = file.getDefs();
    if (defs != null) {
      allElements.addAll(defs.getTypeDefWrapperList()
                             .stream()
                             .map(SchemaTypeDefWrapper::getElement)
                             .collect(Collectors.toList()));
    }

    for (PsiNamedElement element : allElements) {
      if (element instanceof SchemaTypeDef && !removedElements.contains(element)) {
        SchemaTypeDef typeDef = (SchemaTypeDef) element;

        DiagramNode<PsiNamedElement> node = getOrAddNode(typeDef, allElements);

        addParents(typeDef, node, allElements);
        addMeta(typeDef, node, allElements);
        if (isShowDependencies())
          addMembers(typeDef, node, allElements);
      }
    }
  }

  private void addMembers(SchemaTypeDef typeDef,
                          DiagramNode<PsiNamedElement> node,
                          Collection<PsiNamedElement> allElements) {
    if (typeDef instanceof SchemaVarTypeDef) {
      SchemaVarTypeDef varTypeDef = (SchemaVarTypeDef) typeDef;
      SchemaVarTypeBody body = varTypeDef.getVarTypeBody();
      if (body != null) {
        for (SchemaVarTagDecl tagDecl : body.getVarTagDeclList()) {
          SchemaTypeRef typeRef = tagDecl.getTypeRef();
          addMember(node, typeRef, allElements, tagDecl.getQid().getCanonicalName(), null, null);
        }
      }

    }

    if (typeDef instanceof SchemaRecordTypeDef) {
      SchemaRecordTypeDef recordTypeDef = (SchemaRecordTypeDef) typeDef;
      SchemaRecordTypeBody body = recordTypeDef.getRecordTypeBody();
      if (body != null) {
        for (SchemaFieldDecl fieldDecl : body.getFieldDeclList()) {
          SchemaValueTypeRef valueTypeRef = fieldDecl.getValueTypeRef();
          addMember(node, valueTypeRef, allElements, fieldDecl.getQid().getCanonicalName(), null, null);
        }
      }
    }
  }

  private void addMember(final DiagramNode<PsiNamedElement> node,
                         SchemaValueTypeRef valueTypeRef,
                         Collection<PsiNamedElement> allElements,
                         String label,
                         String fromLabel,
                         String toLabel) {
    if (valueTypeRef != null) {
      SchemaTypeRef typeRef = valueTypeRef.getTypeRef();
      addMember(node, typeRef, allElements, label, fromLabel, toLabel);
    }
  }

  private void addMember(final DiagramNode<PsiNamedElement> node,
                         SchemaTypeRef typeRef,
                         Collection<PsiNamedElement> allElements,
                         final String label,
                         final String fromLabel,
                         final String toLabel) {
    if (typeRef instanceof SchemaQnTypeRef) {
      SchemaTypeDef targetTypeDef = ((SchemaQnTypeRef) typeRef).resolve();
      if (targetTypeDef != null) {
        DiagramNode<PsiNamedElement> targetNode = getOrAddNode(targetTypeDef, allElements);
        if (targetNode != null) {
          edges.add(
              new DiagramEdgeBase<PsiNamedElement>(
                  targetNode, node, SchemaDiagramRelationshipManager.member(
                  label,
                  fromLabel,
                  toLabel
              )
              ) {}
          );
        }
      }
    } else if (typeRef instanceof SchemaAnonList) {
      addMember(node,
                ((SchemaAnonList) typeRef).getValueTypeRef(),
                allElements,
                label,
                null,
                SchemaDiagramRelationshipManager.ZERO_OR_MORE_CARDINALITY
      );
    } else if (typeRef instanceof SchemaAnonMap) {
      addMember(node,
                ((SchemaAnonMap) typeRef).getTypeRef(),
                allElements,
                label,
                null,
                SchemaDiagramRelationshipManager.ZERO_OR_MORE_CARDINALITY
      );
      addMember(node,
                ((SchemaAnonMap) typeRef).getValueTypeRef(),
                allElements,
                label,
                null,
                SchemaDiagramRelationshipManager.ZERO_OR_MORE_CARDINALITY
      );
    }
  }

  private void addParents(SchemaTypeDef typeDef,
                          final DiagramNode<PsiNamedElement> node,
                          Collection<PsiNamedElement> allElements) {
    List<SchemaTypeDef> parents = typeDef.extendsParents();
    for (SchemaTypeDef parent : parents) {
      DiagramNode<PsiNamedElement> node2 = getOrAddNode(parent, allElements);

      if (node2 != null) {
        edges.add(
            new DiagramEdgeBase<PsiNamedElement>(
                node, node2, SchemaDiagramRelationshipManager.EXTENDS
            ) {}
        );
      }
    }
  }

  @Nullable
  private DiagramNode<PsiNamedElement> getOrAddNode(SchemaTypeDef typeDef, Collection<PsiNamedElement> allElements) {
    DiagramNode<PsiNamedElement> node = nodes.get(typeDef);
    if (node == null && allElements.contains(typeDef))
      node = addNode(typeDef);
    return node;
  }

  private void addMeta(SchemaTypeDef typeDef,
                       final DiagramNode<PsiNamedElement> node,
                       Collection<PsiNamedElement> allElements) {
    SchemaMetaDecl metaDecl = typeDef.getMetaDecl();
    if (metaDecl != null) {
      SchemaQnTypeRef typeRef = metaDecl.getQnTypeRef();
      if (typeRef != null) {
        SchemaTypeDef metaType = typeRef.resolve();
        if (metaType != null) {
          DiagramNode<PsiNamedElement> node2 = getOrAddNode(metaType, allElements);

          if (node2 != null) {
            edges.add(
                new DiagramEdgeBase<PsiNamedElement>(
                    node, node2, SchemaDiagramRelationshipManager.META
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
        return SchemaPresentationUtil.getName(element, true);
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
