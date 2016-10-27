package io.epigraph.projections.op.delete;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenListModelProjection;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.ListType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteListModelProjection
    extends OpDeleteModelProjection<OpDeleteListModelProjection, ListType>
    implements GenListModelProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>,
    OpDeleteListModelProjection,
    ListType
    > {

  @NotNull
  private OpDeleteVarProjection itemsProjection;

  public OpDeleteListModelProjection(
      @NotNull ListType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpDeleteVarProjection itemsProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);
    this.itemsProjection = itemsProjection;
  }

  @NotNull
  public OpDeleteVarProjection itemsProjection() { return itemsProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpDeleteListModelProjection that = (OpDeleteListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
