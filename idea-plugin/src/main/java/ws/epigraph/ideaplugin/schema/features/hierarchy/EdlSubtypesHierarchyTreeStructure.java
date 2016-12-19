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
import ws.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import ws.epigraph.schema.parser.psi.EdlTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class EdlSubtypesHierarchyTreeStructure extends HierarchyTreeStructure {
  EdlSubtypesHierarchyTreeStructure(Project project, EdlTypeDef typeDef) {
    super(project, new EdlHierarchyNodeDescriptor(project, null, typeDef, true));
  }

  @NotNull
  @Override
  protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor descriptor) {
    final EdlTypeDef typeDef = ((EdlHierarchyNodeDescriptor) descriptor).getTypeDef();

//    final List<EdlTypeDef> inheritors = new ArrayList<>(EdlDirectTypeInheritorsSearch.search(typeDef).findAll());
    final List<EdlTypeDef> inheritors = HierarchyCache.getHierarchyCache(myProject).getDirectTypeInheritors(typeDef);

    return inheritors.stream()
        .map(def -> new EdlHierarchyNodeDescriptor(myProject, descriptor, def, false))
        .toArray();
  }
}
