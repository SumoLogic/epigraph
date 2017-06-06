/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.ideaplugin.schema.features.actions.fixes;

import com.intellij.codeInsight.completion.actions.CodeCompletionAction;
import com.intellij.codeInsight.intention.LowPriorityAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import ws.epigraph.ideaplugin.schema.SchemaBundle;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.TypeMembers;
import ws.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class AddRetroAction extends PsiElementBaseIntentionAction implements LowPriorityAction {
  @Nls
  @NotNull
  @Override
  public String getText() {
    return SchemaBundle.message("actions.add.retro");
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return getText();
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
    if (element.getNode().getElementType() == TokenType.WHITE_SPACE) element = PsiTreeUtil.prevVisibleLeaf(element);

    SchemaValueTypeRef valueTypeRef = PsiTreeUtil.getParentOfType(element, SchemaValueTypeRef.class);
    if (valueTypeRef != null) {
      int endOffset = valueTypeRef.getNode().getTextRange().getEndOffset();
      editor.getCaretModel().moveToOffset(endOffset);
      EditorModificationUtil.insertStringAtCaret(editor, " default ");

      ApplicationManager.getApplication().invokeLater(() -> {
        AnActionEvent event = AnActionEvent.createFromDataContext("AddDefaultAction",
            new Presentation(), ((EditorEx) editor).getDataContext());

        new CodeCompletionAction().actionPerformed(event);
      });
    }
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
    if (element.getNode().getElementType() == TokenType.WHITE_SPACE) element = PsiTreeUtil.prevVisibleLeaf(element);

    SchemaValueTypeRef valueTypeRef = PsiTreeUtil.getParentOfType(element, SchemaValueTypeRef.class);
    SchemaEntityTypeDef entityTypeDef = findEntityTypeDef(valueTypeRef);

    return entityTypeDef != null && !TypeMembers.getEntityTagDecls(entityTypeDef, null).isEmpty();
  }

  private SchemaEntityTypeDef findEntityTypeDef(@Nullable SchemaValueTypeRef valueTypeRef) {
    if (valueTypeRef != null && valueTypeRef.getRetroDecl() == null) {
      SchemaTypeRef typeRef = valueTypeRef.getTypeRef();
      if (typeRef instanceof SchemaQnTypeRef) {
        SchemaQnTypeRef fqnTypeRef = (SchemaQnTypeRef) typeRef;
        SchemaTypeDef typeDef = fqnTypeRef.resolve();
        if (typeDef instanceof SchemaEntityTypeDef) {
          return (SchemaEntityTypeDef) typeDef;
        }
      }
    }

    return null;
  }

  @Override
  public boolean startInWriteAction() {
    return true;
  }
}
