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

/* Created by yegor on 8/12/16. */

package ws.epigraph.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Util {

  @Contract(pure = true)
  static @NotNull <K, V> Supplier<HashMap<K, V>> hashMapSupplier(int size) { return () -> createHashMap(size); }

  @Contract(pure = true)
  static @NotNull <K, V> HashMap<K, V> createHashMap(int size) { return new HashMap<>(hashMapCapacity(size), 0.75f); }

  @Contract(pure = true)
  static @NotNull <K, V> LinkedHashMap<K, V> createLinkedHashMap(int size) {
    return new LinkedHashMap<>(hashMapCapacity(size), 0.75f);
  }

  @Contract(pure = true)
  static int hashMapCapacity(int size) { return (size * 4 + 2) / 3; } // TODO make sure arithmetic is correct

  @Contract("null, _ -> null")
  static <T, R> @Nullable R apply(@Nullable T arg, @NotNull Function<T, @Nullable R> function) {
    return arg == null ? null : function.apply(arg);
  }

  static <T, R> @NotNull R apply(
      @Nullable T arg,
      @NotNull Function<@NotNull T, @NotNull R> function,
      @NotNull R ifNull
  ) { return arg == null ? ifNull : function.apply(arg); }

  @SuppressWarnings("unchecked")
  @Contract(value = "null -> null; !null -> !null", pure = true)
  static <E> List<E> cast(List<? super E> list) { return (List<E>) list; }

  /**
   * cast readable list to a readable list of sub-element type
   */
  @SuppressWarnings("unchecked")
  @Contract(value = "null -> null; !null -> !null", pure = true)
  static <SE, E extends SE> List<? extends E> castEx(List<? extends SE> list) { return (List<? extends E>) list; }

  @SuppressWarnings("unchecked")
  @Contract(value = "null -> null; !null -> !null", pure = true)
  static <K, V> Map<K, V> cast(Map<? super K, ? super V> map) { return (Map<K, V>) map; }

  @SuppressWarnings("unchecked")
  @Contract(value = "null -> null; !null -> !null", pure = true)
  static <SK, SV, K extends SK, V extends SV> Map<? extends K, ? extends V> castEx(Map<? extends SK, ? extends SV> map) {
    return (Map<? extends K, ? extends V>) map;
  }

  static @NotNull <V> List<V> tail(@NotNull List<V> list) {
    if (list.isEmpty()) throw new IllegalArgumentException("Can't remove head from an empty list");
    return list.subList(1, list.size());
  }

  static <K, V> LinkedHashMap<K, V> createSingletonLinkedHashMap(K key, V value) {
    final LinkedHashMap<K, V> res = createLinkedHashMap(1);
    res.put(key, value);
    return res;
  }

  static <K, V> LinkedHashMap<K, V> createLinkedHashMap(K[] keys, V[] values) {
    if (keys.length != values.length)
      throw new IllegalArgumentException("Keys size " + keys.length + " != values size " + values.length);

    int size = keys.length;
    final LinkedHashMap<K, V> res = createLinkedHashMap(size);

    for (int i = 0; i < size; i++) {
      res.put(keys[i], values[i]);
    }

    return res;
  }

  static <K, V> HashMap<K, V> createSingletonHashMap(K key, V value) {
    final HashMap<K, V> res = createHashMap(1);
    res.put(key, value);
    return res;
  }

  static <K, V> HashMap<K, V> createHashMap(K[] keys, V[] values) {
    if (keys.length != values.length)
      throw new IllegalArgumentException("Keys size " + keys.length + " != values size " + values.length);

    int size = keys.length;
    final HashMap<K, V> res = createHashMap(size);

    for (int i = 0; i < size; i++) {
      res.put(keys[i], values[i]);
    }

    return res;
  }
}
