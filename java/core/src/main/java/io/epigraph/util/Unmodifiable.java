/* Created by yegor on 7/22/16. */

package io.epigraph.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
  public static @NotNull <T> BinaryOperator<T> throwingMerger() {
    return (oldValue, newValue) -> { throw new IllegalStateException(); };
  }

  @Contract(pure = true)
  public static @NotNull <E> Collection<? extends E> collection(@NotNull Collection<? extends E> collection) {
    return UnmodifiableCollectionClass.isInstance(collection)
        ? collection
        : Collections.unmodifiableCollection(collection);
  }

  @Contract(pure = true)
  public static @NotNull <E> List<? extends E> list(@NotNull List<? extends E> list) {
    return UnmodifiableCollectionClass.isInstance(list)
        ? list
        : Collections.unmodifiableList(list);
  }

  @Contract(pure = true)
  public static @NotNull <O, E> List<? extends E> list(
      @NotNull Collection<O> collection,
      @NotNull Function<? super O, ? extends E> elementMapper
  ) {
    List<E> list = new ArrayList<>(collection.size());
    for (O element : collection) list.add(elementMapper.apply(element));
    return Unmodifiable.list(list);
    // return Unmodifiable.list(collection.stream().map(elementMapper).collect(Collectors.toList())); // doesn't start with empty list of required size
  }

  @Contract(pure = true)
  public static @NotNull <E> Supplier<ArrayList<E>> arrayList(int size) { // TODO arrayListSupplier?
    return () -> new ArrayList<E>(size);
  }

  @Contract(pure = true)
  public static @NotNull <K, V> Map<K, ? extends V> map(@NotNull Map<K, ? extends V> map) {
    for (Class<?> unmodifiableMapClass : UnmodifiableMapClasses) if (unmodifiableMapClass.isInstance(map)) return map;
    return Collections.unmodifiableMap(map);
  }

  @Contract(pure = true)
  public static @NotNull <O, K, V> Map<K, ? extends V> map(
      @NotNull Collection<? extends O> collection,
      @NotNull Function<? super O, ? extends K> keyMapper,
      @NotNull Function<? super O, ? extends V> valueMapper
  ) {
    return Unmodifiable.map(collection.stream().collect(
        Collectors.toMap(keyMapper, valueMapper, Unmodifiable.throwingMerger(), Unmodifiable.hashMap(collection.size()))
    ));
  }

  @Contract(pure = true)
  public static @NotNull <K, V> Supplier<HashMap<K, V>> hashMap(int size) { // TODO hashMapSupplier?
    return () -> new HashMap<>(hashMapCapacity(size), 0.75f);
  }

  @Contract(pure = true)
  public static int hashMapCapacity(int size) { return (size * 4 + 2) / 3; } // TODO make sure arithmetic is correct

}
