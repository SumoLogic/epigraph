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

package ws.epigraph.assembly;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Assembler context, used by generated data assemblers to detect recursive data
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class AsmContext {
  public final Map<Key, Object> visited = new HashMap<>();

  public static final class Key {
    public final @NotNull Object dto; // should we compare it by identity?
    public final @NotNull Object projection;

    public Key(@NotNull Object dto, @NotNull Object projection) {
      this.dto = dto;
      this.projection = projection;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final Key key = (Key) o;
      return Objects.equals(dto, key.dto) &&
             Objects.equals(projection, key.projection);
    }

    @Override
    public int hashCode() {
      return Objects.hash(dto, projection);
    }
  }
}
