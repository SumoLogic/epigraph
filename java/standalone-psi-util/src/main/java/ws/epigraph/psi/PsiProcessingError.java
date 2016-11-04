package ws.epigraph.psi;

import com.intellij.psi.PsiElement;
import ws.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PsiProcessingError {
  @NotNull
  private final String message;
  @NotNull
  private final TextLocation location;

  public PsiProcessingError(@NotNull String message, @NotNull TextLocation location) {
    this.message = message;
    this.location = location;
  }

  public PsiProcessingError(@NotNull String message, @NotNull PsiElement psi) {
    this(message, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public String message() { return message; }

  @NotNull
  public TextLocation location() { return location; }
}
