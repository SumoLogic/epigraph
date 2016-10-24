package io.epigraph.projections.abs;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenFieldProjection;
import io.epigraph.projections.gen.GenModelProjection;
import io.epigraph.projections.gen.GenTagProjectionEntry;
import io.epigraph.projections.gen.GenVarProjection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractFieldProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>
    > implements GenFieldProjection<VP, TP, MP> {

  @NotNull
  private final Annotations annotations;

  @NotNull
  private final VP projection;

  @NotNull
  private final TextLocation location;

  protected AbstractFieldProjection(
      @NotNull Annotations annotations,
      @NotNull VP projection,
      @NotNull TextLocation location) {
    this.annotations = annotations;
    this.projection = projection;
    this.location = location;
  }

  @NotNull
  @Override
  public Annotations annotations() { return annotations; }

  @NotNull
  @Override
  public VP projection() { return projection; }

  @NotNull
  @Override
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractFieldProjection<?, ?, ?> that = (AbstractFieldProjection<?, ?, ?>) o;
    return Objects.equals(annotations, that.annotations) &&
           Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotations, projection);
  }
}
