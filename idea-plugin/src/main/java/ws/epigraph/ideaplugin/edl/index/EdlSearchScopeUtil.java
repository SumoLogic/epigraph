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

package ws.epigraph.ideaplugin.edl.index;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import ws.epigraph.ideaplugin.edl.brains.VirtualFileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlSearchScopeUtil {
  @NotNull
  public static GlobalSearchScope getSearchScope(@NotNull PsiElement element) {
    Project project = element.getProject();
    // ignore scopes in test mode. Todo: set up tests properly and remove this hack
    if (ApplicationManager.getApplication().isUnitTestMode()) return GlobalSearchScope.projectScope(project);

    PsiFile psiFile = element.getContainingFile();
    VirtualFile virtualFile = VirtualFileUtil.getOriginalVirtualFile(psiFile);

    if (!EdlFileIndexUtil.isEdlSourceFile(project, virtualFile))
      return GlobalSearchScope.fileScope(psiFile);

    Module module = ModuleUtil.findModuleForPsiElement(element);

    if (module == null)
      return GlobalSearchScope.projectScope(project); // or file scope?

    ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();

    boolean inTestSourceContent = fileIndex.isInTestSourceContent(virtualFile);
    return module.getModuleWithDependenciesAndLibrariesScope(inTestSourceContent);
  }

//  @NotNull
//  public static GlobalSearchScope getSearchScope(@NotNull Project project, @NotNull VirtualFile virtualFile) {
//    if (!EdlFileIndexUtil.isEdlSourceFile(project, virtualFile))
//      return GlobalSearchScope.fileScope(project, virtualFile);
//
//    Module module = ModuleUtil.findModuleForFile(virtualFile, project);
//
//    if (module == null)
//      return GlobalSearchScope.projectScope(project); // or file scope?
//
//    ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
//
//    boolean inTestSourceContent = fileIndex.isInTestSourceContent(virtualFile);
//    return module.getModuleWithDependenciesAndLibrariesScope(inTestSourceContent);
//  }

  public static boolean isInScope(@NotNull GlobalSearchScope scope, @Nullable PsiElement element) {
    if (element == null) return false;
    PsiFile psiFile = element.getContainingFile();
    if (psiFile == null) return false;
    VirtualFile virtualFile = psiFile.getVirtualFile();
    return virtualFile != null && scope.contains(virtualFile);
  }
}
