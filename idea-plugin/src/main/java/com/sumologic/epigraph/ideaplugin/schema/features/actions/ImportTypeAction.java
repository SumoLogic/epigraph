package com.sumologic.epigraph.ideaplugin.schema.features.actions;

import com.intellij.codeInsight.hint.QuestionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.ui.components.JBList;
import com.sumologic.epigraph.ideaplugin.schema.brains.ImportsManager;
import io.epigraph.lang.schema.parser.psi.SchemaFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ImportTypeAction implements QuestionAction {
  @NotNull
  private final SchemaFile file;
  @NotNull
  private final List<String> importOptions;
  @NotNull
  private final Editor editor;

  public ImportTypeAction(@NotNull SchemaFile file, @NotNull List<String> importOptions, @NotNull Editor editor) {
    this.file = file;
    this.importOptions = importOptions;
    this.editor = editor;
  }

  @Override
  public boolean execute() {

    final JList list = new JBList(importOptions);
    //noinspection unchecked
    list.setCellRenderer(SchemaNamespaceRenderer.INSTANCE);
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

  public static void addImport(@NotNull SchemaFile file, @NotNull String namespace) {
    // TODO: try to insert an import, fallback to adding a prefix to the FQN in case of a clash
    // see ImportNSAction for a full example

    ImportsManager.addImport(file, namespace);
  }
}
