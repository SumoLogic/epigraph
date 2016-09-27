/* Created by yegor on 9/27/16. */

package io.epigraph.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Modifiable mapped view of modifiable map.
 *
 * @param <K>  map key type
 * @param <OV> Original map value type
 * @param <V>  View map value type
 */
public final class MapView<K, OV, V> extends AbstractMap<K, V> {

  private final @NotNull Map<K, @NotNull OV> original;

  private final @NotNull Function<@NotNull OV, @Nullable V> view;

  private final @NotNull Function<@Nullable V, @NotNull OV> originalValueConstructor;

  public MapView(
      @NotNull Map<@NotNull K, @NotNull OV> original,
      @NotNull Function<@NotNull OV, @Nullable V> view,
      @NotNull Function<@Nullable V, @NotNull OV> originalValueConstructor
  ) {
    this.original = original;
    this.view = view;
    this.originalValueConstructor = originalValueConstructor;
  }

  @Override
  public boolean containsKey(Object key) { return original.containsKey(key); }

  @Override
  public V get(Object key) { return view.apply(original.get(key)); }

  @Override
  public @Nullable V put(@NotNull K key, @Nullable V value) {
    return view.apply(original.put(key, originalValueConstructor.apply(value)));
  }

  @Override
  public V remove(Object key) { return view.apply(original.remove(key)); }

  @Override
  public int size() { return original.size(); }

  @Override
  public @NotNull Set<Map.Entry<K, V>> entrySet() { return new SetView<>(original.entrySet(), Entry::new); }


  private final class Entry implements Map.Entry<K, V> {

    private final Map.Entry<@NotNull K, @NotNull OV> original;

    public Entry(@NotNull Map.Entry<@NotNull K, @NotNull OV> original) { this.original = original; }

    @Override
    public @NotNull K getKey() { return original.getKey(); }

    @Override
    public @Nullable V getValue() { return view.apply(original.getValue()); }

    @Override
    public @Nullable V setValue(@Nullable V value) {
      return view.apply(original.setValue(originalValueConstructor.apply(value)));
    }

  }


}
