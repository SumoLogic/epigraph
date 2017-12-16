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

package ws.epigraph.server.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.invocation.InvocationErrorImpl;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.schema.operations.OperationKind;
import ws.epigraph.util.HttpStatusCode;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class NotFoundError extends InvocationErrorImpl {
  // todo rename to OperationNotFoundInvocationError ?
  public NotFoundError(
      @NotNull String resourceName,
      @NotNull OperationKind operationKind,
      @Nullable String operationName) {

    this(resourceName, operationKind, null, operationName);
  }

  public NotFoundError(
      @NotNull String resourceName,
      @NotNull OperationKind operationKind,
      @Nullable HttpMethod method,
      @Nullable String operationName) {

    super(
        HttpStatusCode.BAD_REQUEST, String.format(
            "%s operation '%s'%s in resource '%s' not found",
            operationKind,
            operationName == null ? "<default>" : operationName,
            method == null ? "" : " for HTTP method '" + method + "'",
            resourceName
        )
    );
  }
}
