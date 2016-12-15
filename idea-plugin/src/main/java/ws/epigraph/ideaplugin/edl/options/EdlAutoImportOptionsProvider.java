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

package ws.epigraph.ideaplugin.edl.options;

import com.intellij.application.options.editor.AutoImportOptionsProvider;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlAutoImportOptionsProvider implements AutoImportOptionsProvider {
  private JPanel panel;
  private JCheckBox showAutoImportPopups;

  @Nullable
  @Override
  public JComponent createComponent() {
    return panel;
  }

  @Override
  public boolean isModified() {
    return EdlSettings.getInstance().SHOW_EDL_ADD_IMPORT_HINTS != showAutoImportPopups.isSelected();
  }

  @Override
  public void apply() throws ConfigurationException {
    EdlSettings.getInstance().SHOW_EDL_ADD_IMPORT_HINTS = showAutoImportPopups.isSelected();
  }

  @Override
  public void reset() {
    showAutoImportPopups.setSelected(EdlSettings.getInstance().SHOW_EDL_ADD_IMPORT_HINTS);
  }

  @Override
  public void disposeUIResources() {

  }
}
