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

package ws.epigraph.ideaplugin.edl.features.actions.fixes;

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
import ws.epigraph.ideaplugin.edl.features.imports.EdlImportOptimizer;
import ws.epigraph.edl.parser.psi.EdlFile;
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
    return file instanceof EdlFile;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    optimizeImports(project, (EdlFile) file);
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
    optimizeImports(project, (EdlFile) file);
  }

  private void optimizeImports(final Project project, final EdlFile file) {
    ImportOptimizer optimizer = new EdlImportOptimizer();

    final Runnable runnable = optimizer.processFile(file);
    new WriteCommandAction.Simple(project, getFamilyName(), file) {
      @Override
      protected void run() throws Throwable {
        runnable.run();
      }
    }.execute();
  }
}
