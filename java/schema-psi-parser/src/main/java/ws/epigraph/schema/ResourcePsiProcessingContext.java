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

package ws.epigraph.schema;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.op.delete.OpDeleteVarReferenceContext;
import ws.epigraph.projections.op.input.OpInputVarReferenceContext;
import ws.epigraph.projections.op.output.OpOutputVarReferenceContext;
import ws.epigraph.psi.DelegatingPsiProcessingContext;
import ws.epigraph.psi.PsiProcessingContext;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ResourcePsiProcessingContext extends DelegatingPsiProcessingContext {
  private final @NotNull Qn resourceNamespace;
  private final @NotNull OpInputVarReferenceContext inputVarReferenceContext;
  private final @NotNull OpOutputVarReferenceContext outputVarReferenceContext;
  private final @NotNull OpDeleteVarReferenceContext deleteVarReferenceContext;

  public ResourcePsiProcessingContext(
      final @NotNull PsiProcessingContext psiProcessingContext,
      @NotNull Qn namespace,
      @NotNull String resourceName) {

    super(psiProcessingContext);

    resourceNamespace = namespace.append("resources").append(resourceName);
    Qn projectionsNamespace = resourceNamespace.append("projections");

    inputVarReferenceContext = new OpInputVarReferenceContext(
        projectionsNamespace.append("input"),
        null
    );

    outputVarReferenceContext = new OpOutputVarReferenceContext(
        projectionsNamespace.append("ouput"),
        null
    );

    deleteVarReferenceContext = new OpDeleteVarReferenceContext(
        projectionsNamespace.append("delete"),
        null
    );
  }

  public @NotNull OpInputVarReferenceContext inputVarReferenceContext() { return inputVarReferenceContext; }

  public @NotNull OpOutputVarReferenceContext outputVarReferenceContext() { return outputVarReferenceContext; }

  public @NotNull OpDeleteVarReferenceContext deleteVarReferenceContext() { return deleteVarReferenceContext; }

  public @NotNull Qn resourceNamespace() { return resourceNamespace; }
}
