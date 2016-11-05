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

package ws.epigraph.ideaplugin.schema.presentation;

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
import ws.epigraph.ideaplugin.schema.index.SchemaFileIndexUtil;
import ws.epigraph.schema.parser.SchemaFileType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaFileIconPatcher implements FileIconPatcher {
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
      return SchemaPresentationUtil.schemaOutsideSourceFileIcon();
    }

    return baseIcon;
  }
}
