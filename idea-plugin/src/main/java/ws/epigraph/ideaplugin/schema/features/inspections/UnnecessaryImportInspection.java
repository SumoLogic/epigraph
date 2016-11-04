package ws.epigraph.ideaplugin.schema.features.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
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

import static ws.epigraph.lang.DefaultImports.DEFAULT_IMPORTS_LIST;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UnnecessaryImportInspection extends LocalInspectionTool {
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new SchemaVisitor() {
      @Override
      public void visitImports(@NotNull SchemaImports schemaImports) {
        super.visitImports(schemaImports);

        List<SchemaImportStatement> imports = schemaImports.getImportStatementList();
        MultiMap<Qn, SchemaImportStatement> importsByQn = ImportsManager.getImportsByQn(imports);

        for (Map.Entry<Qn, Collection<SchemaImportStatement>> entry : importsByQn.entrySet()) {
          entry.getValue().stream()
              .filter(is -> DEFAULT_IMPORTS_LIST.contains(entry.getKey()))
              .forEach(is -> holder.registerProblem(is,
                  InspectionBundle.message("import.unnecessary.problem.descriptor"),
                  ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                  OptimizeImportsQuickFix.INSTANCE));
        }
      }
    };
  }
}
