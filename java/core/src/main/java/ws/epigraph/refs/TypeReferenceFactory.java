/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.refs;

import ws.epigraph.names.QualifiedTypeName;
import ws.epigraph.names.TypeName;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class TypeReferenceFactory {
  private TypeReferenceFactory() {}

  public static @NotNull TypeRef createReference(@NotNull TypeApi type) {
    if (type instanceof AnonListType) {
      final AnonListType anonListType = (AnonListType) type;
      return createAnonListReference(anonListType.elementType());
    }

    if (type instanceof AnonMapType) {
      final AnonMapType anonMapType = (AnonMapType) type;
      final @NotNull DatumType keyType = anonMapType.keyType();
      final @NotNull DataType valueType = anonMapType.valueType();
      return createAnonMapReference(keyType, valueType);
    }

    final @NotNull TypeName typeName = type.name();
    if (typeName instanceof QualifiedTypeName) {
      QualifiedTypeName qualifiedTypeName = (QualifiedTypeName) typeName;
      return new QnTypeRef(qualifiedTypeName.toFqn());
    } else
      throw new IllegalArgumentException("Don't know how to handle " + type.getClass().getName());
  }

  public static @NotNull AnonListRef createAnonListReference(@NotNull DataTypeApi itemType) {
    return new AnonListRef(
      createValueTypeReference(itemType)
    );
  }

  public static @NotNull AnonMapRef createAnonMapReference(@NotNull DatumTypeApi keyType, @NotNull DataTypeApi valueType) {
    return new AnonMapRef(
        createReference(keyType),
        createValueTypeReference(valueType)
    );
  }

  public static @NotNull ValueTypeRef createValueTypeReference(@NotNull DataTypeApi dataType) {
    final @Nullable TagApi defaultTag = dataType.defaultTag();
    final String defaultTagName = defaultTag == null ? null : defaultTag.name();

    final @NotNull TypeApi type = dataType.type();
    final @NotNull TypeRef typeRef = createReference(type);

    return new ValueTypeRef(typeRef, defaultTagName);
  }
}
