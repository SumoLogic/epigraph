package ws.epigraph.projections;

import org.jetbrains.annotations.NotNull;

/**
 * Projection together with number of steps along the path. Projection should not have
 * any branching points in the first `steps` segments.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class StepsAndProjection<P> {
  private final int pathSteps;
  @NotNull
  private final P projection;

  public StepsAndProjection(int pathSteps, @NotNull P projection) {
    this.pathSteps = pathSteps;
    this.projection = projection;
  }

  public int pathSteps() {
    return pathSteps;
  }

  @NotNull
  public P projection() {
    return projection;
  }
}
