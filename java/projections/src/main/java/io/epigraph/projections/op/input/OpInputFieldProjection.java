package io.epigraph.projections.op.input;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.types.RecordType;
import io.epigraph.util.pp.DataPrettyPrinter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputFieldProjection implements PrettyPrintable {
  @NotNull
  private final RecordType.Field field;
  @NotNull
  private final OpInputVarProjection projection;
  private final boolean required;

  public OpInputFieldProjection(@NotNull RecordType.Field field,
                                @NotNull OpInputVarProjection projection,
                                boolean required) {
    this.field = field;
    this.projection = projection;
    this.required = required;
  }

  @NotNull
  public RecordType.Field getField() { return field; }

  @NotNull
  public OpInputVarProjection projection() { return projection; }

  public boolean required() { return required; }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    if (required) l.print("+");
    l.print(field.name());
    l.print(":").brk().beginCInd().ind().print(projection).end();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpInputFieldProjection that = (OpInputFieldProjection) o;
    return required == that.required &&
        Objects.equals(field, that.field) &&
        Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(field, projection, required); }

  @Override
  public String toString() { return DataPrettyPrinter.prettyPrint(this); }
}
