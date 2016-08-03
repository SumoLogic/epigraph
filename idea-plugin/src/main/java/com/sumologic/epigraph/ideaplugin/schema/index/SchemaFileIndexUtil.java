package com.sumologic.epigraph.ideaplugin.schema.index;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileIndexUtil {
  public static boolean isSchemaSourceFile(@NotNull Project project, @Nullable VirtualFile file) {
    if (file == null) return false;
    FileTypeManager fileTypeManager = FileTypeManager.getInstance();
    if (file.isDirectory() || fileTypeManager.isFileIgnored(file)) {
      return false;
    }
    ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
    return fileIndex.isInSource(file) || fileIndex.isInTestSourceContent(file) || fileIndex.isInLibrarySource(file);
  }
}
