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

package ws.epigraph.ideaplugin.schema.features.navigation;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import ws.epigraph.ideaplugin.schema.index.SchemaFileIndexUtil;
import ws.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GotoTypeContributor implements ChooseByNameContributor {
  @NotNull
  @Override
  public String[] getNames(Project project, boolean includeNonProjectItems) {
    GlobalSearchScope scope = includeNonProjectItems
                              ? GlobalSearchScope.allScope(project)
                              : GlobalSearchScope.projectScope(project);

    return SchemaIndexUtil.findTypeDefs(project, null, null, scope)
                          .stream()
                          .filter(def -> shouldInclude(project, def))
                          .map(SchemaTypeDef::getName)
                          .toArray(String[]::new);
  }

  @NotNull
  @Override
  public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
    GlobalSearchScope scope = includeNonProjectItems
                              ? GlobalSearchScope.allScope(project)
                              : GlobalSearchScope.projectScope(project);

    return SchemaIndexUtil.findTypeDefs(project, null, Qn.fromDotSeparated(name), scope)
                          .stream()
                          .filter(def -> shouldInclude(project, def))
                          .toArray(NavigationItem[]::new);
  }

  private static boolean shouldInclude(@NotNull Project project, @NotNull SchemaTypeDef def) {
    final PsiFile file = def.getContainingFile();
    if (file == null) return false;
    final VirtualFile virtualFile = file.getVirtualFile();
    if (virtualFile == null) return false;
    return SchemaFileIndexUtil.isSchemaSourceFile(project, virtualFile);
  }
}
