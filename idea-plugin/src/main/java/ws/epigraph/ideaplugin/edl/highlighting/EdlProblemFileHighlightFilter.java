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

package ws.epigraph.ideaplugin.edl.highlighting;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import ws.epigraph.ideaplugin.edl.index.EdlFileIndexUtil;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlProblemFileHighlightFilter implements Condition<VirtualFile> {
  private final Project project;

  public EdlProblemFileHighlightFilter(Project project) {
    this.project = project;
  }

  @Override
  public boolean value(VirtualFile file) {
    return EdlFileIndexUtil.isEdlSourceFile(project, file)
        && !CompilerManager.getInstance(project).isExcludedFromCompilation(file);
  }
}
