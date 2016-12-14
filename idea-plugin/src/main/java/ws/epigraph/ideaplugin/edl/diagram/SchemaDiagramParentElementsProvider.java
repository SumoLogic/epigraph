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

package ws.epigraph.ideaplugin.edl.diagram;

import com.intellij.diagram.extras.providers.SupersProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.ideaplugin.edl.SchemaBundle;
import ws.epigraph.ideaplugin.edl.brains.hierarchy.HierarchyCache;
import ws.epigraph.edl.parser.psi.SchemaTypeDef;

import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramParentElementsProvider extends SupersProvider<PsiNamedElement> {
  @Override
  public boolean isEnabledOn(PsiNamedElement element) {
    return element instanceof SchemaTypeDef;
  }

  @Override
  public PsiNamedElement[] getElements(PsiNamedElement psiNamedElement, Project project) {
    SchemaTypeDef typeDef = (SchemaTypeDef) psiNamedElement;
    List<SchemaTypeDef> parents = HierarchyCache.getHierarchyCache(project).getDirectTypeParents(typeDef);
    return parents.toArray(new PsiNamedElement[parents.size()]);
  }

  @Override
  public String getHeaderName(PsiNamedElement psiNamedElement, Project project) {
    return SchemaBundle.message("diagram.show.supertypes", psiNamedElement.getName());
  }

  @Override
  public Comparator<? super PsiNamedElement> getComparator() {
    return PSI_COMPARATOR;
  }
}
