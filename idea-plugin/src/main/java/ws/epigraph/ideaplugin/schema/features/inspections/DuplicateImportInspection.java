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

package ws.epigraph.ideaplugin.schema.features.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.containers.MultiMap;
import ws.epigraph.ideaplugin.schema.brains.ImportsManager;
import ws.epigraph.ideaplugin.schema.features.actions.fixes.OptimizeImportsQuickFix;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.SchemaImportStatement;
import ws.epigraph.schema.parser.psi.SchemaImports;
import ws.epigraph.schema.parser.psi.SchemaVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DuplicateImportInspection extends LocalInspectionTool {
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new SchemaVisitor() {
      @Override
      public void visitImports(@NotNull SchemaImports schemaTypeImports) {
        super.visitImports(schemaTypeImports);

        List<SchemaImportStatement> imports = schemaTypeImports.getImportStatementList();

        MultiMap<Qn, SchemaImportStatement> importsByQn = ImportsManager.getImportsByQn(imports);

        for (Map.Entry<Qn, Collection<SchemaImportStatement>> entry : importsByQn.entrySet()) {
          entry.getValue().stream()
              .filter(is -> entry.getValue().size() > 1)
              .forEach(is -> holder.registerProblem(is,
                  InspectionBundle.message("import.duplicate.problem.descriptor"),
                  OptimizeImportsQuickFix.INSTANCE));
        }
      }
    };
  }
}
