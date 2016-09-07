package io.epigraph.types;

import io.epigraph.lang.Fqn;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.names.TypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SimpleTypesResolver implements TypesResolver {
  // todo add support for imports?

  private final Map<Fqn, Type> types;

  public SimpleTypesResolver(Type... types) {
    this.types = new HashMap<>();
    for (Type type : types) {
      @NotNull TypeName typeName = type.name();
      if (typeName instanceof QualifiedTypeName) {
        QualifiedTypeName qualifiedTypeName = (QualifiedTypeName) typeName;
        this.types.put(qualifiedTypeName.toFqn(), type);
      } else {
        throw new IllegalArgumentException("Don't know how to add " + typeName);
      }
    }
  }

  @Override
  public @Nullable Type resolve(@NotNull Fqn reference) {
    return types.get(reference);
  }
}
