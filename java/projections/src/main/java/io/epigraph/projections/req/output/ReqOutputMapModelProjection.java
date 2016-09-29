package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.CustomParams;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputMapModelProjection extends ReqOutputModelProjection<MapType> {
  @Nullable
  private final List<ReqOutputKeyProjection> keys;
  private final boolean keysRequired;
  @NotNull
  private final ReqOutputVarProjection valuesProjection;

  public ReqOutputMapModelProjection(@NotNull MapType model,
                                     boolean required,
                                     @Nullable ReqParams params,
                                     @Nullable CustomParams customParams,
                                     @Nullable ReqOutputModelProjection<?> metaProjection,
                                     @Nullable List<ReqOutputKeyProjection> keys,
                                     boolean keysRequired,
                                     @NotNull ReqOutputVarProjection valuesProjection,
                                     @NotNull TextLocation location) {
    super(model, required, params, customParams, metaProjection, location);
    this.keys = keys;
    this.keysRequired = keysRequired;
    this.valuesProjection = valuesProjection;
  }

  @NotNull
  public ReqOutputVarProjection itemsProjection() { return valuesProjection; }

  @Nullable
  public List<ReqOutputKeyProjection> keys() { return keys; }

  public boolean keysRequired() { return keysRequired; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqOutputMapModelProjection that = (ReqOutputMapModelProjection) o;
    return keysRequired == that.keysRequired &&
           Objects.equals(keys, that.keys) &&
           Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), keys, keysRequired, valuesProjection); }
}
