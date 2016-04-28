package com.sumologic.dohyo.plugin.schema.formatting.codeStyle;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.sumologic.dohyo.plugin.schema.SchemaLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
  @Nullable
  @Override
  public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
    return new SchemaCodeStyleSettings(settings);
  }

  @NotNull
  @Override
  public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
    return new CodeStyleAbstractConfigurable(settings, originalSettings, getConfigurableDisplayName()) {
      @Override
      protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
        final CodeStyleSettings currentSettings = getCurrentSettings();

        return new TabbedLanguageCodeStylePanel(SchemaLanguage.INSTANCE, currentSettings, settings) {
          @Override
          protected void initTabs(CodeStyleSettings settings) {
            addIndentOptionsTab(settings);
            // TODO more setting tabs
          }
        };
      }

      @Nullable
      @Override
      public String getHelpTopic() {
        return null;
      }
    };
  }

  @Nullable
  @Override
  public String getConfigurableDisplayName() {
    return SchemaLanguage.INSTANCE.getDisplayName();
  }
}
