package com.sumologic.epigraph.ideaplugin.schema.formatting.codeStyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaCodeStyleSettings extends CustomCodeStyleSettings {
  protected SchemaCodeStyleSettings(CodeStyleSettings container) {
    super("SchemaCodeStyleSettings", container);
  }
}
