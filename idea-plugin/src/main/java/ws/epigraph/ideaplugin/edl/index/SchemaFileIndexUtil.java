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

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaFileIndexUtil {
  @Contract("_, null -> false")
  public static boolean isSchemaSourceFile(@NotNull Project project, @Nullable VirtualFile file) {
    if (file == null) return false;
    FileTypeManager fileTypeManager = FileTypeManager.getInstance();
    if (file.isDirectory() || fileTypeManager.isFileIgnored(file)) {
      return false;
    }

    return fileUnderSources(project, file);
  }

  public static boolean fileUnderSources(@NotNull Project project, @NotNull VirtualFile file) {
    ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
//    return fileIndex.isInSource(file) || fileIndex.isInTestSourceContent(file) || fileIndex.isInLibrarySource(file);
    return fileIndex.isUnderSourceRootOfType(file, JavaModuleSourceRootTypes.SOURCES) || fileIndex.isInLibrarySource(file);
  }
}
