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
