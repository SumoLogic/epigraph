package io.epigraph.projections.op.delete;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenMapModelProjection;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteMapModelProjection
    extends OpDeleteModelProjection<OpDeleteMapModelProjection, MapType>
    implements GenMapModelProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>,
    OpDeleteMapModelProjection,
    MapType
    > {

  @NotNull
  private final OpDeleteVarProjection itemsProjection;
  @NotNull
  private final OpDeleteKeyProjection keyProjection;

  public OpDeleteMapModelProjection(
      @NotNull MapType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpDeleteKeyProjection keyProjection,
      @NotNull OpDeleteVarProjection valuesProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);
    this.itemsProjection = valuesProjection;
    this.keyProjection = keyProjection;
  }

  @NotNull
  public OpDeleteVarProjection itemsProjection() { return itemsProjection; }

  @NotNull
  public OpDeleteKeyProjection keyProjection() { return keyProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpDeleteMapModelProjection that = (OpDeleteMapModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection) &&
           Objects.equals(keyProjection, that.keyProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection, keyProjection);
  }
}
