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

package ws.epigraph.types;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.util.Unmodifiable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractTypesIndex {
  public final @NotNull Map<@NotNull String, @NotNull ? extends Type> types;

  private Map<String, Set<? extends Type>> children = null;

  protected AbstractTypesIndex(final @NotNull Map<@NotNull String, @NotNull ? extends Type> types) {
    this.types = types;
  }

  @SuppressWarnings("unchecked")
  public synchronized @NotNull Set<? extends Type> children(@NotNull Type type) {
    if (children == null) {
      Map<String, Set<? extends Type>> _children = new HashMap<>();

      for (final Type _type : types.values()) {

        for (final Type supertype : _type.immediateSupertypes()) {
          String supertypeName = supertype.name().toString();

          Set<Type> c = (Set<Type>) _children.get(supertypeName);
          if (c == null) c = new HashSet<>();

          c.add(_type);

          _children.put(supertypeName, c);
        }
      }

      children = _children;
    }

    Set<? extends Type> c = children.get(type.name().toString());
    return c == null ? Collections.emptySet() : Unmodifiable.set(c);
  }
}
