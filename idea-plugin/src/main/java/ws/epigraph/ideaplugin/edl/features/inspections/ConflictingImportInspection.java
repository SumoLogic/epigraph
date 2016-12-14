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
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.containers.MultiMap;
import ws.epigraph.ideaplugin.edl.brains.ImportsManager;
import ws.epigraph.ideaplugin.edl.features.actions.fixes.OptimizeImportsQuickFix;
import ws.epigraph.lang.Qn;
import ws.epigraph.edl.parser.psi.SchemaImportStatement;
import ws.epigraph.edl.parser.psi.SchemaImports;
import ws.epigraph.edl.parser.psi.SchemaVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ConflictingImportInspection extends LocalInspectionTool {
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new SchemaVisitor() {
      @Override
      public void visitImports(@NotNull SchemaImports schemaImports) {
        super.visitImports(schemaImports);

        List<SchemaImportStatement> imports = schemaImports.getImportStatementList();
        MultiMap<Qn, SchemaImportStatement> importsByQn = ImportsManager.getImportsByQn(imports);

        MultiMap<String, SchemaImportStatement> importsByLastSegment = new MultiMap<>(importsByQn.size(), 0.75f);
        for (Map.Entry<Qn, Collection<SchemaImportStatement>> entry : importsByQn.entrySet()) {
          String lastSegment = entry.getKey().last();
          assert lastSegment != null;
          importsByLastSegment.putValue(lastSegment, entry.getValue().iterator().next()); // take only first one so we don't report duplicate imports as conflicts
        }

        for (Map.Entry<String, Collection<SchemaImportStatement>> entry : importsByLastSegment.entrySet()) {
          Collection<SchemaImportStatement> conflictingImports = entry.getValue();
          if (conflictingImports.size() > 1) {
            for (SchemaImportStatement conflictingImport : conflictingImports) {
              String conflictingImportsString = conflictingImports.stream()
                  .filter(i -> i != conflictingImport)
                  .map(i -> "'" + i.getText() + "'")
                  .collect(Collectors.joining(", "));

              holder.registerProblem(conflictingImport,
                  InspectionBundle.message("import.conflicting.problem.descriptor",
                      conflictingImport.getText(),
                      conflictingImportsString),
                  OptimizeImportsQuickFix.INSTANCE
              );
            }
          }
        }
      }
    };
  }
}
