package com.sumologic.epigraph.ideaplugin.schema.highlighting;

import com.intellij.codeInsight.daemon.ProblemHighlightFilter;
import com.intellij.ide.scratch.ScratchFileType;
import com.intellij.psi.PsiFile;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaFileIndexUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaProblemHighlightFilter extends ProblemHighlightFilter {
  @Override
  public boolean shouldHighlight(@NotNull PsiFile psiFile) {
    return SchemaFileIndexUtil.isSchemaSourceFile(psiFile.getProject(), psiFile.getVirtualFile()) ||
        (psiFile.getViewProvider().getFileType() == ScratchFileType.INSTANCE);
  }

  // do we need to replicate logic from JavaProblemHighlightFilter::shouldProcessInBatch?
}
