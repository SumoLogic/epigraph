package io.epigraph.projections.op.output;

import io.epigraph.projections.op.OpCustomParams;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputMapModelProjection extends OpOutputModelProjection<MapType> {
  @NotNull
  private final OpOutputVarProjection itemsProjection;
  @NotNull
  private final OpOutputKeyProjection keyProjection;

  public OpOutputMapModelProjection(@NotNull MapType model,
                                    boolean includeInDefault,
                                    @Nullable OpParams params,
                                    @Nullable OpCustomParams customParams,
                                    @Nullable OpOutputModelProjection<?> metaProjection,
                                    @NotNull OpOutputKeyProjection keyProjection,
                                    @NotNull OpOutputVarProjection valuesProjection) {
    super(model, includeInDefault, params, customParams, metaProjection);
    this.itemsProjection = valuesProjection;
    this.keyProjection = keyProjection;
  }

  @NotNull
  public OpOutputVarProjection itemsProjection() { return itemsProjection; }

  @NotNull
  public OpOutputKeyProjection keyProjection() { return keyProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpOutputMapModelProjection that = (OpOutputMapModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection) &&
           Objects.equals(keyProjection, that.keyProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection, keyProjection);
  }
}
