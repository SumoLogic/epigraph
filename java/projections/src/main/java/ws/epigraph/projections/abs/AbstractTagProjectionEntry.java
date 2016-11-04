package ws.epigraph.projections.abs;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractTagProjectionEntry<MP extends AbstractModelProjection</*MP*/?, ?>>
    implements GenTagProjectionEntry<MP> {
  @NotNull
  private final Type.Tag tag;
  @NotNull
  private final MP projection;
  @NotNull
  private final TextLocation location;

  public AbstractTagProjectionEntry(@NotNull Type.Tag tag, @NotNull MP projection, @NotNull TextLocation location) {
    this.tag = tag;
    this.projection = projection;
    this.location = location;
  }

  @NotNull
  public Type.Tag tag() { return tag; }

  @NotNull
  public MP projection() { return projection; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractTagProjectionEntry that = (AbstractTagProjectionEntry) o;
    return Objects.equals(tag.name(), that.tag.name()) && Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(tag.name(), projection); }
}
