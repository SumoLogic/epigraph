/*
 * Copyright 2017 Sumo Logic
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
import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DelegatingPsiProcessingContext implements PsiProcessingContext {
  final @NotNull PsiProcessingContext delegate;

  public DelegatingPsiProcessingContext(final @NotNull PsiProcessingContext delegate) {this.delegate = delegate;}

  @Override
  public @NotNull List<PsiProcessingError> errors() { return delegate.errors(); }

  @Override
  public void setErrors(final @NotNull List<PsiProcessingError> errors) {
    delegate.setErrors(errors);
  }

  @Override
  public void addError(final @NotNull String message, final @NotNull TextLocation location) {
    delegate.addError(message, location);
  }

  @Override
  public void addError(final @NotNull String message, final @NotNull PsiElement psi) {
    delegate.addError(message, psi);
  }
}
