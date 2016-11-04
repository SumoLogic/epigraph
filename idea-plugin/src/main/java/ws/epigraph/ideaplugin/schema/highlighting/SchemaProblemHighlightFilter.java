package ws.epigraph.ideaplugin.schema.highlighting;

import com.intellij.codeInsight.daemon.ProblemHighlightFilter;
import com.intellij.ide.scratch.ScratchFileType;
import com.intellij.psi.PsiFile;
import ws.epigraph.ideaplugin.schema.brains.VirtualFileUtil;
import ws.epigraph.ideaplugin.schema.index.SchemaFileIndexUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class SchemaProblemHighlightFilter extends ProblemHighlightFilter {
  @Override
  public boolean shouldHighlight(@NotNull PsiFile psiFile) {
    return SchemaFileIndexUtil.isSchemaSourceFile(psiFile.getProject(), VirtualFileUtil.getOriginalVirtualFile(psiFile)) ||
        (psiFile.getViewProvider().getFileType() == ScratchFileType.INSTANCE);
  }

  // do we need to replicate logic from JavaProblemHighlightFilter::shouldProcessInBatch?
}
