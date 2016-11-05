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

package ws.epigraph.idl;

import ws.epigraph.idl.operations.OperationIdl;
import ws.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlError {
  @Nullable
  private final ResourceIdl resource;
  @Nullable
  private final OperationIdl operation;
  @NotNull
  private final String message;
  @NotNull
  private final TextLocation location;

  public IdlError(
      @Nullable ResourceIdl resource,
      @Nullable OperationIdl operation,
      @NotNull String message,
      @NotNull TextLocation location) {

    this.resource = resource;
    this.operation = operation;
    this.message = message;
    this.location = location;
  }

  @Nullable
  public ResourceIdl resource() { return resource; }

  @Nullable
  public OperationIdl operation() { return operation; }

  @NotNull
  public String message() { return message; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IdlError idlError = (IdlError) o;
    return Objects.equals(resource, idlError.resource) &&
           Objects.equals(operation, idlError.operation) &&
           Objects.equals(message, idlError.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resource, operation, message);
  }
}
