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

/* Created by yegor on 9/1/16. */

package ws.epigraph.types;

import ws.epigraph.data.Data;
import ws.epigraph.names.DataTypeName;
import ws.epigraph.types.Type.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Declares type of data held by container type components (record fields, list elements, map values).
 */
public final class DataType {

  public final @NotNull Type type;

  public final @Nullable Tag defaultTag;

  public final @NotNull DataTypeName name;

  public DataType(@NotNull Type type, @Nullable Tag defaultTag) {
    this.type = type;
    this.defaultTag = defaultTag;
    this.name = new DataTypeName(type.name(), defaultTag == null ? null : defaultTag.name);
  }

//  public @NotNull Type type() { return type; }
//
//  public @Nullable Tag defaultTag() { return defaultTag; }

  public <D extends Data> D checkAssignable(@NotNull D data) throws IllegalArgumentException {
    return type.checkAssignable(data);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DataType dataType = (DataType) o;
    return Objects.equals(name, dataType.name);
  }

  @Override
  public int hashCode() { return name.hashCode(); }

}
