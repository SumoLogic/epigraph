package io.epigraph.projections.op.input;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractFieldProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputFieldProjection extends AbstractFieldProjection<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?>
    > {

  private final boolean required;

  public OpInputFieldProjection(
      @Nullable Annotations annotations,
      @NotNull OpInputVarProjection projection,
      boolean required,
      @NotNull TextLocation location) {
    super(annotations, projection, location);
    this.required = required;
  }

  public boolean required() { return required; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputFieldProjection that = (OpInputFieldProjection) o;
    return required == that.required;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), required);
  }
}
