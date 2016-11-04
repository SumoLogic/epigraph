package ws.epigraph.refs;

import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;
import ws.epigraph.types.UnionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface TypeRef {
  @Nullable
  Type resolve(@NotNull TypesResolver resolver);

  @Nullable
  default DatumType resolveDatumType(@NotNull TypesResolver resolver) {
    Type t = resolve(resolver);
    if (t instanceof DatumType) return (DatumType) t;
    else return null; // or throw if it has wrong kind?
  }

  @Nullable
  default UnionType resolveVarType(@NotNull TypesResolver resolver) {
    Type t = resolve(resolver);
    if (t instanceof UnionType) return (UnionType) t;
    else return null; // or throw if it has wrong kind?
  }
}
