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

import com.intellij.codeInsight.daemon.ProblemHighlightFilter;
import com.intellij.ide.scratch.ScratchFileType;
import com.intellij.psi.PsiFile;
import ws.epigraph.ideaplugin.edl.brains.VirtualFileUtil;
import ws.epigraph.ideaplugin.edl.index.EdlFileIndexUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlProblemHighlightFilter extends ProblemHighlightFilter {
  @Override
  public boolean shouldHighlight(@NotNull PsiFile psiFile) {
    return EdlFileIndexUtil.isEdlSourceFile(psiFile.getProject(), VirtualFileUtil.getOriginalVirtualFile(psiFile)) ||
        (psiFile.getViewProvider().getFileType() == ScratchFileType.INSTANCE);
  }

  // do we need to replicate logic from JavaProblemHighlightFilter::shouldProcessInBatch?
}
