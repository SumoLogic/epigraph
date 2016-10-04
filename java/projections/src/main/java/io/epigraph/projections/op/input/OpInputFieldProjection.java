package io.epigraph.projections.op.input;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputFieldProjection {
  @Nullable
  private final Annotations annotations;
  @NotNull
  private final OpInputVarProjection projection;
  private final boolean required;
  @NotNull
  private final TextLocation location;

  public OpInputFieldProjection(@Nullable Annotations annotations,
                                @NotNull OpInputVarProjection projection,
                                boolean required,
                                @NotNull TextLocation location) {
    this.annotations = annotations;
    this.projection = projection;
    this.required = required;
    this.location = location;
  }

  @Nullable
  public Annotations annotations() { return annotations; }

  @NotNull
  public OpInputVarProjection projection() { return projection; }

  public boolean required() { return required; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpInputFieldProjection that = (OpInputFieldProjection) o;
    return required == that.required &&
           Objects.equals(annotations, that.annotations) &&
           Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(annotations, projection, required); }
}
