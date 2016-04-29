package com.sumologic.epigraph.ideaplugin.schema;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaLanguage extends Language {
  public static final SchemaLanguage INSTANCE = new SchemaLanguage();

  private SchemaLanguage() {
    super("epi_schema", "text/epi_schema");
    // todo syntax highlighter
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Epigraph Schema"; // TODO
  }

  @Override
  public boolean isCaseSensitive() {
    return true;
  }
}
