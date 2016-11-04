package ws.epigraph.idl.parser;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlLanguage extends Language {
  public static final IdlLanguage INSTANCE = new IdlLanguage();

  private IdlLanguage() {
    super("epigraph_idl");
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
