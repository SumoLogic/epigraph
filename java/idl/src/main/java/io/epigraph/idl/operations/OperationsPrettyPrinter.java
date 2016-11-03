package io.epigraph.idl.operations;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.delete.OpDeleteProjectionsPrettyPrinter;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.projections.op.input.OpInputProjectionsPrettyPrinter;
import io.epigraph.projections.op.output.OpOutputProjectionsPrettyPrinter;
import io.epigraph.projections.op.path.OpFieldPath;
import io.epigraph.projections.op.path.OpPathPrettyPrinter;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("ConstantConditions")
public class OperationsPrettyPrinter<E extends Exception> {
  @NotNull
  private final Layouter<E> l;
  private final OpPathPrettyPrinter<E> opPathPrinter;
  private final OpOutputProjectionsPrettyPrinter<E> opOutputPrinter;
  private final OpInputProjectionsPrettyPrinter<E> opInputPrinter;
  private final OpDeleteProjectionsPrettyPrinter<E> opDeletePrinter;

  public OperationsPrettyPrinter(@NotNull Layouter<E> l) {
    this.l = l;
    opPathPrinter = new OpPathPrettyPrinter<>(l);
    opOutputPrinter = new OpOutputProjectionsPrettyPrinter<>(l);
    opInputPrinter = new OpInputProjectionsPrettyPrinter<>(l);
    opDeletePrinter = new OpDeleteProjectionsPrettyPrinter<>(l);
  }

  public void printOperation(@NotNull OperationIdl operation) throws E {
    l.beginIInd(0);

    if (!operation.isDefault())
      l.print(operation.name()).brk();

    l.print(operation.kind().toString());
    l.brk().print("{");
    l.beginCInd();

    boolean first = true;

    first = printMethod(operation, first);

    @Nullable Annotations annotations = operation.annotations();
    if (annotations != null) first = opOutputPrinter.print(annotations, true, first);

    first = printPath(operation, first);

    first = printInputType(operation, first);
    first = printInputProjection(operation, first);

    first = printDeleteProjection(operation, first);

    first = printOutputType(operation, first);
    printOutputProjection(operation, first);

    l.brk(1, -l.getDefaultIndentation()).end().print("}");
    l.end();
  }

  private boolean printMethod(@NotNull OperationIdl operation, boolean first) throws E {
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

  private boolean printPath(@NotNull OperationIdl operation, boolean first) throws E {
    final @Nullable OpFieldPath path = operation.path();

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

  private boolean printInputType(@NotNull OperationIdl operation, boolean first) throws E {
    final @Nullable DatumType inputType = operation.inputType();

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

  private boolean printInputProjection(@NotNull OperationIdl operation, boolean first) throws E {
    OpInputModelProjection<?, ?, ?> projection = operation.inputProjection();

    if (projection != null) {
      if (first) first = false;
      else l.print(",");

      l.brk();

      l.beginIInd();
      l.print("inputProjection").brk();
      opInputPrinter.print(projection, 0);
      l.end();
    }

    return first;
  }

  private boolean printOutputType(@NotNull OperationIdl operation, boolean first) throws E {
    final @NotNull Type outputType = operation.outputType();

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

  private boolean printOutputProjection(@NotNull OperationIdl operation, boolean first) throws E {
    if (!opOutputPrinter.isPrintoutEmpty(operation.outputProjection())) {

      if (first) first = false;
      else l.print(",");

      l.brk();

      opOutputPrinter.print("outputProjection", operation.outputProjection());

    }

    return first;
  }

  private boolean printDeleteProjection(@NotNull OperationIdl operation, boolean first) throws E {
    if (operation instanceof DeleteOperationIdl) {
      DeleteOperationIdl deleteOperation = (DeleteOperationIdl) operation;

      if (first) first = false;
      else l.print(",");

      l.brk();

      opDeletePrinter.print("deleteProjection", deleteOperation.deleteProjection());
    }

    return first;
  }

}
