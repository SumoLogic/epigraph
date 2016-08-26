package io.epigraph.lang.idl;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlLanguage extends Language {
  public static final IdlLanguage INSTANCE = new IdlLanguage();

  private IdlLanguage() {
    super("epi_idl");
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Epigraph IDL";
  }

  @Override
  public boolean isCaseSensitive() {
    return true;
  }
}
