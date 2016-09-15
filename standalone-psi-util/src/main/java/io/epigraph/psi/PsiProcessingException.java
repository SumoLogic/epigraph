package io.epigraph.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PsiProcessingException extends Exception {
  @NotNull
  private final PsiElement psi;

  public PsiProcessingException(@NotNull PsiErrorElement psiErrorElement) {
    // todo extract line numbers, similar to CError/CErrorPosition
    super(psiErrorElement.getErrorDescription());
    psi = psiErrorElement;
  }

  public PsiProcessingException(@NotNull String message, @NotNull PsiElement location) {
    super(message);
    psi = location;
  }

  @NotNull
  public PsiElement psi() {
    return psi;
  }
}
