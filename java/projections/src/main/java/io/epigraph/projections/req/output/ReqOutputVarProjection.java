package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractVarProjection;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

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
}
