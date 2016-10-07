package io.epigraph.idl;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.idl.operations.OperationIdl;
import io.epigraph.idl.operations.OperationsPrettyPrinter;
import io.epigraph.types.DataType;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
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
