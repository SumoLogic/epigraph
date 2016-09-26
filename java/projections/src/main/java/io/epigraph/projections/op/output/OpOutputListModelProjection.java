package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.op.OpCustomParams;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.ListType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputListModelProjection extends OpOutputModelProjection<ListType> {
  @NotNull
  private OpOutputVarProjection itemsProjection;

  public OpOutputListModelProjection(@NotNull ListType model,
                                     boolean includeInDefault,
                                     @Nullable OpParams params,
                                     @Nullable OpCustomParams customParams,
                                     @Nullable OpOutputModelProjection<?> metaProjection,
                                     @NotNull OpOutputVarProjection itemsProjection,
                                     @NotNull TextLocation location) {
    super(model, includeInDefault, params, customParams, metaProjection, location);
    this.itemsProjection = itemsProjection;
  }

  @NotNull
  public OpOutputVarProjection itemsProjection() { return itemsProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpOutputListModelProjection that = (OpOutputListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
