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

import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.service.Resource;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.types.DataType;
import ws.epigraph.url.ReadRequestUrl;
import ws.epigraph.url.ReadRequestUrlPsiParser;
import ws.epigraph.url.parser.psi.UrlReadUrl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadOperationRouter {
  @NotNull
  public static OperationSearchResult<ReadOperation<?>> findReadOperation(
      @Nullable String operationName,
      @NotNull UrlReadUrl urlPsi,
      @NotNull Resource resource,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    final @NotNull DataType resourceFieldType = resource.declaration().fieldType();

    if (operationName != null) {
      @Nullable final ReadOperation<?> operation = resource.namedReadOperation(operationName);
      return matchReadOperation(operation, resourceFieldType, urlPsi, resolver);
    } else {
      final Map<ReadOperation<?>, List<PsiProcessingError>> matchingErrors = new HashMap<>();

      for (final ReadOperation<?> operation : resource.unnamedReadOperations()) {
        @NotNull OperationSearchResult<ReadOperation<?>> matchingResult =
            matchReadOperation(operation, resourceFieldType, urlPsi, resolver);

        if (matchingResult instanceof OperationSearchSuccess)
          return matchingResult;

        if (matchingResult instanceof OperationSearchFailure) {
          matchingErrors.put(
              operation,
              ((OperationSearchFailure<ReadOperation<?>>) matchingResult).errors().get(operation)
          );
        }

      }

      if (matchingErrors.isEmpty())
        return OperationNotFound.instance();
      else
        return new OperationSearchFailure<>(matchingErrors);
    }
  }

  @NotNull
  private static OperationSearchResult<ReadOperation<?>> matchReadOperation(
      final @Nullable ReadOperation<?> operation,
      final @NotNull DataType resourceFieldType,
      final @NotNull UrlReadUrl urlPsi,
      final @NotNull TypesResolver resolver) {

    if (operation == null)
      return OperationNotFound.instance();
    else {
      List<PsiProcessingError> operationErrors = new ArrayList<>();

      ReadRequestUrl readRequestUrl = null;

      try {
        readRequestUrl = ReadRequestUrlPsiParser.parseReadRequestUrl(
            resourceFieldType,
            operation.declaration(),
            urlPsi,
            resolver,
            operationErrors
        );
      } catch (PsiProcessingException e) {
        operationErrors = e.errors();
      }

      if (operationErrors.isEmpty()) {
        assert readRequestUrl != null;
        return new OperationSearchSuccess<>(
            operation, readRequestUrl.parameters(),
            readRequestUrl.path(),
            readRequestUrl.outputProjection()
        );
      } else
        return new OperationSearchFailure<>(
            Collections.singletonMap(operation, operationErrors)
        );

    }
  }

}
