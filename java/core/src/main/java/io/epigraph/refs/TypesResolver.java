package io.epigraph.refs;

import io.epigraph.types.DataType;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface TypesResolver {
  @Nullable
  Type resolve(@NotNull FqnTypeRef reference);

  @Nullable
  Type resolve(@NotNull AnonListRef reference);

  @Nullable
  Type resolve(@NotNull AnonMapRef reference);

  @Nullable
  default DataType resolve(@NotNull ValueTypeRef valueTypeRef) {
    @Nullable Type type = valueTypeRef.typeRef().resolve(this);
    if (type == null) return null;

    final Type.Tag defaultTag;
    @Nullable String defaultTagOverride = valueTypeRef.defaultOverride();
    if (defaultTagOverride != null) {
      defaultTag = type.tagsMap().get(defaultTagOverride);
      if (defaultTag == null) return null;
    } else {
//      if (type instanceof DatumType) {
//        DatumType datumType = (DatumType) type;
//        defaultTag = datumType.self;
//      } else
        defaultTag = null;
    }

    return new DataType(type, defaultTag);
  }

}
