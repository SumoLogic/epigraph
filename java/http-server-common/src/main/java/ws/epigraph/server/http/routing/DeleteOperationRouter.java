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
import ws.epigraph.idl.operations.DeleteOperationIdl;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.service.Resource;
import ws.epigraph.service.operations.DeleteOperation;
import ws.epigraph.types.DataType;
import ws.epigraph.url.DeleteRequestUrl;
import ws.epigraph.url.parser.DeleteRequestUrlPsiParser;
import ws.epigraph.url.parser.psi.UrlDeleteUrl;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DeleteOperationRouter
    extends AbstractOperationRouter<UrlDeleteUrl, DeleteOperationIdl, DeleteOperation<?>, DeleteRequestUrl> {

  @Nullable
  @Override
  protected DeleteOperation<?> namedOperation(@NotNull final String name, @NotNull final Resource resource) {
    return resource.namedDeleteOperation(name);
  }

  @NotNull
  @Override
  protected Collection<? extends DeleteOperation<?>> unnamedOperations(@NotNull final Resource resource) {
    return resource.unnamedDeleteOperations();
  }

  @NotNull
  @Override
  protected DeleteRequestUrl parseUrl(
      @NotNull final DataType resourceType,
      @NotNull final DeleteOperationIdl opDecl,
      @NotNull final UrlDeleteUrl urlPsi,
      @NotNull final TypesResolver resolver,
      @NotNull final List<PsiProcessingError> errors) throws PsiProcessingException {
    return DeleteRequestUrlPsiParser.parseDeleteRequestUrl(
        resourceType,
        opDecl,
        urlPsi,
        resolver,
        errors
    );
  }

}
