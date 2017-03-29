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

/* Created by yegor on 7/22/16. */

package ws.epigraph.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class Unmodifiable {

  private static final Class<?> UnmodifiableCollectionClass =
      Collections.unmodifiableCollection(Collections.emptySet()).getClass();

  private static final Class<?> UnmodifiableMapClass = Collections.unmodifiableMap(Collections.emptyMap()).getClass();

//  private static final Class<?> SingletonMapClass = Collections.singletonMap(null, null).getClass();
//
//  private static final Class<?> EmptyMapClass = Collections.emptyMap().getClass();
//
//  private static final Collection<Class<?>> UnmodifiableMapClasses =
//      Collections.unmodifiableCollection(Arrays.asList(UnmodifiableMapClass, SingletonMapClass, EmptyMapClass));

  private static final Class<?> UnmodifiableSetClass = Collections.unmodifiableSet(Collections.emptySet()).getClass();

//  private static final Class<?> SingletonSetClass = Collections.singleton(null).getClass();
//
//  private static final Class<?> EmptySetClass = Collections.emptySet().getClass();
//
//  private static final Collection<Class<?>> UnmodifiableSetClasses =
//      Collections.unmodifiableCollection(Arrays.asList(UnmodifiableSetClass, SingletonSetClass, EmptySetClass));

  private Unmodifiable() {}

  @Contract(pure = true)
  public static @NotNull <E> Collection<? extends E> collection(@NotNull Collection<? extends E> collection) {
    return UnmodifiableCollectionClass.isInstance(collection)
        ? collection
        : Collections.unmodifiableCollection(collection);
  }

  @Contract(pure = true)
  public static @NotNull <E> Set<? extends E> set(@NotNull Set<? extends E> set) {
    return UnmodifiableSetClass.isInstance(set) ? set : Collections.unmodifiableSet(set);
  }

  @Contract(pure = true)
  public static @NotNull <E> List<? extends E> list(@NotNull List<? extends E> list) {
    return UnmodifiableCollectionClass.isInstance(list) ? list : Collections.unmodifiableList(list);
  }

  @Contract(pure = true)
  public static @NotNull <O, E> List<? extends E> list(
      @NotNull Collection<O> collection,
      @NotNull Function<? super O, ? extends E> elementMapper
  ) {
    List<E> list = new ArrayList<>(collection.size());
    for (O element : collection) list.add(elementMapper.apply(element));
    return Unmodifiable.list(list);
  }

  @Contract(pure = true) // TODO rename to arrayListSupplier()?
  public static @NotNull <E> Supplier<ArrayList<E>> arrayList(int size) { return () -> new ArrayList<E>(size); }

  @Contract(pure = true)
  public static @NotNull <K, V> Map<K, ? extends V> map(@NotNull Map<K, ? extends V> map) {
//    for (Class<?> unmodifiableMapClass : UnmodifiableMapClasses) if (unmodifiableMapClass.isInstance(map)) return map;
//    return Collections.unmodifiableMap(map);
    return UnmodifiableMapClass.isInstance(map) ? map : Collections.unmodifiableMap(map);
  }

  @Contract(pure = true)
  public static @NotNull <K, V> Map<K, V> map_(@NotNull Map<K, V> map) { // todo get rid of the previous one?
//    for (Class<?> unmodifiableMapClass : UnmodifiableMapClasses) if (unmodifiableMapClass.isInstance(map)) return map;
//    return Collections.unmodifiableMap(map);
    return UnmodifiableMapClass.isInstance(map) ? map : Collections.unmodifiableMap(map);
  }

  @Contract(pure = true)
  public static @NotNull <O, K, V> Map<K, ? extends V> map(
      @NotNull Collection<? extends O> collection,
      @NotNull Function<? super O, ? extends K> keyMapper,
      @NotNull Function<? super O, ? extends V> valueMapper
  ) {
    return Unmodifiable.map(collection.stream().collect(
        Collectors.toMap(keyMapper, valueMapper, Unmodifiable.throwingMerger(), Util.hashMapSupplier(collection.size()))
    ));
  }

  @Contract(pure = true)
  @SuppressWarnings("unchecked")
  private static @NotNull <T> BinaryOperator<T> throwingMerger() { return (BinaryOperator<T>) ThrowingMerger; }

  private static final @NotNull BinaryOperator<Void> ThrowingMerger =
      (oldValue, newValue) -> { throw new IllegalStateException(); };

  @Contract(pure = true)
  public static @NotNull <OK, OV, K, V> Map<K, ? extends V> map(
      @NotNull Map<? extends OK, ? extends OV> original,
      @NotNull Function<? super OK, ? extends K> keyMapper,
      @NotNull Function<? super OV, ? extends V> valueMapper
  ) { return map(original, keyMapper, valueMapper, () -> Util.createHashMap(original.size())); }

  @Contract(pure = true)
  public static @NotNull <OK, OV, K, V> Map<K, ? extends V> map(
      @NotNull Map<? extends OK, ? extends OV> original,
      @NotNull Function<? super OK, ? extends K> keyMapper,
      @NotNull Function<? super OV, ? extends V> valueMapper,
      @NotNull Supplier<? extends Map<K, V>> mapSupplier
  ) {
    Map<K, V> map = mapSupplier.get();
    original.forEach((k, v) -> map.put(keyMapper.apply(k), valueMapper.apply(v)));
    return Unmodifiable.map(map);
  }


  /**
   * Unmodifiable mapped view of a collection.
   *
   * @param <O> Original collection element type
   * @param <V> View collection element type
   */
  public static final class CollectionView<O, V> extends AbstractCollection<V> {

    private final @NotNull Collection<? extends O> original;

    private final @NotNull Function<O, V> view;

    public CollectionView(@NotNull Collection<? extends O> original, @NotNull Function<O, V> view) {
      this.original = original;
      this.view = view;
    }

    @Override
    public @NotNull Iterator<V> iterator() { return new MappedIterator<>(original.iterator(), view); }

    @Override
    public int size() { return original.size(); }

  }


  public static final class MappedIterator<O, V> implements Iterator<V> {

    private final @NotNull Iterator<? extends O> iterator;

    private final @NotNull Function<? super O, V> view;

    public MappedIterator(@NotNull Iterator<? extends O> iterator, @NotNull Function<? super O, V> view) {
      this.iterator = iterator;
      this.view = view;
    }

    @Override
    public boolean hasNext() { return iterator.hasNext(); }

    @Override
    public V next() { return view.apply(iterator.next()); }

  }


  /**
   * Unmodifiable mapped view of a list. Optimized for random access original lists.
   *
   * @param <O> Original list element type
   * @param <V> View list element type
   */
  public static final class ListView<O, V> extends AbstractList<V> {

    private final @NotNull List<? extends O> original;

    private final @NotNull Function<O, V> view;

    public ListView(@NotNull List<? extends O> original, @NotNull Function<O, V> view) {
      this.original = original;
      this.view = view;
    }

    @Override
    public V get(int index) { return view.apply(original.get(index)); }

    @Override
    public int size() { return original.size(); }

  }


  /**
   * Unmodifiable mapped view of a map.
   *
   * @param <K>  Key type
   * @param <OV> Original map value type
   * @param <V>  View value type
   */
  public static final class MapView<K, OV, V> extends AbstractMap<K, V> {

    private final @NotNull Map<? extends K, ? extends OV> original;

    private final @NotNull Function<OV, V> view;

    public MapView(@NotNull Map<? extends K, ? extends OV> original, @NotNull Function<OV, V> view) {
      this.original = original;
      this.view = view;
    }

    @Override
    public boolean containsKey(Object key) { return original.containsKey(key); }

    @Override
    public V get(Object key) { return view.apply(original.get(key)); }

    @Override
    public int size() { return original.size(); }

    @Override
    public @NotNull Set<Map.Entry<K, V>> entrySet() {
      return new Unmodifiable.SetView<>(original.entrySet(), Entry::new);
    }


    private final class Entry implements Map.Entry<K, V> {

      private final Map.Entry<? extends K, ? extends OV> original;

      Entry(@NotNull Map.Entry<? extends K, ? extends OV> original) { this.original = original; }

      @Override
      public K getKey() { return original.getKey(); }

      @Override
      public V getValue() { return view.apply(original.getValue()); }

      @Override
      public V setValue(V value) throws UnsupportedOperationException { throw new UnsupportedOperationException(); }

    }


  }

  public static class MapEntry<K, V> implements Map.Entry<K, V> {

    private final @NotNull K key;

    private final @Nullable V value;

    public MapEntry(@NotNull K key, @Nullable V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public @NotNull K getKey() { return key; }

    @Override
    public @Nullable V getValue() { return value; }

    @Override
    public V setValue(V value) throws UnsupportedOperationException { throw new UnsupportedOperationException(); }

  }


  /**
   * Unmodifiable mapped view of a set.
   *
   * @param <O> Original set element type
   * @param <V> View set element type
   */
  public static final class SetView<O, V> extends AbstractSet<V> {

    private final @NotNull Set<? extends O> original;

    private final @NotNull Function<O, V> view;

    public SetView(@NotNull Set<? extends O> original, @NotNull Function<O, V> view) {
      this.original = original;
      this.view = view;
    }

    @Override
    public @NotNull Iterator<V> iterator() { return new MappedIterator<>(original.iterator(), view); }

    @Override
    public int size() { return original.size(); }

  }


}
