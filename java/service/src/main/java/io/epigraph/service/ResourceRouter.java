package io.epigraph.service;

import io.epigraph.idl.operations.OperationKind;
import io.epigraph.service.operations.ReadOperation;
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
