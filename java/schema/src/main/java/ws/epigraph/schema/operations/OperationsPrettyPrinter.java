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

package ws.epigraph.schema.operations;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.op.*;
import ws.epigraph.types.TypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("ConstantConditions")
public class OperationsPrettyPrinter<E extends Exception> {
  private final @NotNull Layouter<E> l;
  private final OpPathPrettyPrinter<E> opPathPrinter;
  private OpProjectionsPrettyPrinter<E> opOutputPrinter;
  private OpProjectionsPrettyPrinter<E> opInputPrinter;
  private OpProjectionsPrettyPrinter<E> opDeletePrinter;

  public OperationsPrettyPrinter(@NotNull Layouter<E> l) {
    this.l = l;
    opPathPrinter = new OpPathPrettyPrinter<>(l);
  }

  public void printOperation(
      @NotNull OperationDeclaration operation,
      @NotNull ProjectionsPrettyPrinterContext<OpProjection<?, ?>> outputProjectionsPrinterContext,
      @NotNull ProjectionsPrettyPrinterContext<OpProjection<?, ?>> inputProjectionsPrinterContext,
      @NotNull ProjectionsPrettyPrinterContext<OpProjection<?, ?>> deleteProjectionsPrinterContext
  ) throws E {
    opOutputPrinter = new OpProjectionsPrettyPrinter<>(l, outputProjectionsPrinterContext);
    opInputPrinter = new OpProjectionsPrettyPrinter<>(l, inputProjectionsPrinterContext);
    opDeletePrinter = new OpProjectionsPrettyPrinter<>(l, deleteProjectionsPrinterContext);

    l.beginIInd(0);

    l.print(operation.kind().toString().toLowerCase()).brk();

    if (!operation.isDefault())
      l.print(operation.name()).brk();

    l.end();
    l.print("{");
    l.beginCInd();

    boolean first = true;

    first = printMethod(operation, first);

    @Nullable Annotations annotations = operation.annotations();
    if (annotations != null && !annotations.isEmpty()) {
      if (first) l.brk();
      first = opOutputPrinter.printAnnotations(annotations, true, first);
    }

    first = printPath(operation, first);

    if (operation.kind() == OperationKind.DELETE) {
      first = printDeleteProjection(operation, first);
    } else {
      first = printInputType(operation, first);
      first = printInputProjection(operation, first);
    }

    first = printOutputType(operation, first);
    printOutputProjection(operation, first);

    l.brk(1, -l.getDefaultIndentation()).end().print("}");
    //l.end();
  }

  private boolean printMethod(@NotNull OperationDeclaration operation, boolean first) throws E {
    if (operation.kind() != OperationKind.CUSTOM) return first;

    if (first) first = false;
    else l.print(",");

    l.brk();

    l.beginIInd();
    l.print("method").brk();
    l.print(operation.method().toString());
    l.end();

    return first;
  }

  private boolean printPath(@NotNull OperationDeclaration operation, boolean first) throws E {
    final @Nullable OpFieldProjection path = operation.path();

    if (path != null) {
      if (first) first = false;
      else l.print(",");

      l.brk();

      l.beginIInd();
      l.print("path").brk();
      opPathPrinter.print(path);
      l.end();
    }

    return first;
  }

  private boolean printInputType(@NotNull OperationDeclaration operation, boolean first) throws E {
    final @Nullable TypeApi inputType = operation.inputType();

    if (inputType != null) {
      if (first) first = false;
      else l.print(",");

      l.brk();

      l.beginIInd();
      l.print("inputType").brk();
      l.print(inputType.name().toString());
      l.end();
    }

    return first;
  }

  private boolean printInputProjection(@NotNull OperationDeclaration operation, boolean first) throws E {
    @Nullable OpFieldProjection projection = operation.inputProjection();

    if (projection != null) {
      if (first) first = false;
      else l.print(",");

      l.brk();

      l.beginIInd();
      l.print("inputProjection").brk();
      opInputPrinter.print(projection);
      l.end();
    }

    return first;
  }

  private boolean printOutputType(@NotNull OperationDeclaration operation, boolean first) throws E {
    final @NotNull TypeApi outputType = operation.outputType();

    if (outputType != null) {
      if (first) first = false;
      else l.print(",");

      l.brk();

      l.beginIInd();
      l.print("outputType").brk();
      l.print(outputType.name().toString());
      l.end();
    }

    return first;
  }

  private boolean printOutputProjection(@NotNull OperationDeclaration operation, boolean first) throws E {
    if (!opOutputPrinter.isPrintoutEmpty(operation.outputProjection())) {

      if (first) first = false;
      else l.print(",");

      l.brk();

      opOutputPrinter.printFieldProjection("outputProjection", operation.outputProjection());

    }

    return first;
  }

  private boolean printDeleteProjection(@NotNull OperationDeclaration operation, boolean first) throws E {
    if (operation instanceof DeleteOperationDeclaration) {
      DeleteOperationDeclaration deleteOperation = (DeleteOperationDeclaration) operation;

      if (first) first = false;
      else l.print(",");

      l.brk();

      final OpFieldProjection deleteFieldProjection = deleteOperation.deleteProjection();

      opDeletePrinter.printFieldProjection(
          "deleteProjection",
          deleteFieldProjection.projection().flag() ? "+" : "",
          deleteFieldProjection
      );
    }

    return first;
  }

}
