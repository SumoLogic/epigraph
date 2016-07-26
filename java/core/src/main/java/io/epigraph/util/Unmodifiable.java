/* Created by yegor on 7/22/16. */

package io.epigraph.util;

import io.epigraph.data.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface Unmodifiable {

  Class<?> UnmodifiableCollectionClass = Collections.unmodifiableCollection(new ArrayList()).getClass();

  Class<?> UnmodifiableMapClass = Collections.unmodifiableMap(new HashMap<>()).getClass();

  @NotNull
  public static <E> List<? extends E> list(@NotNull List<? extends E> list) {
    return UnmodifiableCollectionClass.isAssignableFrom(list.getClass())
        ? list
        : Collections.unmodifiableList(list);
  }

  @NotNull
  public static <E> Collection<? extends E> collection(@NotNull Collection<? extends E> list) {
    return UnmodifiableCollectionClass.isAssignableFrom(list.getClass())
        ? list
        : Collections.unmodifiableCollection(list);
  }

  @NotNull
  public static <K, V> Map<K, ? extends V> map(@NotNull Map<K, ? extends V> map) {
    // TODO add Collections.singletonMap().class
    // TODO add Collections.emptyMap().class
    return UnmodifiableMapClass.isAssignableFrom(map.getClass())
        ? map
        : Collections.unmodifiableMap(map);
  }

  @NotNull
  public static <K, V> Supplier<HashMap<K, V>> hashMap(int size) {
    return () -> new HashMap<>(hashMapCapacity(size), 0.75f);
  }

  public static int hashMapCapacity(int size) {
    return (size * 4 + 2) / 3;
  }

}
