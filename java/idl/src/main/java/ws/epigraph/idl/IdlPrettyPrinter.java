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

import de.uka.ilkd.pp.Layouter;
import ws.epigraph.idl.operations.OperationIdl;
import ws.epigraph.idl.operations.OperationsPrettyPrinter;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlPrettyPrinter<E extends Exception> {
  @NotNull
  private final Layouter<E> l;

  private final OperationsPrettyPrinter<E> operationsPrettyPrinter;

  public IdlPrettyPrinter(@NotNull Layouter<E> l) {
    this.l = l;
    operationsPrettyPrinter = new OperationsPrettyPrinter<>(l);
  }

  public void print(@NotNull Idl idl) throws E {
    // should we try to preserve imports?

    l.beginCInd(0);
    l.print("namespace ").print(idl.namespace().toString());
    if (!idl.resources().isEmpty()) l.nl();

    for (ResourceIdl resource : idl.resources().values())
      printResource(resource);

    l.end();
  }

  public void printResource(@NotNull ResourceIdl resource) throws E {
    l.beginIInd(0);
    l.print("resource").brk();
    l.print(resource.fieldName()).print(":").brk();

    @NotNull DataType fieldType = resource.fieldType();
    l.print(fieldType.type.name().toString()).brk();

    @Nullable Type.Tag defaultTag = fieldType.defaultTag;
    if (defaultTag != null && !(fieldType.type instanceof DatumType)) {
      l.print("default").brk();
      l.print(defaultTag.name()).brk();
    }

    l.print("{");
    l.beginIInd();

    for (OperationIdl operation : resource.operations()) {
      l.brk();
      operationsPrettyPrinter.printOperation(operation);
    }

    l.end();
    l.brk(1, -l.getDefaultIndentation()).print("}");
    l.end();
  }
}
