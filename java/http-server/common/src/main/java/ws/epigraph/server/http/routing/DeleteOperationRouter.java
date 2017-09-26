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

package ws.epigraph.server.http.routing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.schema.operations.DeleteOperationDeclaration;
import ws.epigraph.service.Resource;
import ws.epigraph.service.operations.DeleteOperation;
import ws.epigraph.url.NonReadRequestUrl;
import ws.epigraph.url.parser.DeleteRequestUrlPsiParser;

import java.util.Collection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class DeleteOperationRouter
    extends AbstractNonReadOperationRouter<DeleteOperationDeclaration, DeleteOperation<?>> {

  public static final DeleteOperationRouter INSTANCE = new DeleteOperationRouter();

  private DeleteOperationRouter() { super(DeleteRequestUrlPsiParser.INSTANCE); }

  @Override
  protected @Nullable DeleteOperation<?> namedOperation(final @Nullable String name, final @NotNull Resource resource) {
    return resource.namedDeleteOperation(DeleteOperationDeclaration.DEFAULT_NAME.equals(name) ? null : name);
  }

  @Override
  protected @NotNull Collection<? extends DeleteOperation<?>> operations(final @NotNull Resource resource) {
    return resource.deleteOperations();
  }

  @Override
  protected void validateMatchingRequest(
      final @NotNull NonReadRequestUrl request,
      final @NotNull PsiProcessingContext context) {

    if (request.inputProjection() == null)
      context.addError("Delete projection must be specified", TextLocation.UNKNOWN);

    super.validateMatchingRequest(request, context);
  }
}
