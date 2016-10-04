package io.epigraph.idl.operations;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.input.OpInputProjectionsPrettyPrinter;
import io.epigraph.projections.op.input.OpInputVarProjection;
import io.epigraph.projections.op.output.OpOutputProjectionsPrettyPrinter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("ConstantConditions")
public class OperationsPrettyPrinter<E extends Exception> {
  @NotNull
  private final Layouter<E> l;
  private final OpOutputProjectionsPrettyPrinter<E> opOutputPrinter;
  private final OpInputProjectionsPrettyPrinter<E> opInputPrinter;

  public OperationsPrettyPrinter(@NotNull Layouter<E> l) {
    this.l = l;
    opOutputPrinter = new OpOutputProjectionsPrettyPrinter<>(l);
    opInputPrinter = new OpInputProjectionsPrettyPrinter<>(l);
  }

  public void printOperation(@NotNull Operation operation) throws E {
    l.beginIInd(0);

    if (!operation.isDefault())
      l.print(operation.name()).brk();

    l.print(operation.type().toString());
    l.brk().print("{");
    l.beginCInd();

    boolean first = true;

    @Nullable OpParams params = operation.params();
    if (params != null) first = opOutputPrinter.print(params, true, first);

    @Nullable Annotations annotations = operation.annotations();
    if (annotations != null) first = opOutputPrinter.print(annotations, true, first);

    first = printInput(operation, first);
    first = printOutput(operation, first);

    l.brk(1, -l.getDefaultIndentation()).end().print("}");
    l.end();
  }

  private boolean printInput(@NotNull Operation operation, boolean first) throws E {
    StepsAndProjection<OpInputVarProjection> projection = null;

    if (operation instanceof CreateOperation) projection = ((CreateOperation) operation).inputProjection();
    if (operation instanceof UpdateOperation) projection = ((UpdateOperation) operation).updateProjection();
    if (operation instanceof CustomOperation) projection = ((CustomOperation) operation).inputProjection();

    if (projection != null) {
      if (first) first = false;
      else l.print(",").brk();

      l.beginIInd();
      l.print("input").brk();
      opInputPrinter.print(projection.projection(), projection.pathSteps());
      l.end();
    }

    return first;
  }

  private boolean printOutput(@NotNull Operation operation, boolean first) throws E {
    if (first) first = false;
    else l.print(",").brk();

    l.beginIInd();
    l.print("output").brk();
    opOutputPrinter.print(operation.outputProjection(), 0);
    l.end();

    return first;
  }
}
