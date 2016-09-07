package io.epigraph.types;

import io.epigraph.lang.Fqn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface TypesResolver {
  @Nullable
  Type resolve(@NotNull Fqn reference);

  @Nullable
  default DatumType resolveDatumType(@NotNull Fqn reference) {
    Type t = resolve(reference);
    if (t instanceof DatumType) return (DatumType) t;
    else return null; // or throw if it has wrong kind?
  }

  @Nullable
  default UnionType resolveVarType(@NotNull Fqn reference) {
    Type t = resolve(reference);
    if (t instanceof UnionType) return (UnionType) t;
    else return null; // or throw if it has wrong kind?
  }
}
