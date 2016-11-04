package ws.epigraph.ideaplugin.schema.formatting.codeStyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class SchemaCodeStyleSettings extends CustomCodeStyleSettings {
  protected SchemaCodeStyleSettings(CodeStyleSettings container) {
    super("SchemaCodeStyleSettings", container);
  }
}
