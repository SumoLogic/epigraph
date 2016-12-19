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
import ws.epigraph.service.operations.ReadOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ResourceRouter { // todo move to service?
  public static Resource findResource(@NotNull String resourceName, @NotNull Service service)
      throws ResourceNotFoundException {

    Resource resource = service.resources().get(resourceName);
    if (resource == null) throw new ResourceNotFoundException(resourceName);
    return resource;
  }

  @Deprecated
  public static ReadOperation<?> findReadOperation(@Nullable String operationName, @NotNull Resource resource)
      throws ResourceNotFoundException, OperationNotFoundException {

    ReadOperation operation = operationName == null
                              ? resource.unnamedReadOperations().iterator().next()
                              : resource.namedReadOperation(operationName);

    if (operation == null)
      throw new OperationNotFoundException(resource.declaration().fieldName(), OperationKind.READ, operationName);

    return operation;
  }
}
