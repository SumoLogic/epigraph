package io.epigraph.idl;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.idl.operations.Operation;
import io.epigraph.idl.operations.OperationsPrettyPrinter;
import io.epigraph.types.DataType;
import io.epigraph.types.Type;
import io.epigraph.types.TypesResolver;
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

  public void print(@NotNull Idl idl, @NotNull TypesResolver resolver) throws E {
    // should we try to preserve imports?

    l.beginCInd(0);

    for (Resource resource : idl.resources())
      printResource(resource, resolver);

    l.end();
  }

  public void printResource(@NotNull Resource resource, @NotNull TypesResolver resolver) throws E {
    l.beginIInd();
    l.print("resource").brk();
    l.print(resource.fieldName()).brk();

    @NotNull DataType fieldType = resource.fieldType();
    l.print(fieldType.type.name().toString()).brk();

    Type.@Nullable Tag defaultTag = fieldType.defaultTag;
    if (defaultTag != null) l.print(defaultTag.name()).brk();

    l.print("{");
    l.beginIInd();

    for (Operation operation : resource.operations())
      operationsPrettyPrinter.printOperation(operation);

    l.brk(1, -l.getDefaultIndentation()).end().print("}");
    l.end();
  }
}
