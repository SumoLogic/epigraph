package io.epigraph.refs;

import io.epigraph.lang.Qn;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class QnTypeRef implements TypeRef {
  @NotNull
  private final Qn qn;

  public QnTypeRef(@NotNull Qn qn) {this.qn = qn;}

  @NotNull
  public Qn fqn() { return qn; }

  @Override
  public @Nullable Type resolve(@NotNull TypesResolver resolver) {
    return resolver.resolve(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    QnTypeRef that = (QnTypeRef) o;
    return Objects.equals(qn, that.qn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qn);
  }

  @Override
  public String toString() {
    return qn.toString();
  }
}
