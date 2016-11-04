package ws.epigraph.projections.req.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractVarProjection;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputVarProjection extends AbstractVarProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?>
    > {
  private final boolean parenthesized;
      // if parens were present, e.g. `:(id, ...)`. Tells marshaller if to use multi- or single-var

  public ReqOutputVarProjection(
      @NotNull Type type,
      @NotNull Map<String, ReqOutputTagProjectionEntry> tagProjections,
      @Nullable List<ReqOutputVarProjection> polymorphicTails,
      boolean parenthesized,
      @NotNull TextLocation location) {
    super(type, tagProjections, polymorphicTails, location);
    this.parenthesized = parenthesized;
  }

  public boolean parenthesized() { return parenthesized; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqOutputVarProjection that = (ReqOutputVarProjection) o;
    return parenthesized == that.parenthesized;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), parenthesized);
  }
}
