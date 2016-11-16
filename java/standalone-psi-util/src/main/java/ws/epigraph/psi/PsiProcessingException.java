/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    this(
        cause,
        psi,
        Collections.singletonList(new PsiProcessingError(cause.getMessage(), EpigraphPsiUtil.getLocation(psi)))
    );
  }

  public PsiProcessingException(
      @NotNull Exception cause,
      @NotNull PsiElement psi,
      @NotNull List<PsiProcessingError> precedingErrors) {

    super(cause);
    this.psi = psi;
    this.errors = precedingErrors;
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

  @Override
  public String toString() {
    return super.toString() + " at " + location();
  }
}
