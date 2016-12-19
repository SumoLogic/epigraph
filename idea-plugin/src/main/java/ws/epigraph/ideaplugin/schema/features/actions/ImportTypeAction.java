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

import com.intellij.codeInsight.hint.QuestionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.components.JBList;
import ws.epigraph.ideaplugin.schema.brains.ImportsManager;
import ws.epigraph.schema.parser.psi.EdlFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ImportTypeAction implements QuestionAction {
  @NotNull
  private final EdlFile file;
  @NotNull
  private final List<String> importOptions;
  @NotNull
  private final Editor editor;

  public ImportTypeAction(@NotNull EdlFile file, @NotNull List<String> importOptions, @NotNull Editor editor) {
    this.file = file;
    this.importOptions = importOptions;
    this.editor = editor;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean execute() {

    final JList list = new JBList(importOptions);
    list.setCellRenderer(EdlNamespaceRenderer.INSTANCE);
    list.setSelectedIndex(0);

    final Runnable runnable = () -> {
      final String namespace = (String) list.getSelectedValue();
      if (namespace != null) {
        final Project project = file.getProject();
        new WriteCommandAction.Simple(project, file) {
          @Override
          protected void run() throws Throwable {
            addImport(file, namespace);
          }
        }.execute();
      }
    };

    if (list.getModel().getSize() == 1) {
      runnable.run();
    } else {
      new PopupChooserBuilder(list)
          .setTitle("Select namespace to import")
          .setItemChoosenCallback(runnable)
          .createPopup()
          .showInBestPositionFor(editor);
    }


    return true;
  }

  public static void addImport(@NotNull EdlFile file, @NotNull String namespace) {
    // TODO: try to insert an import, fallback to adding a prefix to the FQN in case of a clash
    // see ImportNSAction for a full example

    ImportsManager.addImport(file, namespace);
  }
}
