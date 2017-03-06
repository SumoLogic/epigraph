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
public interface PsiProcessingContext {
  @NotNull
  List<PsiProcessingError> errors();

  default void setErrors(@NotNull List<PsiProcessingError> errors) {
    if (errors() != errors) {
      errors().clear();
      errors().addAll(errors);
    }
  }

  // todo track and provide more information about current location in the errors like resource/operation/field/model/key

  default void addError(@NotNull String message, @NotNull TextLocation location) {
    errors().add(new PsiProcessingError(message, location));
  }

  default void addError(@NotNull String message, @NotNull PsiElement psi) {
    errors().add(new PsiProcessingError(message, psi));
  }
}
