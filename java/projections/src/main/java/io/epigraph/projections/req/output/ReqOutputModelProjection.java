package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.generic.GenericModelProjection;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReqOutputModelProjection<M extends DatumType> extends GenericModelProjection<M> {

  protected final boolean required;
  @Nullable
  protected final ReqParams params;
  @Nullable
  protected final Annotations annotations;
  @Nullable
  protected final ReqOutputModelProjection<?> metaProjection;

  public ReqOutputModelProjection(@NotNull M model,
                                  boolean required,
                                  @Nullable ReqParams params,
                                  @Nullable Annotations annotations,
                                  @Nullable ReqOutputModelProjection<?> metaProjection,
                                  @NotNull TextLocation location) {
    super(model, annotations, location);
    this.required = required;
    this.params = params;
    this.annotations = annotations;
    this.metaProjection = metaProjection;
  }

  public boolean required() { return required; }

  @Nullable
  public ReqParams params() { return params; }

  @Nullable
  public Annotations annotations() { return annotations; }

  @Nullable
  public ReqOutputModelProjection<?> metaProjection() { return metaProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqOutputModelProjection<?> that = (ReqOutputModelProjection<?>) o;
    return required == that.required &&
           Objects.equals(params, that.params) &&
           Objects.equals(annotations, that.annotations) &&
           Objects.equals(metaProjection, that.metaProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), required, params, annotations, metaProjection);
  }
}
