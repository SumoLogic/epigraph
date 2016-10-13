package io.epigraph.refs;

import io.epigraph.names.QualifiedTypeName;
import io.epigraph.names.TypeName;
import io.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TypeReferenceFactory {
  @NotNull
  public static TypeRef createReference(@NotNull Type type) {
    if (type instanceof AnonListType) {
      final AnonListType anonListType = (AnonListType) type;
      return createAnonListReference(anonListType.elementType());
    }

    if (type instanceof AnonMapType) {
      final AnonMapType anonMapType = (AnonMapType) type;
      @NotNull final DatumType keyType = anonMapType.keyType();
      @NotNull final DataType valueType = anonMapType.valueType();
      return createAnonMapReference(keyType, valueType);
    }

    @NotNull final TypeName typeName = type.name();
    if (typeName instanceof QualifiedTypeName) {
      QualifiedTypeName qualifiedTypeName = (QualifiedTypeName) typeName;
      return new QnTypeRef(qualifiedTypeName.toFqn());
    } else
      throw new IllegalArgumentException("Don't know how to handle " + type.getClass().getName());
  }

  @NotNull
  public static AnonListRef createAnonListReference(@NotNull DataType itemType) {
    return new AnonListRef(
      createValueTypeReference(itemType)
    );
  }

  @NotNull
  public static AnonMapRef createAnonMapReference(@NotNull DatumType keyType, @NotNull DataType valueType) {
    return new AnonMapRef(
        createReference(keyType),
        createValueTypeReference(valueType)
    );
  }

  @NotNull
  public static ValueTypeRef createValueTypeReference(@NotNull DataType dataType) {
    @Nullable
    final Type.Tag defaultTag = dataType.defaultTag;
    final String defaultTagName = defaultTag == null ? null : defaultTag.name();

    @NotNull final Type type = dataType.type;
    @NotNull final TypeRef typeRef = createReference(type);

    return new ValueTypeRef(typeRef, defaultTagName);
  }
}
