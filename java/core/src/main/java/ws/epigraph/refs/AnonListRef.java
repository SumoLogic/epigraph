package ws.epigraph.refs;

import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class AnonListRef implements TypeRef {
  @NotNull
  private final ValueTypeRef itemsType;

  public AnonListRef(@NotNull ValueTypeRef itemsType) {this.itemsType = itemsType;}

  @NotNull
  public ValueTypeRef itemsType() {
    return itemsType;
  }

  @Nullable
  @Override
  public Type resolve(@NotNull TypesResolver resolver) {
    return resolver.resolve(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AnonListRef that = (AnonListRef) o;
    return Objects.equals(itemsType, that.itemsType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemsType);
  }

  @Override
  public String toString() {
    return "list[" + itemsType + ']';
  }
}
