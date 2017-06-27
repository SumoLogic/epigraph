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
import ws.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PsiProcessingMessage {
  private final @NotNull Level level;
  private final @NotNull String message;
  private final @NotNull TextLocation location;


  public static @NotNull PsiProcessingMessage error(@NotNull String message, @NotNull TextLocation location) {
    return new PsiProcessingMessage(Level.ERROR, message, location);
  }

  public static @NotNull PsiProcessingMessage warning(@NotNull String message, @NotNull TextLocation location) {
    return new PsiProcessingMessage(Level.WARNING, message, location);
  }

  public PsiProcessingMessage(@NotNull Level level, @NotNull String message, @NotNull TextLocation location) {
    this.level = level;
    this.message = message;
    this.location = location;
  }

  public PsiProcessingMessage(@NotNull Level level, @NotNull String message, @NotNull PsiElement psi) {
    this(level, message, EpigraphPsiUtil.getLocation(psi));
  }

  public @NotNull Level level() { return level; }

  public @NotNull String message() { return message; }

  public @NotNull TextLocation location() { return location; }

  @Override
  public String toString() {
    return String.format("[%s] %s at %s", level(), message(), location());
  }

  public enum Level {ERROR, WARNING}
}
