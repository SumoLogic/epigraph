package ws.epigraph.url.lexer;

import com.intellij.psi.tree.IElementType;
import ws.epigraph.url.parser.UrlLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlElementType extends IElementType {
  public UrlElementType(@NotNull @NonNls String debugName) {
    super(debugName, UrlLanguage.INSTANCE);
  }
}
