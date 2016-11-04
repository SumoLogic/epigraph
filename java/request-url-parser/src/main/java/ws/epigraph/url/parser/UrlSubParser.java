package ws.epigraph.url.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlSubParser extends UrlParser {
  @NotNull
  private final IElementType entryElementType;

  public UrlSubParser(@NotNull IElementType rootElementType) {
    this.entryElementType = rootElementType;
  }

  @Override
  public void parseLight(IElementType t, PsiBuilder b) {
    super.parseLight(t == null ? entryElementType : t, b);
  }
}
