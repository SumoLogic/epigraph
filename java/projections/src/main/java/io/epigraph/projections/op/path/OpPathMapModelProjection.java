package io.epigraph.projections.op.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenMapModelProjection;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPathMapModelProjection
    extends OpPathModelProjection<OpPathMapModelProjection, MapType>
    implements GenMapModelProjection<
    OpPathVarProjection,
    OpPathTagProjectionEntry,
    OpPathModelProjection<?, ?>,
    OpPathMapModelProjection,
    MapType
    > {

  @NotNull
  private final OpPathVarProjection itemsProjection;
  @NotNull
  private final OpPathKeyProjection keyProjection;

  public OpPathMapModelProjection(
      @NotNull MapType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpPathMapModelProjection metaProjection,
      @NotNull OpPathKeyProjection keyProjection,
      @NotNull OpPathVarProjection valuesProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, location);
    this.itemsProjection = valuesProjection;
    this.keyProjection = keyProjection;
  }

  @NotNull
  public OpPathVarProjection itemsProjection() { return itemsProjection; }

  @NotNull
  public OpPathKeyProjection keyProjection() { return keyProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpPathMapModelProjection that = (OpPathMapModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection) &&
           Objects.equals(keyProjection, that.keyProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection, keyProjection);
  }
}
