package io.epigraph.projections.op.delete;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractVarProjection;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteVarProjection extends AbstractVarProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>
    > {

  private final boolean canDelete;

  public OpDeleteVarProjection(
      @NotNull Type type,
      boolean canDelete,
      @NotNull LinkedHashMap<String, OpDeleteTagProjectionEntry> tagProjections,
      @Nullable List<OpDeleteVarProjection> polymorphicTails,
      @NotNull TextLocation location) {
    super(type, tagProjections, polymorphicTails, location);
    this.canDelete = canDelete;
  }

  public boolean canDelete() { return canDelete; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpDeleteVarProjection that = (OpDeleteVarProjection) o;
    return canDelete == that.canDelete;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), canDelete);
  }
}
