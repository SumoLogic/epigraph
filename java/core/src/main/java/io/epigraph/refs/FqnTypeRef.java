package io.epigraph.refs;

import io.epigraph.lang.Fqn;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class FqnTypeRef implements TypeRef {
  @NotNull
  private final Fqn fqn;

  public FqnTypeRef(@NotNull Fqn fqn) {this.fqn = fqn;}

  @NotNull
  public Fqn fqn() { return fqn; }

  @Override
  public @Nullable Type resolve(@NotNull TypesResolver resolver) {
    return resolver.resolve(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FqnTypeRef that = (FqnTypeRef) o;
    return Objects.equals(fqn, that.fqn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fqn);
  }

  @Override
  public String toString() {
    return fqn.toString();
  }
}
