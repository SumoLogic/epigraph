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

import com.intellij.diagram.DiagramVfsResolver;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import ws.epigraph.ideaplugin.edl.index.SchemaIndexUtil;
import ws.epigraph.ideaplugin.edl.presentation.SchemaPresentationUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.edl.parser.psi.SchemaFile;
import ws.epigraph.edl.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaDiagramVfsResolver implements DiagramVfsResolver<PsiNamedElement> {
  private static final String FILE_PREFIX = "file:";
  private static final String TYPE_PREFIX = "type:";

  @Override
  public String getQualifiedName(PsiNamedElement element) {
    if (element instanceof PsiFile) {
      PsiFile psiFile = (PsiFile) element;
      return FILE_PREFIX + psiFile.getVirtualFile().getUrl();
    }

    if (element instanceof SchemaTypeDef) {
      SchemaTypeDef typeDef = (SchemaTypeDef) element;
      return TYPE_PREFIX + SchemaPresentationUtil.getName(typeDef, true);
    }

    return null;
  }

  @Nullable
  @Override
  public PsiNamedElement resolveElementByFQN(String s, Project project) {
    if (s.startsWith(FILE_PREFIX)) {
      String name = s.substring(FILE_PREFIX.length());

      VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(name);
      PsiFile psiFile = virtualFile == null ? null : PsiManager.getInstance(project).findFile(virtualFile);
      return psiFile instanceof SchemaFile ? psiFile : null;
    }

    if (s.startsWith(TYPE_PREFIX)) {
      String name = s.substring(TYPE_PREFIX.length());

      SchemaIndexUtil.findTypeDef(project, Qn.fromDotSeparated(name), GlobalSearchScope.allScope(project));
    }

    return null;
  }
}
