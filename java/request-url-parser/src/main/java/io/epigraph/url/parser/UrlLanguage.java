package io.epigraph.url.parser;

import com.intellij.lang.Language;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlLanguage extends Language {
  public static final UrlLanguage INSTANCE = new UrlLanguage();

  private UrlLanguage() { super("epigraph_url"); }

  @Override
  public boolean isCaseSensitive() { return true; }
}
