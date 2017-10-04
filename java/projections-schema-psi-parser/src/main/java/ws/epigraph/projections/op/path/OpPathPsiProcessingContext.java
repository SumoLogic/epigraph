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

package ws.epigraph.projections.op.path;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.op.OpPsiProcessingContext;
import ws.epigraph.psi.DelegatingPsiProcessingContext;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingMessage;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPathPsiProcessingContext extends DelegatingPsiProcessingContext {
  private final OpPsiProcessingContext inputPsiProcessingContext;

  public OpPathPsiProcessingContext(
      final @NotNull PsiProcessingContext psiProcessingContext,
      final @NotNull OpPsiProcessingContext context2) {
    super(psiProcessingContext);
    inputPsiProcessingContext = context2;
  }

  @Override
  public @NotNull List<PsiProcessingMessage> messages() { return inputPsiProcessingContext.messages(); }

  public @NotNull OpPsiProcessingContext inputPsiProcessingContext() { return inputPsiProcessingContext; }
}
