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

package ws.epigraph.ideaplugin.schema.features.hierarchy;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import ws.epigraph.schema.parser.psi.EdlTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlSupertypesHierarchyTreeStructure extends HierarchyTreeStructure {
  EdlSupertypesHierarchyTreeStructure(Project project, EdlTypeDef typeDef) {
    super(project, new EdlHierarchyNodeDescriptor(project, null, typeDef, true));
  }

  @NotNull
  @Override
  protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor descriptor) {
    final PsiElement element = descriptor.getPsiElement();
    if (element instanceof EdlTypeDef) {
      EdlTypeDef typeDef = (EdlTypeDef) element;
//      Collection<EdlTypeDef> parents = EdlDirectTypeParentsSearch.search(typeDef).findAll();
      Collection<EdlTypeDef> parents = HierarchyCache.getHierarchyCache(myProject).getDirectTypeParents(typeDef);
      if (parents.isEmpty()) return ArrayUtil.EMPTY_OBJECT_ARRAY;

      return parents.stream()
          .map(def -> new EdlHierarchyNodeDescriptor(myProject, descriptor, def, false))
          .toArray();
    }

    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }
}
