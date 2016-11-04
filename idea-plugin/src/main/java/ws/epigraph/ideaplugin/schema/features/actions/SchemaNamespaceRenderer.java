package ws.epigraph.ideaplugin.schema.features.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.*;
import java.awt.*;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
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
