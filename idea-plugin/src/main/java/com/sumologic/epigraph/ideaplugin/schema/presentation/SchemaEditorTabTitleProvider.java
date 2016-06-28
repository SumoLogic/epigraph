package com.sumologic.epigraph.ideaplugin.schema.presentation;

import com.intellij.openapi.fileEditor.impl.EditorTabTitleProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.sumologic.epigraph.schema.parser.Common;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaEditorTabTitleProvider implements EditorTabTitleProvider {
  @Nullable
  @Override
  public String getEditorTabTitle(Project project, VirtualFile file) {
//    if (file.getName().endsWith("." + Common.FILE_EXTENSION)) {
//      return file.getNameWithoutExtension();
//    }
    return file.getPresentableName();

//    return null;
  }
}
