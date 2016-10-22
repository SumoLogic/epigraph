package io.epigraph.projections.op.input;

import io.epigraph.data.MapDatum;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenMapModelProjection;
import io.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputMapModelProjection
    extends OpInputModelProjection<OpInputMapModelProjection, MapType, MapDatum>
    implements GenMapModelProjection<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?>,
    OpInputMapModelProjection,
    MapType
    > {
  @NotNull
  private final OpInputVarProjection valuesProjection;

  public OpInputMapModelProjection(
      @NotNull MapType model,
      boolean required,
      @Nullable MapDatum defaultValue,
      @Nullable Annotations annotations,
      @Nullable OpInputMapModelProjection metaProjection,
      @NotNull OpInputVarProjection valuesProjection,
      @NotNull TextLocation location) {
    super(model, required, defaultValue, annotations, metaProjection, location);
    this.valuesProjection = valuesProjection;
  }

  @NotNull
  public OpInputVarProjection itemsProjection() { return valuesProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputMapModelProjection that = (OpInputMapModelProjection) o;
    return Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), valuesProjection);
  }
}
