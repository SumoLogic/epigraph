package io.epigraph.lang;

import com.intellij.lang.Language;
import com.sumologic.epigraph.schema.parser.Common;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphLanguage extends Language {
  public static final EpigraphLanguage INSTANCE = new EpigraphLanguage();

  private EpigraphLanguage() {
    super("epigraph");
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
