package com.sumologic.epigraph.ideaplugin.schema.presentation;

import com.intellij.ide.FileIconPatcher;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.LayeredIcon;
import com.intellij.util.PlatformIcons;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaFileIndexUtil;
import io.epigraph.schema.parser.SchemaFileType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileIconPatcher implements FileIconPatcher {
  // TODO only icon in the editor tab but not in the project view gets patched
  // https://intellij-support.jetbrains.com/hc/en-us/community/posts/207277349-handling-custom-language-file-outside-of-source-root

  @Override
  public Icon patchIcon(Icon baseIcon, VirtualFile file, @Iconable.IconFlags int flags, @Nullable Project project) {
    if (project == null) {
      return baseIcon;
    }

    Icon icon = replaceIcon(file, flags, project, baseIcon);

    final ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
    if (fileIndex.isInSource(file) && CompilerManager.getInstance(project).isExcludedFromCompilation(file)) {
      return new LayeredIcon(icon, PlatformIcons.EXCLUDED_FROM_COMPILE_ICON);
    }

    return icon;
  }

  private static Icon replaceIcon(VirtualFile file, @Iconable.IconFlags int flags, Project project, Icon baseIcon) {
    FileType fileType = file.getFileType();
    if (fileType == SchemaFileType.INSTANCE && !SchemaFileIndexUtil.isSchemaSourceFile(project, file)) {
      return SchemaPresentationUtil.SCHEMA_OUTSIDE_SOURCE_FILE_ICON;
    }

    return baseIcon;
  }
}
