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
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.DeleteOperationDeclaration;
import ws.epigraph.service.Resource;
import ws.epigraph.service.operations.DeleteOperation;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.DeleteRequestUrl;
import ws.epigraph.url.parser.DeleteRequestUrlPsiParser;
import ws.epigraph.url.parser.psi.UrlDeleteUrl;

import java.util.Collection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class DeleteOperationRouter
    extends AbstractOperationRouter<UrlDeleteUrl, DeleteOperationDeclaration, DeleteOperation<?>, DeleteRequestUrl> {

  public static final DeleteOperationRouter INSTANCE = new DeleteOperationRouter();

  private DeleteOperationRouter() {}

  @Override
  protected @Nullable DeleteOperation<?> namedOperation(final @NotNull String name, final @NotNull Resource resource) {
    return resource.namedDeleteOperation(name);
  }

  @Override
  protected @NotNull Collection<? extends DeleteOperation<?>> unnamedOperations(final @NotNull Resource resource) {
    return resource.unnamedDeleteOperations();
  }

  @Override
  protected @NotNull DeleteRequestUrl parseUrl(
      final @NotNull DataTypeApi resourceType,
      final @NotNull DeleteOperationDeclaration opDecl,
      final @NotNull UrlDeleteUrl urlPsi,
      final @NotNull TypesResolver resolver,
      final @NotNull PsiProcessingContext context) throws PsiProcessingException {
    return DeleteRequestUrlPsiParser.parseDeleteRequestUrl(
        resourceType,
        opDecl,
        urlPsi,
        resolver,
        context
    );
  }

}
