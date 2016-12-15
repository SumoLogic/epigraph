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

package ws.epigraph.ideaplugin.edl.features.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import ws.epigraph.ideaplugin.edl.brains.ImportsManager;
import ws.epigraph.ideaplugin.edl.features.actions.fixes.OptimizeImportsQuickFix;
import ws.epigraph.edl.parser.psi.EdlFile;
import ws.epigraph.edl.parser.psi.EdlImportStatement;
import ws.epigraph.edl.parser.psi.EdlImports;
import ws.epigraph.edl.parser.psi.EdlVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UnusedImportInspection extends LocalInspectionTool {
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new EdlVisitor() {
      @Override
      public void visitImports(@NotNull EdlImports edlTypeImports) {
        super.visitImports(edlTypeImports);

        Set<EdlImportStatement> unusedImports = ImportsManager.findUnusedImports((EdlFile) edlTypeImports.getContainingFile());
        for (EdlImportStatement unusedImport : unusedImports) {
          holder.registerProblem(unusedImport,
              InspectionBundle.message("import.unused.problem.descriptor"),
              ProblemHighlightType.LIKE_UNUSED_SYMBOL,
              OptimizeImportsQuickFix.INSTANCE);
        }
      }
    };
  }

}
