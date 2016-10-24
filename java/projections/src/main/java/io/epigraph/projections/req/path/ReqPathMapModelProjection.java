package io.epigraph.projections.req.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenMapModelProjection;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathMapModelProjection
    extends ReqPathModelProjection<ReqPathMapModelProjection, MapType>
    implements GenMapModelProjection<
    ReqPathVarProjection,
    ReqPathTagProjectionEntry,
    ReqPathModelProjection<?, ?>,
    ReqPathMapModelProjection,
    MapType
    > {

  @NotNull
  private final ReqPathKeyProjection key;
  @NotNull
  private final ReqPathVarProjection valuesProjection;

  public ReqPathMapModelProjection(
      @NotNull MapType model,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqPathMapModelProjection metaProjection,
      @NotNull ReqPathKeyProjection key,
      @NotNull ReqPathVarProjection valuesProjection,
      @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, location);
    this.key = key;
    this.valuesProjection = valuesProjection;
  }

  @NotNull
  public ReqPathVarProjection itemsProjection() { return valuesProjection; }

  @NotNull
  public ReqPathKeyProjection key() { return key; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqPathMapModelProjection that = (ReqPathMapModelProjection) o;
    return Objects.equals(key, that.key) &&
           Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), key, valuesProjection); }
}
