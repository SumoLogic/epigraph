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

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class DuplicateImportInspection extends LocalInspectionTool {
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new EpigraphVisitor() {
      @Override
      public void visitImports(@NotNull EpigraphImports epigraphImports) {
        super.visitImports(epigraphImports);

        List<EpigraphImportStatement> imports = epigraphImports.getImportStatementList();

        MultiMap<Fqn, EpigraphImportStatement> importsByFqn = ImportsManager.getImportsByFqn(imports);

        for (Map.Entry<Fqn, Collection<EpigraphImportStatement>> entry : importsByFqn.entrySet()) {
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
