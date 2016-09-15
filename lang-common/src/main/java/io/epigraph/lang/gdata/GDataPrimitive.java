package io.epigraph.lang.gdata;

import io.epigraph.lang.Fqn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GDataPrimitive extends GDataVarValue {
  @NotNull
  private final Object value;

  public GDataPrimitive(@Nullable Fqn typeRef, @NotNull Object value) {
    super(typeRef);
    this.value = value;
  }

  @NotNull
  public Object value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GDataPrimitive that = (GDataPrimitive) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  @Override
  public String toString() {
    if (typeRef() == null) return value.toString();
    else return typeRef() + "@" + value;
  }
}
