/*
 * Copyright 2017 Sumo Logic
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

import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface TypesResolver {
  @Nullable
  TypeApi resolve(@NotNull QnTypeRef reference);

  @Nullable
  TypeApi resolve(@NotNull AnonListRef reference);

  @Nullable
  TypeApi resolve(@NotNull AnonMapRef reference);

  default @Nullable DataTypeApi resolve(@NotNull ValueTypeRef valueTypeRef) {
    @Nullable TypeApi type = valueTypeRef.typeRef().resolve(this);
    if (type == null) return null;

    final TagApi defaultTag;
    @Nullable String defaultTagOverride = valueTypeRef.defaultOverride();
    if (defaultTagOverride == null) {
      if (type.kind() == TypeKind.ENTITY) defaultTag = null;
      else defaultTag = ((DatumTypeApi) type).self();
    } else {
      defaultTag = type.tagsMap().get(defaultTagOverride);
      if (defaultTag == null) return null;
    }

    if (type instanceof EntityTypeApi) {
      EntityTypeApi entityTypeApi = (EntityTypeApi) type;
      return entityTypeApi.dataType(defaultTag);
    }

    if (type instanceof DatumTypeApi) {
      DatumTypeApi datumTypeApi = (DatumTypeApi) type;
      return datumTypeApi.dataType();
    }

    throw new RuntimeException("Unknown type: " + type.getClass().getName());
  }

}
