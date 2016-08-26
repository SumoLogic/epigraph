package com.sumologic.epigraph.ideaplugin.schema.features.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import io.epigraph.lang.parser.Common;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class CreateSchemaFileAction extends CreateFileFromTemplateAction implements DumbAware {
  public static final String NEW_SCHEMA_FILE = "New Epigraph Schema File";

  public CreateSchemaFileAction() {
    super(NEW_SCHEMA_FILE, "", SchemaPresentationUtil.SCHEMA_FILE_ICON);
  }

  @Override
  protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
    builder.setTitle(NEW_SCHEMA_FILE)
        .addKind("Empty schema", SchemaPresentationUtil.SCHEMA_FILE_ICON, "Epigraph Schema." + Common.FILE_EXTENSION)
        .setValidator(new InputValidator() {
          @Override
          public boolean checkInput(String inputString) {
            return true;
          }

          @Override
          public boolean canClose(String inputString) {
            return true;
          }
        });
  }

  @Override
  protected String getActionName(PsiDirectory directory, String newName, String templateName) {
    return NEW_SCHEMA_FILE;
  }

  @Override
  protected void postProcess(PsiFile createdElement, String templateName, Map<String, String> customProperties) {
    super.postProcess(createdElement, templateName, customProperties);

    final Project project = createdElement.getProject();
    final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    if (editor != null) {
      editor.getCaretModel().moveToOffset(createdElement.getTextRange().getEndOffset());
    }
  }

}
