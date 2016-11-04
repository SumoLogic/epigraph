package ws.epigraph.projections.op.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpMapModelPath
    extends OpModelPath<OpMapModelPath, MapType>
    implements GenMapModelProjection<
    OpVarPath,
    OpTagPath,
    OpModelPath<?, ?>,
    OpMapModelPath,
    MapType
    > {

  @NotNull
  private final OpVarPath itemsProjection;
  @NotNull
  private final OpPathKeyProjection keyProjection;

  public OpMapModelPath(
      @NotNull MapType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpPathKeyProjection keyProjection,
      @NotNull OpVarPath valuesProjection,
      @NotNull TextLocation location) {

    super(model, params, annotations, location);
    this.itemsProjection = valuesProjection;
    this.keyProjection = keyProjection;
  }

  @NotNull
  public OpVarPath itemsProjection() { return itemsProjection; }

  @NotNull
  public OpPathKeyProjection keyProjection() { return keyProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpMapModelPath that = (OpMapModelPath) o;
    return Objects.equals(itemsProjection, that.itemsProjection) &&
           Objects.equals(keyProjection, that.keyProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection, keyProjection);
  }
}
