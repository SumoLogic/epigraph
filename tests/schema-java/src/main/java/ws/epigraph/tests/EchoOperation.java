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

package ws.epigraph.tests;

import epigraph.Integer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.schema.operations.CustomOperationDeclaration;
import ws.epigraph.tests._resources.users.operations.custom.echo.AbstractCustomEchoOperation;
import ws.epigraph.tests._resources.users.operations.custom.echo.input.InputUsersFieldProjection;
import ws.epigraph.tests._resources.users.operations.custom.echo.output.OutputUsersFieldProjection;
import ws.epigraph.util.HttpStatusCode;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EchoOperation extends AbstractCustomEchoOperation {
  protected EchoOperation(final @NotNull CustomOperationDeclaration declaration) {
    super(declaration);
  }

  @Override
  protected @NotNull CompletableFuture<PersonMap.Data> process(
      final @NotNull PersonMap.Data.Builder resultBuilder,
      final @Nullable Integer inputData,
      final @Nullable InputUsersFieldProjection inputProjection,
      final @NotNull OutputUsersFieldProjection outputProjection) {

    if (inputProjection == null) {
      resultBuilder.set_Error(
          new ErrorValue(HttpStatusCode.BAD_REQUEST, "input projection must be specified")
      );
    } else {
      PersonMap parameter = inputProjection.dataProjection().getParamParameter();
      resultBuilder.set(parameter);
    }


    return CompletableFuture.completedFuture(resultBuilder);
  }
}
