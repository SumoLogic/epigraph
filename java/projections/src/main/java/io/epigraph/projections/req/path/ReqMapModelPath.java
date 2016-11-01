package io.epigraph.projections.req.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenMapModelProjection;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqMapModelPath
    extends ReqModelPath<ReqMapModelPath, MapType>
    implements GenMapModelProjection<
    ReqVarPath,
    ReqTagPath,
    ReqModelPath<?, ?>,
    ReqMapModelPath,
    MapType
    > {

  @NotNull
  private final ReqPathKeyProjection key;
  @NotNull
  private final ReqVarPath valuesProjection;

  public ReqMapModelPath(
      @NotNull MapType model,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull ReqPathKeyProjection key,
      @NotNull ReqVarPath valuesProjection,
      @NotNull TextLocation location) {

    super(model, params, annotations, location);
    this.key = key;
    this.valuesProjection = valuesProjection;
  }

  @NotNull
  public ReqVarPath itemsProjection() { return valuesProjection; }

  @NotNull
  public ReqPathKeyProjection key() { return key; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqMapModelPath that = (ReqMapModelPath) o;
    return Objects.equals(key, that.key) &&
           Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), key, valuesProjection); }
}
