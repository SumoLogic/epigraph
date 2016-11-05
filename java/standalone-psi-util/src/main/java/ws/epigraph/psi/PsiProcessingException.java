package ws.epigraph.psi;

import com.intellij.psi.PsiElement;
import ws.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Indicates a non-recoverable PSI processing error.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PsiProcessingException extends Exception {
  @NotNull
  private final PsiElement psi;
  @NotNull
  private final List<PsiProcessingError> errors; // last item = this exception

  @Deprecated
  public PsiProcessingException(@NotNull String message, @NotNull PsiElement psi) {
    this(message, psi, Collections.emptyList());
  }

  public PsiProcessingException(
      @NotNull String message,
      @NotNull PsiElement psi,
      @NotNull List<PsiProcessingError> precedingErrors) {

    super(message);
    this.psi = psi;
    if (precedingErrors.isEmpty())
      errors = Collections.singletonList(new PsiProcessingError(message, EpigraphPsiUtil.getLocation(psi)));
    else {
      this.errors = new ArrayList<>(precedingErrors);
      errors.add(new PsiProcessingError(message, EpigraphPsiUtil.getLocation(psi)));
    }
  }

  @Deprecated
  public PsiProcessingException(@NotNull Exception cause, @NotNull PsiElement psi) {
    super(cause);
    this.psi = psi;
    errors = Collections.singletonList(new PsiProcessingError(cause.getMessage(), EpigraphPsiUtil.getLocation(psi)));
  }

  @NotNull
  public PsiElement psi() { return psi; }

  /**
   * @return list of errors, including this one (will be the last item)
   */
  @NotNull
  public List<PsiProcessingError> errors() { return errors; }

  /**
   * @return this exception converted to an error
   */
  @NotNull
  public PsiProcessingError toError() { return errors.get(errors.size() - 1); }

  @NotNull
  public TextLocation location() { return toError().location(); }

}
