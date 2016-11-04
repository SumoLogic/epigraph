package ws.epigraph.projections.req.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputMapModelProjection
    extends ReqOutputModelProjection<ReqOutputMapModelProjection, MapType>
    implements GenMapModelProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?>,
    ReqOutputMapModelProjection,
    MapType
    > {

  @Nullable
  private final List<ReqOutputKeyProjection> keys;
  @NotNull
  private final ReqOutputVarProjection valuesProjection;

  public ReqOutputMapModelProjection(
      @NotNull MapType model,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputMapModelProjection metaProjection,
      @Nullable List<ReqOutputKeyProjection> keys,
      @NotNull ReqOutputVarProjection valuesProjection,
      @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, location);
    this.keys = keys;
    this.valuesProjection = valuesProjection;
  }

  @NotNull
  public ReqOutputVarProjection itemsProjection() { return valuesProjection; }

  @Nullable
  public List<ReqOutputKeyProjection> keys() { return keys; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqOutputMapModelProjection that = (ReqOutputMapModelProjection) o;
    return Objects.equals(keys, that.keys) &&
           Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), keys, valuesProjection); }
}
