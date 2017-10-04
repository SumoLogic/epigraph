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
import ws.epigraph.projections.op.OpReferenceContext;
import ws.epigraph.psi.DelegatingPsiProcessingContext;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ResourcePsiProcessingContext extends DelegatingPsiProcessingContext
    implements ReferenceAwarePsiProcessingContext {

  private final @NotNull Qn namespace;
  private final @NotNull String resourceName;
  private final @NotNull OpReferenceContext inputReferenceContext;
  private final @NotNull OpReferenceContext outputReferenceContext;
  private final @NotNull OpReferenceContext deleteReferenceContext;

  public ResourcePsiProcessingContext(
      @NotNull SchemaPsiProcessingContext psiProcessingContext,
      @NotNull Qn namespace,
      @NotNull String resourceName) {

    super(psiProcessingContext);

    this.namespace = namespace;
    this.resourceName = resourceName;

    final Namespaces namespaces = new Namespaces(namespace);

    inputReferenceContext = new OpReferenceContext(
        ProjectionReferenceName.fromQn(
            namespaces.inputProjectionsNamespace(resourceName)
        ),
        psiProcessingContext.inputReferenceContext(),
        psiProcessingContext
    );

    outputReferenceContext = new OpReferenceContext(
        ProjectionReferenceName.fromQn(
            namespaces.outputProjectionsNamespace(resourceName)
        ),
        psiProcessingContext.outputReferenceContext(),
        psiProcessingContext
    );

    deleteReferenceContext = new OpReferenceContext(
        ProjectionReferenceName.fromQn(
            namespaces.deleteProjectionsNamespace(resourceName)
        ),
        psiProcessingContext.deleteReferenceContext(),
        psiProcessingContext
    );
  }

  public @NotNull Qn namespace() { return namespace; }

  public @NotNull String resourceName() { return resourceName; }

  @Override
  public @NotNull OpReferenceContext inputReferenceContext() { return inputReferenceContext; }

  @Override
  public @NotNull OpReferenceContext outputReferenceContext() { return outputReferenceContext; }

  @Override
  public @NotNull OpReferenceContext deleteReferenceContext() { return deleteReferenceContext; }
}
