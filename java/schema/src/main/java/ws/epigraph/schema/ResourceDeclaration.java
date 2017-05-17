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

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.OperationKind;
import ws.epigraph.types.DataTypeApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    Map<String, OperationDeclaration> customOperationsByName = new HashMap<>();
    Map<NameAndKind, OperationDeclaration> operationsByName = new HashMap<>();

    for (final OperationDeclaration operation : operations) {
      OperationKind kind = operation.kind();

      String name = operation.nameOrDefaultName();

      NameAndKind key = new NameAndKind(name, kind);
      OperationDeclaration clashesWith = operationsByName.get(key);

      if (clashesWith == null) {
        // check clashes with custom operations
        if (kind == OperationKind.CUSTOM) {
          customOperationsByName.put(name, operation);
          operationsByName.entrySet().stream().filter(e -> e.getKey().name.equals(name)).forEach(e ->
              addClashingError(
                  operation,
                  e.getValue().kind(),
                  e.getValue(),
                  errors
              )
          );
        } else {
          OperationDeclaration clashesWithCustom = customOperationsByName.get(name);
          if (clashesWithCustom != null)
            addClashingError(operation, OperationKind.CUSTOM, clashesWithCustom, errors);
        }

        operationsByName.put(key, operation);
      } else {
        addClashingError(operation, kind, clashesWith, errors);
      }
    }
  }

  private void addClashingError(
      final @NotNull OperationDeclaration operation,
      final @NotNull OperationKind kind,
      final @NotNull OperationDeclaration clashesWith,
      final @NotNull List<ResourceDeclarationError> errors) {

    String name = operation.isDefault()
                  ? "default"
                  : String.format("'%s'", operation.name());

    errors.add(
        new ResourceDeclarationError(
            this,
            operation,
            String.format(
                "%s %s operation is already defined at %s",
                name,
                kind.toString().toLowerCase(),
                clashesWith.location()
            ),
            operation.location()
        )
    );
  }

  private static final class NameAndKind {
    final @NotNull String name;
    final @NotNull OperationKind kind;

    private NameAndKind(final @NotNull String name, final @NotNull OperationKind kind) {
      this.name = name;
      this.kind = kind;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final NameAndKind kind1 = (NameAndKind) o;
      return Objects.equals(name, kind1.name) &&
             kind == kind1.kind;
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, kind);
    }
  }
}
