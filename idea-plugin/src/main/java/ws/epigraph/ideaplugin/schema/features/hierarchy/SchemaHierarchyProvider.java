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

import com.intellij.ide.hierarchy.HierarchyBrowser;
import com.intellij.ide.hierarchy.HierarchyProvider;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import ws.epigraph.schema.parser.psi.SchemaSupplementDef;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaHierarchyProvider implements HierarchyProvider {
  @Nullable
  @Override
  public PsiElement getTarget(@NotNull DataContext dataContext) {
    final Project project = CommonDataKeys.PROJECT.getData(dataContext);
    if (project == null) return null;

    final PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);

    SchemaTypeDef typeDef = PsiTreeUtil.getParentOfType(element, SchemaTypeDef.class, false);
    if (typeDef != null) return typeDef;


    SchemaSupplementDef supplementDef = PsiTreeUtil.getParentOfType(element, SchemaSupplementDef.class, false);
    if (supplementDef != null) return supplementDef;

    return null;
  }

  @NotNull
  @Override
  public HierarchyBrowser createHierarchyBrowser(PsiElement target) {
    return new SchemaHierarchyBrowser(target.getProject(), target);
  }

  @Override
  public void browserActivated(@NotNull HierarchyBrowser hierarchyBrowser) {
    ((SchemaHierarchyBrowser) hierarchyBrowser).changeView(SchemaHierarchyBrowser.SUBTYPES_HIERARCHY_TYPE);
  }
}
