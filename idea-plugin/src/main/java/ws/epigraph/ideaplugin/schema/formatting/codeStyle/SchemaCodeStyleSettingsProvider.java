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

package ws.epigraph.ideaplugin.schema.formatting.codeStyle;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import ws.epigraph.schema.parser.SchemaLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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
