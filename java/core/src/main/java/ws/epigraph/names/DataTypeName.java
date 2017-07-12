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

/* Created by yegor on 9/1/16. */

package ws.epigraph.names;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.DatumTypeApi;

import java.util.Objects;

public final class DataTypeName {

  public final @NotNull TypeName typeName;

  public final @Nullable String defaultTagName; // todo rename to retro

  public DataTypeName(@NotNull TypeName typeName, @Nullable String defaultTagName) {
    this.typeName = typeName;
    this.defaultTagName = defaultTagName;
  }

  public static @NotNull DataTypeName get(@NotNull TypeName typeName, @Nullable String defaultTagName) {
    return new DataTypeName(typeName, defaultTagName); // TODO cache somewhere?
  }

  @Override // TODO cache?
  public String toString() { return typeName + (defaultTagName == null || defaultTagName.equals(DatumTypeApi.MONO_TAG_NAME) ? "" : " default " + defaultTagName); }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DataTypeName that = (DataTypeName) o;
    return Objects.equals(typeName, that.typeName) && Objects.equals(defaultTagName, that.defaultTagName);
  }

  @Override
  public int hashCode() { return Objects.hash(typeName, defaultTagName); } // TODO cache?

}
