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
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpReferenceContext;
import ws.epigraph.psi.DelegatingPsiProcessingContext;
import ws.epigraph.psi.PsiProcessingContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaPsiProcessingContext extends DelegatingPsiProcessingContext
    implements ReferenceAwarePsiProcessingContext {

  private final @NotNull Qn namespace;
  private final @NotNull OpReferenceContext inputReferenceContext;
  private final @NotNull OpReferenceContext outputReferenceContext;
  private final @NotNull OpReferenceContext deleteReferenceContext;

  private final @NotNull Map<String, ResourceDeclaration> resources = new LinkedHashMap<>();
  private final @NotNull Map<String, TransformerDeclaration> transformers = new LinkedHashMap<>();

  public SchemaPsiProcessingContext(
      final @NotNull PsiProcessingContext psiProcessingContext,
      @NotNull Qn namespace) {

    super(psiProcessingContext);

    this.namespace = namespace;

    final Namespaces namespaces = new Namespaces(namespace);

    inputReferenceContext = new OpReferenceContext(
        ProjectionReferenceName.fromQn(namespaces.inputProjectionsNamespace()),
        null,
        psiProcessingContext
    );

    outputReferenceContext = new OpReferenceContext(
        ProjectionReferenceName.fromQn(namespaces.outputProjectionsNamespace()),
        null,
        psiProcessingContext
    );

    deleteReferenceContext = new OpReferenceContext(
        ProjectionReferenceName.fromQn(namespaces.deleteProjectionsNamespace()),
        null,
        psiProcessingContext
    );
  }

  public @NotNull Qn namespace() { return namespace; }

  @Override
  public @NotNull OpReferenceContext inputReferenceContext() { return inputReferenceContext; }

  @Override
  public @NotNull OpReferenceContext outputReferenceContext() { return outputReferenceContext; }

  @Override
  public @NotNull OpReferenceContext deleteReferenceContext() { return deleteReferenceContext; }

  public @Nullable ResourceDeclaration resource(@NotNull String name) { return resources.get(name); }

  public void addResource(@NotNull ResourceDeclaration resource) {
    assert !resources.containsKey(resource.fieldName()) : resource.fieldName();
    resources.put(resource.fieldName(), resource);
  }

  public @Nullable TransformerDeclaration transformer(@NotNull String name) { return transformers.get(name); }

  public void addTransformer(@NotNull TransformerDeclaration transformer) {
    assert !transformers.containsKey(transformer.name()) : transformer.name();
    transformers.put(transformer.name(), transformer);
  }
}
