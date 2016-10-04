package io.epigraph.projections;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionUtils {
  @NotNull
  public static <K, V> LinkedHashMap<K, V> singletonLinkedHashMap(@NotNull K key, @NotNull V value) {
    final LinkedHashMap<K, V> res = new LinkedHashMap<K, V>();
    res.put(key, value);
    return res;
  }
}
