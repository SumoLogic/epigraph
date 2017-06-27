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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Indicates a non-recoverable PSI processing error.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PsiProcessingException extends Exception {
  private final @NotNull TextLocation location;
  private final @NotNull List<PsiProcessingMessage> messages; // last item = this exception

  public PsiProcessingException(
      @NotNull String message,
      @NotNull TextLocation location,
      @NotNull List<PsiProcessingMessage> precedingMessages) {

    super(message);
    this.location = location;

    if (precedingMessages.isEmpty())
      messages = Collections.singletonList(PsiProcessingMessage.error(message, location));
    else {
      this.messages = new ArrayList<>(precedingMessages);
      messages.add(PsiProcessingMessage.error(message, location));
    }
  }

  public PsiProcessingException(
      @NotNull String message,
      @NotNull PsiElement psi,
      @NotNull List<PsiProcessingMessage> precedingMessages) {

    this(
        message,
        EpigraphPsiUtil.getLocation(psi),
        precedingMessages
    );
  }

  public PsiProcessingException(
      @NotNull String message,
      @NotNull PsiElement psi,
      @NotNull PsiProcessingContext context) {

    this(message, psi, context.messages());
  }

  public PsiProcessingException(
      @NotNull String message,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) {

    this(message, location, context.messages());
  }

  public PsiProcessingException(
      @NotNull Exception cause,
      @NotNull PsiElement psi,
      @NotNull List<PsiProcessingMessage> precedingMessages) {

    super(cause);
    this.location = EpigraphPsiUtil.getLocation(psi);
    final String message = cause.getMessage();

    if (precedingMessages.isEmpty())
      messages = Collections.singletonList(PsiProcessingMessage.error(message, EpigraphPsiUtil.getLocation(psi)));
    else {
      this.messages = new ArrayList<>(precedingMessages);
      messages.add(PsiProcessingMessage.error(message, EpigraphPsiUtil.getLocation(psi)));
    }
  }

  public PsiProcessingException(
      @NotNull Exception cause,
      @NotNull PsiElement psi,
      @NotNull PsiProcessingContext context) {

    this(cause, psi, context.messages());
  }

  /**
   * @return list of messages, including this error (will be the last item)
   */
  public @NotNull List<PsiProcessingMessage> messages() { return messages; }

  /**
   * @return this exception converted to an error
   */
  public @NotNull PsiProcessingMessage toMessage() { return messages.get(messages.size() - 1); }

  public @NotNull TextLocation location() { return location; }

  @Override
  public String toString() { return super.toString() + " at " + location(); }
}
