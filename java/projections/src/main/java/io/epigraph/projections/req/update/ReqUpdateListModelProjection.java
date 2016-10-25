package io.epigraph.projections.req.update;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenListModelProjection;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.ListType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateListModelProjection
    extends ReqUpdateModelProjection<ReqUpdateListModelProjection, ListType>
    implements GenListModelProjection<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?>,
    ReqUpdateListModelProjection,
    ListType
    > {
  @NotNull
  private ReqUpdateVarProjection itemsProjection;

  public ReqUpdateListModelProjection(
      @NotNull ListType model,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull ReqUpdateVarProjection itemsProjection,
      @NotNull TextLocation location) {
    super(model, update, params, annotations, location);
    this.itemsProjection = itemsProjection;
  }

  @NotNull
  public ReqUpdateVarProjection itemsProjection() { return itemsProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqUpdateListModelProjection that = (ReqUpdateListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
