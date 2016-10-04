package io.epigraph.refs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ValueTypeRef {
  @NotNull
  private final TypeRef typeRef;
  @Nullable
  private final String defaultOverride;

  public ValueTypeRef(@NotNull TypeRef typeRef, @Nullable String defaultOverride) {
    this.typeRef = typeRef;
    this.defaultOverride = defaultOverride;
  }

  @NotNull
  public TypeRef typeRef() {
    return typeRef;
  }

  @Nullable
  public String defaultOverride() {
    return defaultOverride;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ValueTypeRef that = (ValueTypeRef) o;
    return Objects.equals(typeRef, that.typeRef) &&
           Objects.equals(defaultOverride, that.defaultOverride);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeRef, defaultOverride);
  }

  @Override
  public String toString() {
    String s = typeRef.toString();
    return defaultOverride == null ? s : s + " default " + defaultOverride;
  }
}
