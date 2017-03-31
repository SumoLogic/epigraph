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
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.delete.OpDeleteReferenceContext;
import ws.epigraph.projections.op.input.OpInputReferenceContext;
import ws.epigraph.projections.op.output.OpOutputReferenceContext;
import ws.epigraph.psi.DelegatingPsiProcessingContext;
import ws.epigraph.psi.PsiProcessingContext;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ResourcePsiProcessingContext extends DelegatingPsiProcessingContext {
  private final @NotNull Qn namespace;
  private final @NotNull String resourceName;
  private final @NotNull OpInputReferenceContext inputReferenceContext;
  private final @NotNull OpOutputReferenceContext outputReferenceContext;
  private final @NotNull OpDeleteReferenceContext deleteReferenceContext;

  public ResourcePsiProcessingContext(
      final @NotNull PsiProcessingContext psiProcessingContext,
      @NotNull Qn namespace,
      @NotNull String resourceName) {

    super(psiProcessingContext);

    this.namespace = namespace;
    this.resourceName = resourceName;

    final Namespaces namespaces = new Namespaces(namespace);

    inputReferenceContext = new OpInputReferenceContext(
        ProjectionReferenceName.fromQn(
            namespaces.inputProjectionsNamespace(resourceName)
        ),
        null,
        psiProcessingContext
    );

    outputReferenceContext = new OpOutputReferenceContext(
        ProjectionReferenceName.fromQn(
            namespaces.outputProjectionsNamespace(resourceName)
        ),
        null,
        psiProcessingContext
    );

    deleteReferenceContext = new OpDeleteReferenceContext(
        ProjectionReferenceName.fromQn(
            namespaces.deleteProjectionsNamespace(resourceName)
        ),
        null,
        psiProcessingContext
    );
  }

  public @NotNull Qn namespace() { return namespace; }

  public @NotNull String resourceName() { return resourceName; }

  public @NotNull OpInputReferenceContext inputReferenceContext() { return inputReferenceContext; }

  public @NotNull OpOutputReferenceContext outputReferenceContext() { return outputReferenceContext; }

  public @NotNull OpDeleteReferenceContext deleteReferenceContext() { return deleteReferenceContext; }
}
