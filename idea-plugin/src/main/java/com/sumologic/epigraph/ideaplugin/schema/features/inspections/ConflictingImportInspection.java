package com.sumologic.epigraph.ideaplugin.schema.features.inspections;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.containers.MultiMap;
import com.sumologic.epigraph.ideaplugin.schema.brains.ImportsManager;
import com.sumologic.epigraph.ideaplugin.schema.features.actions.fixes.OptimizeImportsQuickFix;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.SchemaImportStatement;
import com.sumologic.epigraph.schema.parser.psi.SchemaImports;
import com.sumologic.epigraph.schema.parser.psi.SchemaVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ConflictingImportInspection extends LocalInspectionTool {
  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return "Conflicting imports";
  }

  @Nullable
  @Override
  public String getStaticDescription() {
    return "Import statements should not have clashing suffixes, for instance 'import foo.bar' and 'import baz.bar'";
  }

  @NotNull
  @Override
  public HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.ERROR;
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new SchemaVisitor() {
      @Override
      public void visitImports(@NotNull SchemaImports schemaImports) {
        super.visitImports(schemaImports);

        List<SchemaImportStatement> imports = schemaImports.getImportStatementList();
        MultiMap<Fqn, SchemaImportStatement> importsByFqn = ImportsManager.getImportsByFqn(imports);

        MultiMap<String, SchemaImportStatement> importsByLastSegment = new MultiMap<>(importsByFqn.size(), 0.75f);
        for (Map.Entry<Fqn, Collection<SchemaImportStatement>> entry : importsByFqn.entrySet()) {
          String lastSegment = entry.getKey().last();
          assert lastSegment != null;
          importsByLastSegment.putValue(lastSegment, entry.getValue().iterator().next()); // take only first one so we don't report duplicate imports as conflicts
        }

        for (Map.Entry<String, Collection<SchemaImportStatement>> entry : importsByLastSegment.entrySet()) {
          Collection<SchemaImportStatement> conflictingImports = entry.getValue();
          if (conflictingImports.size() > 1) {
            for (SchemaImportStatement conflictingImport : conflictingImports) {
              Collection<String> conflictingImportsStrings = conflictingImports.stream()
                  .filter(i -> i != conflictingImport)
                  .map(i -> "\"" + i.getText() + "\"")
                  .collect(Collectors.toList());

              holder.registerProblem(conflictingImport,
                  String.format("\"%s\" conflicts with %s",
                      conflictingImport.getText(),
                      String.join(", ", conflictingImportsStrings)),
                  OptimizeImportsQuickFix.INSTANCE
              );
            }
          }
        }
      }
    };
  }
}
