package io.epigraph.projections.req.update;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateKeysProjection {
  public static final ReqUpdateKeysProjection UPDATE_KEYS = new ReqUpdateKeysProjection(true);
  public static final ReqUpdateKeysProjection REPLACE_KEYS = new ReqUpdateKeysProjection(false);

  private final boolean update;

  public ReqUpdateKeysProjection(boolean update) {this.update = update;}

  /**
   * @return {@code true} if map entries must be updated (replaced), {@code false} if they must be patched
   */
  public boolean update() { return update; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReqUpdateKeysProjection that = (ReqUpdateKeysProjection) o;
    return update == that.update;
  }

  @Override
  public int hashCode() {
    return Objects.hash(update);
  }
}
