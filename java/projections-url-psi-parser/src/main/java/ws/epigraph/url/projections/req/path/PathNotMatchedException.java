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

package ws.epigraph.url.projections.req.path;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingMessage;
import ws.epigraph.psi.PsiProcessingException;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PathNotMatchedException extends PsiProcessingException {
  public PathNotMatchedException(
      final @NotNull String message,
      final @NotNull PsiElement psi,
      final @NotNull List<PsiProcessingMessage> precedingErrors) {
    super(message, psi, precedingErrors);
  }

  public PathNotMatchedException(
      final @NotNull String message,
      final @NotNull PsiElement psi,
      final @NotNull PsiProcessingContext context) {
    super(message, psi, context);
  }
}
