package ws.epigraph.ideaplugin.schema.formatting.codeStyle;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import ws.epigraph.schema.parser.SchemaLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
  @Nullable
  @Override
  public CommonCodeStyleSettings getDefaultCommonSettings() {
    CommonCodeStyleSettings defaultSettings = new CommonCodeStyleSettings(getLanguage());
    CommonCodeStyleSettings.IndentOptions indentOptions = defaultSettings.initIndentOptions();
    indentOptions.TAB_SIZE = 2;
    indentOptions.INDENT_SIZE = 2;
    indentOptions.CONTINUATION_INDENT_SIZE = 4;
    indentOptions.USE_TAB_CHARACTER = false;
    return defaultSettings;
  }

  @NotNull
  @Override
  public Language getLanguage() {
    return SchemaLanguage.INSTANCE;
  }

  @Override
  public IndentOptionsEditor getIndentOptionsEditor() {
    return new SmartIndentOptionsEditor();
  }

  @Override
  public String getCodeSample(@NotNull SettingsType settingsType) {
    return "namespace test1\n\nrecord MyRecord {\n  someField: Integer " +
        "{\n    doc = \"doc string\"\n  }\n}";
  }
}
