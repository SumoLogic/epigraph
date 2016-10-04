package io.epigraph.refs;

import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class AnonMapRef implements TypeRef {

  @NotNull
  private final TypeRef keysType;
  @NotNull
  private final ValueTypeRef valuesType;

  public AnonMapRef(@NotNull TypeRef keysType, @NotNull ValueTypeRef valuesType) {
    this.keysType = keysType;
    this.valuesType = valuesType;
  }

  @NotNull
  public TypeRef keysType() { return keysType; }

  @NotNull
  public ValueTypeRef itemsType() { return valuesType; }

  @Nullable
  @Override
  public Type resolve(@NotNull TypesResolver resolver) {
    return resolver.resolve(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AnonMapRef that = (AnonMapRef) o;
    return Objects.equals(valuesType, that.valuesType);
  }

  @Override
  public int hashCode() { return Objects.hash(valuesType); }

  @Override
  public String toString() {
    return "map[" + keysType + ',' + valuesType + ']';
  }
}
