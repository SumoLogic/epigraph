package io.epigraph.projections.abs;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenModelProjection;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractModelProjection<MP extends GenModelProjection</*MP*/?, ?>, M extends DatumType>
    implements GenModelProjection<MP, M> {
  @NotNull
  protected final M model;
  @Nullable
  protected final MP metaProjection;
  @Nullable
  protected final Annotations annotations;
  @NotNull
  private final TextLocation location;

  public AbstractModelProjection(
      @NotNull M model,
      @Nullable MP metaProjection, @Nullable Annotations annotations,
      @NotNull TextLocation location) {
    this.model = model;
    this.metaProjection = metaProjection;
    this.annotations = annotations;
    this.location = location;
  }

  @NotNull
  public M model() { return model; }

  @Nullable
  @Override
  public MP metaProjection() { return metaProjection; }

  @Nullable
  public Annotations annotations() { return annotations; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractModelProjection<?, ?> that = (AbstractModelProjection<?, ?>) o;
    return Objects.equals(model, that.model) &&
           Objects.equals(metaProjection, that.metaProjection) &&
           Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(model, metaProjection, annotations);
  }
}
