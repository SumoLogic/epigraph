package io.epigraph.projections.req.delete;

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
public class ReqDeleteListModelProjection
    extends ReqDeleteModelProjection<ReqDeleteListModelProjection, ListType>
    implements GenListModelProjection<
    ReqDeleteVarProjection,
    ReqDeleteTagProjectionEntry,
    ReqDeleteModelProjection<?, ?>,
    ReqDeleteListModelProjection,
    ListType
    > {
  @NotNull
  private ReqDeleteVarProjection itemsProjection;

  public ReqDeleteListModelProjection(
      @NotNull ListType model,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull ReqDeleteVarProjection itemsProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);
    this.itemsProjection = itemsProjection;
  }

  @NotNull
  public ReqDeleteVarProjection itemsProjection() { return itemsProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqDeleteListModelProjection that = (ReqDeleteListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
