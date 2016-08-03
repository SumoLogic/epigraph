package com.sumologic.epigraph.ideaplugin.schema.highlighting;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaFileIndexUtil;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaProblemFileHighlightFilter implements Condition<VirtualFile> {
  private final Project project;

  public SchemaProblemFileHighlightFilter(Project project) {
    this.project = project;
  }

  @Override
  public boolean value(VirtualFile file) {
    return SchemaFileIndexUtil.isSchemaSourceFile(project, file)
        && !CompilerManager.getInstance(project).isExcludedFromCompilation(file);
  }
}
