package ws.epigraph.schema.parser;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaLanguage extends Language {
  public static final SchemaLanguage INSTANCE = new SchemaLanguage();

  private SchemaLanguage() {
    super("epigraph_schema");
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return Common.DESCRIPTION;
  }

  @Override
  public boolean isCaseSensitive() {
    return true;
  }
}
