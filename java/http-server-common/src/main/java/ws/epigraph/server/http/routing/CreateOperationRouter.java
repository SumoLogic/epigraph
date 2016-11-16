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
import ws.epigraph.idl.operations.CreateOperationIdl;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.service.Resource;
import ws.epigraph.service.operations.CreateOperation;
import ws.epigraph.types.DataType;
import ws.epigraph.url.CreateRequestUrl;
import ws.epigraph.url.parser.CreateRequestUrlPsiParser;
import ws.epigraph.url.parser.psi.UrlCreateUrl;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CreateOperationRouter
    extends AbstractOperationRouter<UrlCreateUrl, CreateOperationIdl, CreateOperation<?>, CreateRequestUrl> {

  @Nullable
  @Override
  protected CreateOperation<?> namedOperation(@NotNull final String name, @NotNull final Resource resource) {
    return resource.namedCreateOperation(name);
  }

  @NotNull
  @Override
  protected Collection<? extends CreateOperation<?>> unnamedOperations(@NotNull final Resource resource) {
    return resource.unnamedCreateOperations();
  }

  @NotNull
  @Override
  protected CreateRequestUrl parseUrl(
      @NotNull final DataType resourceType,
      @NotNull final CreateOperationIdl opDecl,
      @NotNull final UrlCreateUrl urlPsi,
      @NotNull final TypesResolver resolver,
      @NotNull final List<PsiProcessingError> errors) throws PsiProcessingException {
    return CreateRequestUrlPsiParser.parseCreateRequestUrl(
        resourceType,
        opDecl,
        urlPsi,
        resolver,
        errors
    );
  }

}
