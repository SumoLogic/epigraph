package ws.epigraph.projections.req.update;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateMapModelProjection
    extends ReqUpdateModelProjection<ReqUpdateMapModelProjection, MapType>
    implements GenMapModelProjection<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?>,
    ReqUpdateMapModelProjection,
    MapType
    > {

  @NotNull
  private final ReqUpdateKeysProjection keys;
  @NotNull
  private final ReqUpdateVarProjection valuesProjection;

  public ReqUpdateMapModelProjection(
      @NotNull MapType model,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull ReqUpdateKeysProjection keys,
      @NotNull ReqUpdateVarProjection valuesProjection,
      @NotNull TextLocation location) {
    super(model, update, params, annotations, location);
    this.keys = keys;
    this.valuesProjection = valuesProjection;
  }

  @NotNull
  public ReqUpdateVarProjection itemsProjection() { return valuesProjection; }

  @NotNull
  public ReqUpdateKeysProjection keys() { return keys; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqUpdateMapModelProjection that = (ReqUpdateMapModelProjection) o;
    return Objects.equals(keys, that.keys) &&
           Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), keys, valuesProjection); }
}
