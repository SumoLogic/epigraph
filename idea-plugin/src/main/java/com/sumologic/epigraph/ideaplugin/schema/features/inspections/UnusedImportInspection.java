package com.sumologic.epigraph.ideaplugin.schema.features.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.sumologic.epigraph.ideaplugin.schema.brains.ImportsManager;
import com.sumologic.epigraph.ideaplugin.schema.features.actions.fixes.OptimizeImportsQuickFix;
import io.epigraph.lang.parser.psi.EpigraphImportStatement;
import io.epigraph.lang.parser.psi.EpigraphVisitor;
import io.epigraph.lang.parser.psi.SchemaFile;
import io.epigraph.lang.parser.psi.EpigraphImports;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class UnusedImportInspection extends LocalInspectionTool {
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new EpigraphVisitor() {
      @Override
      public void visitImports(@NotNull EpigraphImports epigraphImports) {
        super.visitImports(epigraphImports);

        Set<EpigraphImportStatement> unusedImports = ImportsManager.findUnusedImports((SchemaFile) epigraphImports.getContainingFile());
        for (EpigraphImportStatement unusedImport : unusedImports) {
          holder.registerProblem(unusedImport,
              InspectionBundle.message("import.unused.problem.descriptor"),
              ProblemHighlightType.LIKE_UNUSED_SYMBOL,
              OptimizeImportsQuickFix.INSTANCE);
        }
      }
    };
  }

}
