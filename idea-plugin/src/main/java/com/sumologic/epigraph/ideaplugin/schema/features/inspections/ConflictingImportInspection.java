package com.sumologic.epigraph.ideaplugin.schema.features.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.containers.MultiMap;
import com.sumologic.epigraph.ideaplugin.schema.brains.ImportsManager;
import com.sumologic.epigraph.ideaplugin.schema.features.actions.fixes.OptimizeImportsQuickFix;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.parser.psi.EpigraphImportStatement;
import io.epigraph.lang.parser.psi.EpigraphImports;
import io.epigraph.lang.parser.psi.EpigraphVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ConflictingImportInspection extends LocalInspectionTool {
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new EpigraphVisitor() {
      @Override
      public void visitImports(@NotNull EpigraphImports epigraphImports) {
        super.visitImports(epigraphImports);

        List<EpigraphImportStatement> imports = epigraphImports.getImportStatementList();
        MultiMap<Fqn, EpigraphImportStatement> importsByFqn = ImportsManager.getImportsByFqn(imports);

        MultiMap<String, EpigraphImportStatement> importsByLastSegment = new MultiMap<>(importsByFqn.size(), 0.75f);
        for (Map.Entry<Fqn, Collection<EpigraphImportStatement>> entry : importsByFqn.entrySet()) {
          String lastSegment = entry.getKey().last();
          assert lastSegment != null;
          importsByLastSegment.putValue(lastSegment, entry.getValue().iterator().next()); // take only first one so we don't report duplicate imports as conflicts
        }

        for (Map.Entry<String, Collection<EpigraphImportStatement>> entry : importsByLastSegment.entrySet()) {
          Collection<EpigraphImportStatement> conflictingImports = entry.getValue();
          if (conflictingImports.size() > 1) {
            for (EpigraphImportStatement conflictingImport : conflictingImports) {
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
