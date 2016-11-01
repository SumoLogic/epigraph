package io.epigraph.url.projections.req.path;

import com.intellij.psi.PsiElement;
import io.epigraph.psi.PsiProcessingError;
import io.epigraph.psi.PsiProcessingException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PathNotMatchedException extends PsiProcessingException {
  public PathNotMatchedException(
      @NotNull final String message,
      @NotNull final PsiElement psi,
      @NotNull final List<PsiProcessingError> precedingErrors) {
    super(message, psi, precedingErrors);
  }
}
