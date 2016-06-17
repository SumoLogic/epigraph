package com.sumologic.epigraph.ideaplugin.schema.options;

import com.intellij.application.options.editor.AutoImportOptionsProvider;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaAutoImportOptionsProvider implements AutoImportOptionsProvider {
  private JPanel panel;
  private JCheckBox showAutoImportPopups;

  @Nullable
  @Override
  public JComponent createComponent() {
    return panel;
  }

  @Override
  public boolean isModified() {
    return SchemaSettings.getInstance().SHOW_EPIGRAPH_SCHEMA_ADD_IMPORT_HINTS != showAutoImportPopups.isSelected();
  }

  @Override
  public void apply() throws ConfigurationException {
    SchemaSettings.getInstance().SHOW_EPIGRAPH_SCHEMA_ADD_IMPORT_HINTS = showAutoImportPopups.isSelected();
  }

  @Override
  public void reset() {
    showAutoImportPopups.setSelected(SchemaSettings.getInstance().SHOW_EPIGRAPH_SCHEMA_ADD_IMPORT_HINTS);
  }

  @Override
  public void disposeUIResources() {

  }
}
