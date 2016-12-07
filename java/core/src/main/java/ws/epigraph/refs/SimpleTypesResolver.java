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

import ws.epigraph.lang.Qn;
import ws.epigraph.names.QualifiedTypeName;
import ws.epigraph.names.TypeName;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SimpleTypesResolver implements TypesResolver {
  // todo add support for imports?

  private final Map<Qn, Type> types;
  private final Map<DataType, AnonListType> anonLists;
  private final Map<DataType, Map<DatumType, AnonMapType>> anonMaps;

  public SimpleTypesResolver(Type... types) {
    this.types = new HashMap<>();
    anonLists = new HashMap<>();
    anonMaps = new HashMap<>();

    for (Type type : types) {
      @NotNull TypeName typeName = type.name();
      if (typeName instanceof QualifiedTypeName) {
        QualifiedTypeName qualifiedTypeName = (QualifiedTypeName) typeName;
        this.types.put(qualifiedTypeName.toFqn(), type);
      } else if (type instanceof AnonListType) {
        AnonListType anonListType = (AnonListType) type;
        anonLists.put(anonListType.elementType(), anonListType);
      } else if (type instanceof AnonMapType) {
        AnonMapType anonMapType = (AnonMapType) type;
        Map<DatumType, AnonMapType> mapsByKeyType = anonMaps.get(anonMapType.valueType());
        if (mapsByKeyType == null) mapsByKeyType = new HashMap<>();
        mapsByKeyType.put(anonMapType.keyType(), anonMapType);
        anonMaps.put(anonMapType.valueType, mapsByKeyType);
      } else {
        throw new IllegalArgumentException("Don't know how to add " + typeName);
      }
    }
  }

  @Override
  public @Nullable Type resolve(@NotNull QnTypeRef reference) {
    return types.get(reference.qn());
  }

  @Override
  public @Nullable Type resolve(@NotNull AnonListRef reference) {
    @NotNull ValueTypeRef itemTypeRef = reference.itemsType();
    @Nullable DataType itemType = resolve(itemTypeRef);
    return itemType == null ? null : anonLists.get(itemType);
  }

  @Override
  public @Nullable Type resolve(@NotNull AnonMapRef reference) {
    @NotNull ValueTypeRef valueTypeRef = reference.itemsType();
    @Nullable DataType valueType = resolve(valueTypeRef);
    if (valueType == null) return null;
    Map<DatumType, AnonMapType> mapsByKey = anonMaps.get(valueType);
    if (mapsByKey == null) return null;

    @Nullable Type keyType = reference.keysType().resolve(this);
    if (keyType instanceof DatumType) {
      DatumType keyDatumType = (DatumType) keyType;
      return mapsByKey.get(keyDatumType);
    } else return null;
  }
}
