package io.epigraph.projections.op.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenListModelProjection;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.ListType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPathListModelProjection
    extends OpPathModelProjection<OpPathListModelProjection, ListType>
    implements GenListModelProjection<
    OpPathVarProjection,
    OpPathTagProjectionEntry,
    OpPathModelProjection<?, ?>,
    OpPathListModelProjection,
    ListType
    > {

  @NotNull
  private OpPathVarProjection itemsProjection;

  public OpPathListModelProjection(
      @NotNull ListType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpPathListModelProjection metaProjection,
      @NotNull OpPathVarProjection itemsProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, location);
    this.itemsProjection = itemsProjection;
  }

  @NotNull
  public OpPathVarProjection itemsProjection() { return itemsProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpPathListModelProjection that = (OpPathListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
