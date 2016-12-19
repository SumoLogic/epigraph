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
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class SchemaSubtypesHierarchyTreeStructure extends HierarchyTreeStructure {
  SchemaSubtypesHierarchyTreeStructure(Project project, SchemaTypeDef typeDef) {
    super(project, new SchemaHierarchyNodeDescriptor(project, null, typeDef, true));
  }

  @NotNull
  @Override
  protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor descriptor) {
    final SchemaTypeDef typeDef = ((SchemaHierarchyNodeDescriptor) descriptor).getTypeDef();

//    final List<SchemaTypeDef> inheritors = new ArrayList<>(SchemaDirectTypeInheritorsSearch.search(typeDef).findAll());
    final List<SchemaTypeDef> inheritors = HierarchyCache.getHierarchyCache(myProject).getDirectTypeInheritors(typeDef);

    return inheritors.stream()
        .map(def -> new SchemaHierarchyNodeDescriptor(myProject, descriptor, def, false))
        .toArray();
  }
}
