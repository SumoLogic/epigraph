package io.epigraph.projections.generic;

import io.epigraph.lang.TextLocation;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GenericFieldProjectionEntry<FP> {
  @NotNull
  private final RecordType.Field field;
  @NotNull
  private final FP projection;
  @NotNull
  private final TextLocation location;

  public GenericFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull FP projection,
      @NotNull TextLocation location) {

    this.field = field;
    this.projection = projection;
    this.location = location;
  }

  @NotNull
  public RecordType.Field field() { return field; }

  @NotNull
  public FP projection() { return projection; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GenericFieldProjectionEntry that = (GenericFieldProjectionEntry) o;
    return Objects.equals(field.name(), that.field.name()) &&
           Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field.name(), projection);
  }
}
