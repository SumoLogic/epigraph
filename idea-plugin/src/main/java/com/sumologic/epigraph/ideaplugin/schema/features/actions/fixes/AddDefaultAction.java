package com.sumologic.epigraph.ideaplugin.schema.features.actions.fixes;

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
import com.sumologic.epigraph.ideaplugin.schema.SchemaBundle;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.TypeMembers;
import io.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class AddDefaultAction extends PsiElementBaseIntentionAction implements LowPriorityAction {
  @Nls
  @NotNull
  @Override
  public String getText() {
    return SchemaBundle.message("actions.add.default");
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
    SchemaVarTypeDef varTypeDef = findVarTypeDef(valueTypeRef);

    return varTypeDef != null && !TypeMembers.getVarTagDecls(varTypeDef, null).isEmpty();
  }

  private SchemaVarTypeDef findVarTypeDef(@Nullable SchemaValueTypeRef valueTypeRef) {
    if (valueTypeRef != null && valueTypeRef.getDefaultOverride() == null) {
      SchemaTypeRef typeRef = valueTypeRef.getTypeRef();
      if (typeRef instanceof SchemaFqnTypeRef) {
        SchemaFqnTypeRef fqnTypeRef = (SchemaFqnTypeRef) typeRef;
        SchemaTypeDef typeDef = fqnTypeRef.resolve();
        if (typeDef instanceof SchemaVarTypeDef) {
          return (SchemaVarTypeDef) typeDef;
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
