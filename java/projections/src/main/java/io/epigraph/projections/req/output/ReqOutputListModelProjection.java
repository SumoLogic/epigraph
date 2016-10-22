package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenListModelProjection;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.ListType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputListModelProjection
    extends ReqOutputModelProjection<ReqOutputListModelProjection, ListType>
    implements GenListModelProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?>,
    ReqOutputListModelProjection,
    ListType
    > {
  @NotNull
  private ReqOutputVarProjection itemsProjection;

  public ReqOutputListModelProjection(
      @NotNull ListType model,
      boolean required,
      @Nullable ReqParams params,
      @Nullable Annotations annotations,
      @Nullable ReqOutputListModelProjection metaProjection,
      @NotNull ReqOutputVarProjection itemsProjection,
      @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, location);
    this.itemsProjection = itemsProjection;
  }

  @NotNull
  public ReqOutputVarProjection itemsProjection() { return itemsProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqOutputListModelProjection that = (ReqOutputListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
