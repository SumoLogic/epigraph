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

import ws.epigraph.types.DataType;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface TypesResolver {
  @Nullable
  Type resolve(@NotNull QnTypeRef reference);

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
        defaultTag = null;
    }

    return new DataType(type, defaultTag);
  }

}
