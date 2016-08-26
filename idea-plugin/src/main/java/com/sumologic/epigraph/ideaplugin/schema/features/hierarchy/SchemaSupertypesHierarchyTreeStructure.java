package com.sumologic.epigraph.ideaplugin.schema.features.hierarchy;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaSupertypesHierarchyTreeStructure extends HierarchyTreeStructure {
  SchemaSupertypesHierarchyTreeStructure(Project project, EpigraphTypeDef typeDef) {
    super(project, new SchemaHierarchyNodeDescriptor(project, null, typeDef, true));
  }

  @NotNull
  @Override
  protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor descriptor) {
    final PsiElement element = descriptor.getPsiElement();
    if (element instanceof EpigraphTypeDef) {
      EpigraphTypeDef typeDef = (EpigraphTypeDef) element;
//      Collection<SchemaTypeDef> parents = SchemaDirectTypeParentsSearch.search(typeDef).findAll();
      Collection<EpigraphTypeDef> parents = HierarchyCache.getHierarchyCache(myProject).getDirectTypeParents(typeDef);
      if (parents.isEmpty()) return ArrayUtil.EMPTY_OBJECT_ARRAY;

      return parents.stream()
          .map(def -> new SchemaHierarchyNodeDescriptor(myProject, descriptor, def, false))
          .toArray();
    }

    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }
}
