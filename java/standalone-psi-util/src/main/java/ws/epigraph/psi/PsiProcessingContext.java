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
  List<PsiProcessingMessage> messages();

  default void setErrors(@NotNull List<PsiProcessingMessage> messages) {
    if (messages() != messages) {
      messages().clear();
      messages().addAll(messages);
    }
  }

  // todo track and provide more information about current location in the errors like resource/operation/field/model/key
  // todo PsiProcessingException ctor should only be called from this class
  // todo PsiProcessingException should also hold additional context information (see first todo)

  default void addError(@NotNull String message, @NotNull TextLocation location) {
    messages().add(PsiProcessingMessage.error(message, location));
  }

  default void addError(@NotNull String message, @NotNull PsiElement psi) {
    messages().add(new PsiProcessingMessage(PsiProcessingMessage.Level.ERROR, message, psi));
  }

  default void addWarning(@NotNull String message, @NotNull TextLocation location) {
    messages().add(PsiProcessingMessage.warning(message, location));
  }

  default void addWarning(@NotNull String message, @NotNull PsiElement psi) {
    messages().add(new PsiProcessingMessage(PsiProcessingMessage.Level.WARNING, message, psi));
  }

  default void addException(@NotNull PsiProcessingException ex) {
//    final PsiProcessingError error = ex.toError();
//    addError(error.message(), error.location());
    messages().add(ex.toMessage());
  }
}
