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

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.*;
import java.awt.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaNamespaceRenderer extends ColoredListCellRenderer {
  public static SchemaNamespaceRenderer INSTANCE = new SchemaNamespaceRenderer();

  public SchemaNamespaceRenderer() {
    EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
    setFont(new Font(scheme.getEditorFontName(), Font.PLAIN, scheme.getEditorFontSize()));
  }

  @Override
  protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus) {
    append(value.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
    setIcon(AllIcons.Nodes.Static);
    setPaintFocusBorder(false);
  }
}
