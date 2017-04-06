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

package ws.epigraph.schema;

import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.types.DataTypeApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ResourceDeclaration {
  private final @NotNull String fieldName;
  private final @NotNull DataTypeApi fieldType;
  private final @NotNull List<OperationDeclaration> operations;
  private final @NotNull TextLocation location;

  public ResourceDeclaration(
      @NotNull String fieldName,
      @NotNull DataTypeApi fieldType,
      @NotNull List<OperationDeclaration> operations,
      @NotNull TextLocation location) {
    this.fieldName = fieldName;
    this.fieldType = fieldType;
    this.operations = operations;
    this.location = location;
  }

  public @NotNull String fieldName() { return fieldName; }

  public @NotNull DataTypeApi fieldType() { return fieldType; }

  public @NotNull List<OperationDeclaration> operations() { return operations; }

  public @NotNull TextLocation location() { return location; }

  public void validate(@NotNull List<ResourceDeclarationError> errors) {
    verifyNameClashes(errors);
    for (final OperationDeclaration operation : operations) {
      operation.validate(this, errors);
    }
  }

  @Override
  public String toString() { return "resource /" + fieldName; }

  private void verifyNameClashes(@NotNull List<ResourceDeclarationError> errors) {
    Map<String, OperationDeclaration> operationsByName = new HashMap<>();

    for (final OperationDeclaration operation : operations) {
      OperationDeclaration clashesWith = operationsByName.get(operation.nameOrDefaultName());

      if (clashesWith == null)
        operationsByName.put(operation.nameOrDefaultName(), operation);
      else {
        String name = operation.isDefault()
                      ? String.format("default %s", operation.kind().toString().toLowerCase())
                      : String.format("'%s'", operation.name());

        errors.add(
            new ResourceDeclarationError(
                this,
                operation,
                name + " operation is already defined at " + clashesWith.location(),
                operation.location()
            )
        );

      }
    }
  }
}
