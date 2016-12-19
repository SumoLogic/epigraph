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

package ws.epigraph.ideaplugin.schema.features.actions;

import com.intellij.ide.IdeView;
import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import ws.epigraph.ideaplugin.schema.index.SchemaFileIndexUtil;
import ws.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import ws.epigraph.schema.parser.Common;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CreateSchemaFileAction extends CreateFileFromTemplateAction implements DumbAware {
  public static final String NEW_SCHEMA_FILE = "New Epigraph Schema File";

  public CreateSchemaFileAction() {
    super(NEW_SCHEMA_FILE, "", SchemaPresentationUtil.schemaFileIcon());
  }

  @Override
  protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
    builder.setTitle(NEW_SCHEMA_FILE)
        .addKind("Empty Schema", SchemaPresentationUtil.schemaFileIcon(), "Epigraph Schema." + Common.FILE_EXTENSION)
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
  protected boolean isAvailable(DataContext dataContext) {
    if (!super.isAvailable(dataContext)) return false;

    final Project project = CommonDataKeys.PROJECT.getData(dataContext);
    assert project != null; // ensured by super

    final IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
    assert view != null; // ensured by super

    PsiDirectory[] directories = view.getDirectories();
    if (directories.length != 1) return false;

    PsiDirectory directory = directories[0];

    return SchemaFileIndexUtil.fileUnderSources(project, directory.getVirtualFile());
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
