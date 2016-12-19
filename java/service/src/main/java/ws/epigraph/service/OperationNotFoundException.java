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

package ws.epigraph.service;

import ws.epigraph.schema.operations.OperationKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OperationNotFoundException extends Exception {
  @NotNull
  private final String resourceName;
  @NotNull
  private final OperationKind operationKind;
  @Nullable
  private final String operationName;

  public OperationNotFoundException(@NotNull String resourceName,
                                    @NotNull OperationKind operationKind,
                                    @Nullable String operationName) {

    super(
        String.format(
            "%s operation '%s' in resource '%s' not found",
            operationKind, operationName == null ? "<default>" : operationName, resourceName
        )
    );

    this.resourceName = resourceName;
    this.operationKind = operationKind;
    this.operationName = operationName;
  }

  @NotNull
  public String resourceName() { return resourceName; }

  @NotNull
  public OperationKind operationType() { return operationKind; }

  @Nullable
  public String operationName() { return operationName; }
}
