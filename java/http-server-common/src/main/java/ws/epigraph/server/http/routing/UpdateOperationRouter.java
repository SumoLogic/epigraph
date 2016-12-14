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

package ws.epigraph.server.http.routing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.edl.operations.UpdateOperationDeclaration;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.service.Resource;
import ws.epigraph.service.operations.UpdateOperation;
import ws.epigraph.types.DataType;
import ws.epigraph.url.UpdateRequestUrl;
import ws.epigraph.url.parser.UpdateRequestUrlPsiParser;
import ws.epigraph.url.parser.psi.UrlUpdateUrl;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class UpdateOperationRouter
    extends AbstractOperationRouter<UrlUpdateUrl, UpdateOperationDeclaration, UpdateOperation<?>, UpdateRequestUrl> {

  public static final UpdateOperationRouter INSTANCE = new UpdateOperationRouter();

  private UpdateOperationRouter() {}

  @Override
  protected @Nullable UpdateOperation<?> namedOperation(final @NotNull String name, final @NotNull Resource resource) {
    return resource.namedUpdateOperation(name);
  }

  @Override
  protected @NotNull Collection<? extends UpdateOperation<?>> unnamedOperations(final @NotNull Resource resource) {
    return resource.unnamedUpdateOperations();
  }

  @Override
  protected @NotNull UpdateRequestUrl parseUrl(
      final @NotNull DataType resourceType,
      final @NotNull UpdateOperationDeclaration opDecl,
      final @NotNull UrlUpdateUrl urlPsi,
      final @NotNull TypesResolver resolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    return UpdateRequestUrlPsiParser.parseUpdateRequestUrl(
        resourceType,
        opDecl,
        urlPsi,
        resolver,
        errors
    );
  }

}
