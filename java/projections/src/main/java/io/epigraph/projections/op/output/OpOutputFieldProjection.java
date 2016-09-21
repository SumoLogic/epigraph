package io.epigraph.projections.op.output;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.projections.op.OpCustomParams;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.RecordType;
import io.epigraph.util.pp.DataPrettyPrinter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputFieldProjection implements PrettyPrintable {
  @NotNull
  private final RecordType.Field field;
  @Nullable
  private final OpParams params;
  @Nullable
  private final OpCustomParams customParams;
  @NotNull
  private final OpOutputVarProjection projection;
  private final boolean includeInDefault;

  public OpOutputFieldProjection(@NotNull RecordType.Field field,
                                 @Nullable OpParams params,
                                 @Nullable OpCustomParams customParams,
                                 @NotNull OpOutputVarProjection projection,
                                 boolean includeInDefault) {
    this.field = field;
    this.params = params;
    this.customParams = customParams;
    this.projection = projection;
    this.includeInDefault = includeInDefault;
  }

  @NotNull
  public RecordType.Field field() { return field; }

  public @Nullable OpParams params() { return params; }

  @Nullable
  public OpCustomParams customParams() { return customParams; }

  @NotNull
  public OpOutputVarProjection projection() { return projection; }

  public boolean includeInDefault() { return includeInDefault; }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    if (includeInDefault) l.print("+");
    l.print(field.name());
    if ((params != null && !params.isEmpty()) || (customParams != null && !customParams.isEmpty())) {
      l.beginIInd().print(" {");
      if (params != null) l.nl().print(params);
      if (customParams != null) l.nl().print(customParams);
      l.end().nl().print("}");
    }
    l.print(":").brk().beginIInd().ind().print(projection).end();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpOutputFieldProjection that = (OpOutputFieldProjection) o;
    return includeInDefault == that.includeInDefault &&
           Objects.equals(field, that.field) &&
           Objects.equals(params, that.params) &&
           Objects.equals(customParams, that.customParams) &&
           Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(field, params, customParams, projection, includeInDefault); }

  @Override
  public String toString() { return DataPrettyPrinter.prettyPrint(this); }
}
