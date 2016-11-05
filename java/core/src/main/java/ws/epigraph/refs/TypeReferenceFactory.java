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
