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
public final class ReadOperationRouter
    extends AbstractOperationRouter<UrlReadUrl, ReadOperationIdl, ReadOperation<?>, ReadRequestUrl> {

  public static final ReadOperationRouter INSTANCE = new ReadOperationRouter();

  private ReadOperationRouter() {}

  @Override

  protected @Nullable ReadOperation<?> namedOperation(final @NotNull String name, final @NotNull Resource resource) {
    return resource.namedReadOperation(name);
  }

  @Override
  protected @NotNull Collection<? extends ReadOperation<?>> unnamedOperations(final @NotNull Resource resource) {
    return resource.unnamedReadOperations();
  }

  @Override
  protected @NotNull ReadRequestUrl parseUrl(
      final @NotNull DataType resourceType,
      final @NotNull ReadOperationIdl opDecl,
      final @NotNull UrlReadUrl urlPsi,
      final @NotNull TypesResolver resolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    return ReadRequestUrlPsiParser.parseReadRequestUrl(
        resourceType,
        opDecl,
        urlPsi,
        resolver,
        errors
    );
  }

}
