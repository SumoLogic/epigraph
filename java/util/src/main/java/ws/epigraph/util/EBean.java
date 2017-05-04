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

package ws.epigraph.util;

import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * General-purpose heterogeneous properties storage.
 * <p>
 * Please don't use keys with different types but same name, no checks are done around that.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("unchecked")
@ThreadSafe
public final class EBean {
  private final ConcurrentHashMap<Key<?>, Object> storage = new ConcurrentHashMap<>();

  /**
   * Puts a property in the context
   *
   * @param key   property key
   * @param value new property value
   * @param <V>   value type
   *
   * @return old property value or {@code null} if there was none
   */
  public @Nullable <V> V put(@NotNull Key<V> key, @NotNull V value) {
    return (V) storage.put(key, value);
  }

  /**
   * Gets property from the context
   *
   * @param key property key
   * @param <V> value type
   *
   * @return property value or {@code null} if there is none
   */
  public @Nullable <V> V get(@NotNull Key<V> key) {
    return (V) storage.get(key);
  }

  /**
   * Checks if there is a property set by a given key
   *
   * @param key property key
   *
   * @return {@code true} iff property is set
   */
  public boolean contains(@NotNull Key<?> key) {
    return storage.containsKey(key);
  }

  /**
   * removes property by key
   *
   * @param key property key
   * @param <V> property value type
   *
   * @return old property value or {@code null} if there was none
   */
  public @Nullable <V> V remove(@NotNull Key<V> key) {
    return (V) storage.remove(key);
  }

  public static final class Key<V> {
    private final @NotNull String name;

    public Key(final @NotNull String name) {this.name = name;}

    @Contract(pure = true)
    public @NotNull String name() { return name; }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final Key<?> key = (Key<?>) o;
      return Objects.equals(name, key.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }
  }
}
