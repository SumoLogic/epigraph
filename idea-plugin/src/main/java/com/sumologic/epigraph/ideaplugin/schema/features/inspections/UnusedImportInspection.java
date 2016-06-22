package com.sumologic.epigraph.ideaplugin.schema.features.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.sumologic.epigraph.ideaplugin.schema.brains.ImportsManager;
import com.sumologic.epigraph.ideaplugin.schema.features.actions.fixes.OptimizeImportsQuickFix;
import com.sumologic.epigraph.schema.parser.psi.SchemaFile;
import com.sumologic.epigraph.schema.parser.psi.SchemaImportStatement;
import com.sumologic.epigraph.schema.parser.psi.SchemaImports;
import com.sumologic.epigraph.schema.parser.psi.SchemaVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class UnusedImportInspection extends LocalInspectionTool {
  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return "Unused imports";
  }
  @Nullable
  @Override
  public String getStaticDescription() {
    return "Unused import statement";
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new SchemaVisitor() {
      @Override
      public void visitImports(@NotNull SchemaImports schemaImports) {
        super.visitImports(schemaImports);

        Set<SchemaImportStatement> unusedImports = ImportsManager.findUnusedImports((SchemaFile) schemaImports.getContainingFile());
        for (SchemaImportStatement unusedImport : unusedImports) {
          holder.registerProblem(unusedImport, "Unused import", ProblemHighlightType.LIKE_UNUSED_SYMBOL, OptimizeImportsQuickFix.INSTANCE);
        }
      }
    };
  }

}
