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

import ws.epigraph.idl.operations.ReadOperationIdl;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.service.Resource;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.types.DataType;
import ws.epigraph.url.ReadRequestUrl;
import ws.epigraph.url.parser.ReadRequestUrlPsiParser;
import ws.epigraph.url.parser.psi.UrlReadUrl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadOperationRouter
    extends AbstractOperationRouter<UrlReadUrl, ReadOperationIdl, ReadOperation<?>, ReadRequestUrl> {

  @Nullable
  @Override
  protected ReadOperation<?> namedOperation(@NotNull final String name, @NotNull final Resource resource) {
    return resource.namedReadOperation(name);
  }

  @NotNull
  @Override
  protected Collection<? extends ReadOperation<?>> unnamedOperations(@NotNull final Resource resource) {
    return resource.unnamedReadOperations();
  }

  @NotNull
  @Override
  protected ReadRequestUrl parseUrl(
      @NotNull final DataType resourceType,
      @NotNull final ReadOperationIdl opDecl,
      @NotNull final UrlReadUrl urlPsi,
      @NotNull final TypesResolver resolver,
      @NotNull final List<PsiProcessingError> errors) throws PsiProcessingException {
    return ReadRequestUrlPsiParser.parseReadRequestUrl(
        resourceType,
        opDecl,
        urlPsi,
        resolver,
        errors
    );
  }

}
