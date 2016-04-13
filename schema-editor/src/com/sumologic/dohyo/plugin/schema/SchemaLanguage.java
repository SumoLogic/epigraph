package com.sumologic.dohyo.plugin.schema;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaLanguage extends Language {
  public static final SchemaLanguage INSTANCE = new SchemaLanguage();

  private SchemaLanguage() {
    super("schema", "text/schema");
    // todo syntax highlighter
  }

  @Override
  public boolean isCaseSensitive() {
    return true;
  }
}
