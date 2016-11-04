package ws.epigraph.ideaplugin.schema.features.actions.fixes;

import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import ws.epigraph.ideaplugin.schema.features.imports.SchemaImportOptimizer;
import ws.epigraph.schema.parser.psi.SchemaFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OptimizeImportsQuickFix implements LocalQuickFix, IntentionAction, HighPriorityAction {
  public static final OptimizeImportsQuickFix INSTANCE = new OptimizeImportsQuickFix();

  @Nls
  @NotNull
  @Override
  public String getName() {
    return "Optimize imports";
  }

  @Nls
  @NotNull
  @Override
  public String getText() {
    return getName();
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return getName();
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
    return file instanceof SchemaFile;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    optimizeImports(project, (SchemaFile) file);
  }

  @Override
  public boolean startInWriteAction() {
    return false;
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    PsiElement element = descriptor.getPsiElement();
    if (element == null) {  // stale PSI
      return;
    }
    final PsiFile file = element.getContainingFile();
    optimizeImports(project, (SchemaFile) file);
  }

  private void optimizeImports(final Project project, final SchemaFile file) {
    ImportOptimizer optimizer = new SchemaImportOptimizer();

    final Runnable runnable = optimizer.processFile(file);
    new WriteCommandAction.Simple(project, getFamilyName(), file) {
      @Override
      protected void run() throws Throwable {
        runnable.run();
      }
    }.execute();
  }
}
