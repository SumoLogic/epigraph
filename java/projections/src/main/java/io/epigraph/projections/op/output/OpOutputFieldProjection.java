package io.epigraph.projections.op.output;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.types.RecordType;
import io.epigraph.util.pp.DataPrettyPrinter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputFieldProjection implements PrettyPrintable {
  @NotNull
  private final RecordType.Field field;
  @Nullable
  private final Set<OpParam> params;
  @NotNull
  private final OpOutputVarProjection projection;
  private final boolean includeInDefault;

  public OpOutputFieldProjection(@NotNull RecordType.Field field,
                                 @Nullable Set<OpParam> params,
                                 @NotNull OpOutputVarProjection projection,
                                 boolean includeInDefault) {
    this.field = field;
    this.params = params;
    this.projection = projection;
    this.includeInDefault = includeInDefault;
  }

  @NotNull
  public RecordType.Field getField() { return field; }

  @Nullable
  public Set<OpParam> params() { return params; }

  @NotNull
  public OpOutputVarProjection projection() { return projection; }

  public boolean includeInDefault() { return includeInDefault; }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    if (includeInDefault) l.print("+");
    l.print(field.name());
    if (params != null && !params.isEmpty()) {
      l.beginCInd().print(" {");

      l.brk().beginCInd().print("params: {");
      for (OpParam param : params) l.brk().print(param);
      l.end().brk().print("}");

      l.end().brk().print("}");
    }
    l.print(":").brk().beginCInd().ind().print(projection).end();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpOutputFieldProjection that = (OpOutputFieldProjection) o;
    return includeInDefault == that.includeInDefault &&
        Objects.equals(field, that.field) &&
        Objects.equals(params, that.params) &&
        Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(field, params, projection, includeInDefault); }

  @Override
  public String toString() { return DataPrettyPrinter.prettyPrint(this); }
}
