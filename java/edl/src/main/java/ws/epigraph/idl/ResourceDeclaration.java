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

import ws.epigraph.idl.operations.OperationDeclaration;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.DataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ResourceDeclaration {
  private final @NotNull String fieldName;
  private final @NotNull DataType fieldType;
  private final @NotNull List<OperationDeclaration> operations;
  private final @NotNull TextLocation location;

  public ResourceDeclaration(@NotNull String fieldName,
                             @NotNull DataType fieldType,
                             @NotNull List<OperationDeclaration> operations,
                             @NotNull TextLocation location) {
    this.fieldName = fieldName;
    this.fieldType = fieldType;
    this.operations = operations;
    this.location = location;
  }

  public @NotNull String fieldName() { return fieldName; }

  public @NotNull DataType fieldType() { return fieldType; }

  public @NotNull List<OperationDeclaration> operations() { return operations; }

  public @NotNull TextLocation location() { return location; }

  @Override
  public String toString() { return "resource /" + fieldName; }
}
