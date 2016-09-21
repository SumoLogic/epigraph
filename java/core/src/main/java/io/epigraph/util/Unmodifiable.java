/* Created by yegor on 7/22/16. */

package io.epigraph.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface Unmodifiable {

  Class<?> UnmodifiableCollectionClass = Collections.unmodifiableCollection(new ArrayList()).getClass();

  Class<?> UnmodifiableMapClass = Collections.unmodifiableMap(new HashMap<>()).getClass();

  Class<?> SingletonMapClass = Collections.singletonMap(null, null).getClass();

  Class<?> EmptyMapClass = Collections.emptyMap().getClass();

  Collection<Class<?>> UnmodifiableMapClasses =
      Collections.unmodifiableCollection(Arrays.asList(UnmodifiableMapClass, SingletonMapClass, EmptyMapClass));

  @Contract(pure = true)
  static @NotNull <T> BinaryOperator<T> throwingMerger() {
    return (oldValue, newValue) -> { throw new IllegalStateException(); };
  }

  @Contract(pure = true)
  static @NotNull <E> Collection<? extends E> collection(@NotNull Collection<? extends E> collection) {
    return UnmodifiableCollectionClass.isInstance(collection)
        ? collection
        : Collections.unmodifiableCollection(collection);
  }

  @Contract(pure = true)
  static @NotNull <E> List<? extends E> list(@NotNull List<? extends E> list) {
    return UnmodifiableCollectionClass.isInstance(list)
        ? list
        : Collections.unmodifiableList(list);
  }

  @Contract(pure = true)
  static @NotNull <O, E> List<? extends E> list(
      @NotNull Collection<O> collection,
      @NotNull Function<? super O, ? extends E> elementMapper
  ) {
    List<E> list = new ArrayList<>(collection.size());
    for (O element : collection) list.add(elementMapper.apply(element));
    return Unmodifiable.list(list);
    // return Unmodifiable.list(collection.stream().map(elementMapper).collect(Collectors.toList())); // doesn't start with empty list of required size
  }

  @Contract(pure = true) // TODO rename to arrayListSupplier()?
  static @NotNull <E> Supplier<ArrayList<E>> arrayList(int size) { return () -> new ArrayList<E>(size); }

  @Contract(pure = true)
  static @NotNull <K, V> Map<K, ? extends V> map(@NotNull Map<K, ? extends V> map) {
    for (Class<?> unmodifiableMapClass : UnmodifiableMapClasses) if (unmodifiableMapClass.isInstance(map)) return map;
    return Collections.unmodifiableMap(map);
  }

  @Contract(pure = true)
  static @NotNull <O, K, V> Map<K, ? extends V> map(
      @NotNull Collection<? extends O> collection,
      @NotNull Function<? super O, ? extends K> keyMapper,
      @NotNull Function<? super O, ? extends V> valueMapper
  ) {
    return Unmodifiable.map(collection.stream().collect(
        Collectors.toMap(keyMapper, valueMapper, Unmodifiable.throwingMerger(), Unmodifiable.hashMap(collection.size()))
    ));
  }

  @Contract(pure = true)
  static @NotNull <OK, OV, K, V> Map<K, ? extends V> map(
      @NotNull Map<? extends OK, ? extends OV> original,
      @NotNull Function<? super OK, ? extends K> keyMapper,
      @NotNull Function<? super OV, ? extends V> valueMapper
  ) {
    HashMap<K, V> map = Unmodifiable.<K, V>hashMap(original.size()).get();
    original.forEach((k, v) -> map.put(keyMapper.apply(k), valueMapper.apply(v)));
    return Unmodifiable.map(map);
  }

  @Contract(pure = true)
  static @NotNull <K, V> Supplier<HashMap<K, V>> hashMap(int size) { // TODO hashMapSupplier?
    return () -> new HashMap<>(hashMapCapacity(size), 0.75f);
  }

  @Contract(pure = true)
  static int hashMapCapacity(int size) { return (size * 4 + 2) / 3; } // TODO make sure arithmetic is correct


  /**
   * Unmodifiable mapped collection view.
   *
   * @param <O> Original collection element type
   * @param <V> View collection element type
   */
  class CollectionView<O, V> extends AbstractCollection<V> {

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


  final class MappedIterator<O, V> implements Iterator<V> {

    private final @NotNull Iterator<? extends O> iterator;

    private final @NotNull Function<? super O, @Nullable V> view;

    public MappedIterator(@NotNull Iterator<? extends O> iterator, @NotNull Function<? super O, V> view) {
      this.iterator = iterator;
      this.view = view;
    }

    @Override
    public boolean hasNext() { return iterator.hasNext(); }

    @Override
    public @Nullable V next() { return view.apply(iterator.next()); }

  }


  /**
   * Unmodifiable mapped list view. Optimized for random access original lists.
   *
   * @param <O> Original list element type
   * @param <V> View list element type
   */
  final class ListView<O, V> extends AbstractList<V> {

    private final @NotNull List<? extends O> original;

    private final @NotNull Function<O, V> view;

    public ListView(@NotNull List<? extends O> original, @NotNull Function<O, V> view) {
      this.original = original;
      this.view = view;
    }

    @Override
    public V get(int index) {
      return view.apply(original.get(index));
    }

    @Override
    public int size() { return original.size(); }

  }


}
