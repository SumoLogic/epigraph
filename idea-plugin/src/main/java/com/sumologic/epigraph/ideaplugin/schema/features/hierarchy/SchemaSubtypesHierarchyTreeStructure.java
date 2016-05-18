package com.sumologic.epigraph.ideaplugin.schema.features.hierarchy;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.openapi.project.Project;
import com.sumologic.epigraph.ideaplugin.schema.brains.search.SchemaTypeInheritorsSearch;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class SchemaSubtypesHierarchyTreeStructure extends HierarchyTreeStructure {
  SchemaSubtypesHierarchyTreeStructure(Project project, SchemaTypeDef typeDef) {
    super(project, new SchemaHierarchyNodeDescriptor(project, null, typeDef, true));
  }

  @NotNull
  @Override
  protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor descriptor) {
    final SchemaTypeDef typeDef = ((SchemaHierarchyNodeDescriptor) descriptor).getTypeDef();

    final List<SchemaTypeDef> inheritors = new ArrayList<>(SchemaTypeInheritorsSearch.search(typeDef).findAll());

    return inheritors.stream()
        .map(def -> new SchemaHierarchyNodeDescriptor(myProject, descriptor, def, false))
        .toArray();
  }
}
