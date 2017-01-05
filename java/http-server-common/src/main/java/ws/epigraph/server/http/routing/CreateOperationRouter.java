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
import ws.epigraph.schema.operations.CreateOperationDeclaration;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.service.Resource;
import ws.epigraph.service.operations.CreateOperation;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.CreateRequestUrl;
import ws.epigraph.url.parser.CreateRequestUrlPsiParser;
import ws.epigraph.url.parser.psi.UrlCreateUrl;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class CreateOperationRouter
    extends AbstractOperationRouter<UrlCreateUrl, CreateOperationDeclaration, CreateOperation<?>, CreateRequestUrl> {

  public static final CreateOperationRouter INSTANCE = new CreateOperationRouter();

  private CreateOperationRouter() {}

  @Override
  protected @Nullable CreateOperation<?> namedOperation(final @NotNull String name, final @NotNull Resource resource) {
    return resource.namedCreateOperation(name);
  }

  @Override
  protected @NotNull Collection<? extends CreateOperation<?>> unnamedOperations(final @NotNull Resource resource) {
    return resource.unnamedCreateOperations();
  }

  @Override
  protected @NotNull CreateRequestUrl parseUrl(
      final @NotNull DataTypeApi resourceType,
      final @NotNull CreateOperationDeclaration opDecl,
      final @NotNull UrlCreateUrl urlPsi,
      final @NotNull TypesResolver resolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    return CreateRequestUrlPsiParser.parseCreateRequestUrl(
        resourceType,
        opDecl,
        urlPsi,
        resolver,
        errors
    );
  }

}
